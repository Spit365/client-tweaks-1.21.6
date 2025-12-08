package net.spit365.clienttweaks.config;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.util.ConfigManager;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class CosmeticsConfig {
    public static final String ASSETS_FOLDER = ClientTweaks.CONFIG_FOLDER + "assets/";
    public static JSONObject[] loadedCustomCosmetics;

    public static JSONObject[] getCustomCosmetics(){
        try (Stream<Path> paths = Files.list(Paths.get(ASSETS_FOLDER))) {
            return paths.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
				.<JSONObject>mapMulti((path, result) -> {
					try {
						String fileName = path.getFileName().toString();
						JSONObject customCosmetic = new JSONObject();
						customCosmetic.put(fileName.substring(0, fileName.lastIndexOf('.')), Objects.requireNonNull(JSONValue.parseWithException(Files.readString(path, StandardCharsets.UTF_8))));
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

    public static JSONObject getEnabledCosmetics(){
        return ConfigManager.read(ConfigManager.file(), "cosmetics");
    }

    public static JSONObject getEnabledCosmetic(String id){
        return ConfigManager.read(getEnabledCosmetics(), id);
    }
    
    public static void writeCosmetics(JSONObject cosmetics){
        ConfigManager.write("cosmetic", cosmetics);
    }
    public static void writeCosmetic(String id, JSONObject cosmetic){
        JSONObject cosmetics = CosmeticsConfig.getEnabledCosmetics();
        cosmetics.put(id, cosmetic);
        writeCosmetics(cosmetics);
    }

    public static void init(){
        loadCosmetics();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            public static final Identifier ID = Identifier.of(ClientTweaks.MOD_ID, "custom_cosmetics");
            @Override public Identifier getFabricId() {return ID;}
            @Override public void reload(ResourceManager manager) {
				loadCosmetics();
			}
        });
    }

	private static void loadCosmetics() {
		ClientTweaks.LOGGER.error("loading cosmetics");
		loadedCustomCosmetics = getCustomCosmetics();
	}
}
