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

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class CosmeticsConfig {
    public static final String ASSETS_FOLDER = ClientTweaks.CONFIG_FOLDER + "assets/";
    public static JSONObject[] loadedCustomCosmetics;

    public static JSONObject[] getCustomCosmetics(){
        try (Stream<Path> paths = Files.walk(Paths.get(ASSETS_FOLDER), 1)) {
            return paths.filter(Files::isRegularFile).map(path -> {
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) stringBuilder.append(line);
                    if (!stringBuilder.toString().isEmpty())
                        return (JSONObject) JSONValue.parseWithException(stringBuilder.toString());
                } catch (Exception e){
					ClientTweaks.LOGGER.error("Couldn't load custom cosmetics: {}", e.getMessage());
				}
                return null;
            })
            .filter(Objects::nonNull)
            .toArray(JSONObject[]::new);
        } catch (Exception ignored) {}
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
