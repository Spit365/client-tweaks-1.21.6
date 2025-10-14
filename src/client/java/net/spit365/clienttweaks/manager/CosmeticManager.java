package net.spit365.clienttweaks.manager;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.spit365.clienttweaks.ClientTweaks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class CosmeticManager {
    public static final String ASSETS_FOLDER = ClientTweaks.CONFIG_FOLDER + "assets/";
    public static JSONObject[] loadedCustomCosmetics;

    public static JSONObject[] getCustomCosmetics(){
        try (Stream<Path> paths = Files.walk(Paths.get(ASSETS_FOLDER))) {
            return paths.map(path -> {
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) stringBuilder.append(line);
                    if (!stringBuilder.toString().isEmpty())
                        return (JSONObject) JSONValue.parseWithException(stringBuilder.toString());
                } catch (Exception ignored){}
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
        JSONObject cosmetics = CosmeticManager.getEnabledCosmetics();
        cosmetics.put(id, cosmetic);
        writeCosmetics(cosmetics);
    }

    public static void init(){
        loadedCustomCosmetics = getCustomCosmetics();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            public static final Identifier ID = Identifier.of(ClientTweaks.MOD_ID, "custom_cosmetics");
            @Override public Identifier getFabricId() {return ID;}
            @Override public void reload(ResourceManager manager) {loadedCustomCosmetics = getCustomCosmetics();}
        });
    }
}
