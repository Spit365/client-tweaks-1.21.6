package net.spit365.clienttweaks.config;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.util.ConfigManager;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.spit365.clienttweaks.util.ConfigManager.read;

public class CosmeticsConfig {
    private static final String ASSETS_FOLDER = ClientTweaks.CONFIG_FOLDER + "cosmetics/";
    private static JSONObject[] loadedCustomCosmetics;
    private static JSONObject enabledCosmetics;

    public static void apply(String target, String cosmeticKey) {
        JSONObject cosmetic = getEnabledCosmetic(cosmeticKey);
        cosmetic.put(target, new JSONObject());
        writeCosmetic(cosmeticKey, cosmetic);
    }

    public static void remove(String target, String cosmeticKey) {
        JSONObject cosmetic = getEnabledCosmetic(cosmeticKey);
        cosmetic.remove(target);
        writeCosmetic(cosmeticKey, cosmetic);
    }

    public static void customize(String targetKey, String cosmeticKey, String optionKey, String optionValue) {
        JSONObject cosmetic = CosmeticsConfig.getEnabledCosmetic(cosmeticKey);
        JSONObject target = read(cosmetic, targetKey);
        target.put(optionKey, optionValue);
        cosmetic.put(targetKey, target);
        CosmeticsConfig.writeCosmetic(cosmeticKey, cosmetic);
    }

    private static JSONObject[] getCustomCosmetics() {
        try (Stream<Path> paths = Files.list(Paths.get(ASSETS_FOLDER))) {
            return paths.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
				.<JSONObject>mapMulti((path, result) -> {
					try {
						String fileName = path.getFileName().toString();
                        JSONObject customCosmetic = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(Files.readString(path, StandardCharsets.UTF_8));
                        customCosmetic.put("name", fileName.substring(0, fileName.lastIndexOf('.')));
						result.accept(customCosmetic);
					} catch (Exception e){
						ClientTweaks.LOGGER.error("Couldn't load custom cosmetics: {}", e.getMessage());
					}
            	}).toArray(JSONObject[]::new);
        } catch (Exception e) {
			ClientTweaks.LOGGER.error("Couldn't parse custom cosmetics folder: {}", e.getMessage());
		}
        return new JSONObject[0];
    }

    public static JSONObject[] getLoadedCustomCosmetics() {
        return loadedCustomCosmetics;
    }

    public static JSONObject getEnabledCosmetics() {
        return enabledCosmetics;
    }

    public static JSONObject getEnabledCosmetic(String id) {
        return ConfigManager.read(enabledCosmetics, id);
    }

    private static void writeCosmetic(String id, JSONObject cosmetic) {
        enabledCosmetics.put(id, cosmetic);
        ConfigManager.write("cosmetics", enabledCosmetics);
    }

	public static void updateCosmetics() {
		loadedCustomCosmetics = getCustomCosmetics();
        enabledCosmetics = ConfigManager.read(ConfigManager.file(), "cosmetics");
	}

    public static void init() {}
}
