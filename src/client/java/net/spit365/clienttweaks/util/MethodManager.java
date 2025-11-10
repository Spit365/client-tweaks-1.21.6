package net.spit365.clienttweaks.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.spit365.clienttweaks.ClientTweaks;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.CodeSource;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Runtime source manager for a single Java class (Methods.java)
 * - Ensures the source file exists
 * - Allows adding/removing static void methods by name
 * - Compiles with ECJ against the game classpath (+ client-named.jar if present)
 * - Reflectively invokes a selected method
 *
 * This version logs very verbosely to help diagnose classpath/missing file issues.
 */
public final class MethodManager {

	/* ====== Config ====== */

	// IMPORTANT: This must match the package declaration we write into Methods.java
	// You currently keep Methods.java in the default (simple) package "clienttweaks"
	private static final String packageName = "clienttweaks";

	private static final String className = "Methods";
	private static final Path baseDir = FabricLoader.getInstance().getGameDir().resolve(ClientTweaks.MOD_ID);
	private static final Path sourceFile = baseDir.resolve(className + ".java");

	private static long lastSourceModified = -1L;
	private static Class<?> lastClass = null;

	private static final Pattern classClose = Pattern.compile("\\n?}\\s*$");

	/* ====== File bootstrap ====== */

	public static void ensureMethodsFile() {
		try {
			Files.createDirectories(baseDir);
			if (!Files.exists(sourceFile)) {
				String initial = """
                    package %s;
                    
                    public class %s {
                        // Add your methods below. Signature must be:
                        // public static final void methodName()
                    }
                    """.formatted(packageName, className);
				Files.writeString(sourceFile, initial, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
				System.out.println("Created " + sourceFile);
			} else {
				System.out.println("Found " + sourceFile);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/* ====== API to edit Methods.java ====== */

	private static String unquoteAndUnescape(String s) {
		if (s == null) return "";
		s = s.trim();
		if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
			s = s.substring(1, s.length() - 1);
		}
		s = s.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r")
			.replace("\\\"", "\"").replace("\\'", "'");
		return s;
	}

	public static void addMethodFromFile(String name, String filePath) {
		try {
			String body = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
			addMethodFromCode(name, body);
		} catch (IOException e) {
			throw new RuntimeException("Reading source failed: " + e.getMessage(), e);
		}
	}

	public static void addMethodFromCode(String name, String codeBody) {
		try {
			codeBody = unquoteAndUnescape(codeBody);
			String src = Files.readString(sourceFile, StandardCharsets.UTF_8);
			src = removeMethodInternal(src, name);

			Matcher m = classClose.matcher(src);
			if (!m.find()) throw new IllegalStateException("Class closing brace not found.");
			int insert = m.start();

			String method = """
                
                public static final void %s() {
                    %s
                }
                """.formatted(name, codeBody);

			String updated = src.substring(0, insert) + method + src.substring(insert);
			Files.writeString(sourceFile, updated, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
			System.out.println("Added/updated method: " + name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean removeMethod(String name) {
		try {
			String src = Files.readString(sourceFile, StandardCharsets.UTF_8);
			String updated = removeMethodInternal(src, name);
			boolean changed = !updated.equals(src);
			if (changed) {
				Files.writeString(sourceFile, updated, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
				System.out.println("Removed method: " + name);
			} else {
				System.out.println("Method not found (no change): " + name);
			}
			return changed;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String removeMethodInternal(String src, String name) {
		String sig = "public static final void " + name + "()";
		int i = src.indexOf(sig);
		if (i < 0) return src;
		int braceStart = src.indexOf('{', i);
		if (braceStart < 0) return src;
		int depth = 0;
		int j = braceStart;
		while (j < src.length()) {
			char c = src.charAt(j++);
			if (c == '{') depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0) break;
			}
		}
		if (depth != 0) return src;
		int start = src.lastIndexOf('\n', i);
		if (start < 0) start = 0;
		int end = j;
		if (end < src.length() && src.charAt(end) == '\n') end++;
		return src.substring(0, start) + src.substring(end);
	}

	/* ====== Classpath helpers ====== */

	private static String pathOf(CodeSource cs) {
		if (cs == null || cs.getLocation() == null) return null;
		try { return Paths.get(cs.getLocation().toURI()).toString(); }
		catch (URISyntaxException e) { return null; }
	}

	private static List<Path> findNamedMinecraftJars() {
		List<Path> found = new ArrayList<>();
		Path gameDir = FabricLoader.getInstance().getGameDir();
		Path remappedDir = gameDir.resolve(".fabric").resolve("remappedJars");
		if (Files.isDirectory(remappedDir)) {
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(remappedDir, "*-named.jar")) {
				for (Path p : ds) found.add(p.toAbsolutePath());
			} catch (IOException ignored) {}
		}
		Path loom = Paths.get(System.getProperty("user.home"), ".gradle", "caches", "fabric-loom");
		if (Files.isDirectory(loom)) {
			try (java.util.stream.Stream<Path> s = Files.walk(loom, 7)) {
				s.filter(p -> {
					String n = p.getFileName().toString();
					return n.endsWith("-named.jar") && n.contains("client");
				}).forEach(p -> found.add(p.toAbsolutePath()));
			} catch (IOException ignored) {}
		}
		return found;
	}

	public static String buildRuntimeClasspath() {
		LinkedHashSet<String> classPath = new LinkedHashSet<>();
		String sys = System.getProperty("java.class.path");
		if (sys != null && !sys.isBlank()) for (String e : sys.split(File.pathSeparator)) if (!e.isBlank()) classPath.add(e);
		
		ClassLoader cl = MethodManager.class.getClassLoader();
		if (cl instanceof URLClassLoader ucl) {
			for (URL url : ucl.getURLs()) {
				try { classPath.add(Paths.get(url.toURI()).toString()); }
				catch (URISyntaxException ignored) {}
			}
		}
		classPath.add(pathOf(net.fabricmc.loader.api.FabricLoader.class.getProtectionDomain().getCodeSource()));
		try {
			classPath.add(pathOf(net.minecraft.client.MinecraftClient.class.getProtectionDomain().getCodeSource()));
		} catch (Throwable ignored) {}
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModOrigin origin = mod.getOrigin();
			try {
				if (origin.getKind() == ModOrigin.Kind.NESTED) continue;
				origin.getPaths().forEach(p -> classPath.add(p.toAbsolutePath().toString()));
			} catch (UnsupportedOperationException ignored) {}
		}
		for (Path p : findNamedMinecraftJars()) {
			String s = p.toString();
			classPath.add(s);
		}
		return classPath.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(File.pathSeparator));
	}
	public static String compileAndExecute(String methodName) {
		try {
			if (!Files.exists(sourceFile)) {
				System.out.println("ERROR: Missing source file: " + sourceFile);
				return "Compile failed:\nERROR: java.lang.IllegalArgumentException: File " + sourceFile + " is missing @ 0:0";
			}
			if (lastSourceModified == Files.getLastModifiedTime(sourceFile).toMillis() && lastClass != null) return invokeExisting(methodName);
			Path binDir = baseDir.resolve("bin").resolve(Long.toString(Instant.now().toEpochMilli()));
			Files.createDirectories(binDir);
			EclipseCompiler compiler = new EclipseCompiler();
			StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
			System.out.println("Compiling source file: " + sourceFile);
			Iterable<? extends JavaFileObject> units = fm.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile.toFile()));
			List<String> options = List.of(
				"-d", binDir.toAbsolutePath().toString(),
				"-classpath", buildRuntimeClasspath(),
				"-source", "21",
				"-target", "21",
				"-encoding", "UTF-8",
				"-proc:none",
				"-nowarn"
			);
			List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<>();
			JavaCompiler.CompilationTask task = compiler.getTask(null, fm, diagnostics::add, options, null, units);
			boolean ok = task.call();
			fm.close();

			if (!ok) {
				String msg = diagnostics.stream()
					.map(d -> "ERROR: " + d.getMessage(null)
						+ " @ " + d.getLineNumber() + ":" + d.getColumnNumber())
					.collect(Collectors.joining("\n"));
				System.out.println("Diagnostics:\n" + msg);
				return "Compile failed:\n" + msg;
			}
			URLClassLoader loader = new URLClassLoader(new URL[]{ binDir.toUri().toURL() }, MethodManager.class.getClassLoader());
			Class<?> methods = Class.forName(packageName + "." + className, true, loader);

			lastSourceModified = Files.getLastModifiedTime(sourceFile).toMillis();
			lastClass = methods;

			return invokeExisting(methodName);
		} catch (Throwable t) {
			t.printStackTrace();
			return "Execution error: " + t.getClass().getSimpleName() + ": " + t.getMessage();
		}
	}

	private static String invokeExisting(String methodName) {
		try {
			MethodHandles.publicLookup().findStatic(lastClass, methodName, MethodType.methodType(void.class)).invokeExact();
			return "Executed " + methodName + "() successfully.";
		} catch (NoSuchMethodException nsme) {
			return "Method not found or wrong signature: " + nsme.getMessage();
		} catch (Throwable t) {
			t.printStackTrace();
			return "Execution error: " + t.getClass().getSimpleName() + ": " + t.getMessage();
		}
	}
}
