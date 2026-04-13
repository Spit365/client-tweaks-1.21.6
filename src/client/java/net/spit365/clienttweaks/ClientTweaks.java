package net.spit365.clienttweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.config.BoxOutlineConfig;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import net.spit365.clienttweaks.gui.ArmorHud;
import net.spit365.clienttweaks.mod.ClientCommands;
import net.spit365.clienttweaks.mod.ClientTick;
import net.spit365.clienttweaks.mod.ModParticles;
import net.spit365.clienttweaks.renderer.BoxOutlineRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTweaks implements ClientModInitializer {
	public static final String MOD_ID = "client-tweaks";
    public static final String CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir() + "/" + MOD_ID + "/";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override public void onInitializeClient() {
		ClientCommands.init();
		ArmorHud.init();
		ClientTick.init();
		ModParticles.init();
        CosmeticsConfig.init();
		BoxOutlineRenderer.init();

		updateConfig();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			public static final Identifier ID = Identifier.of(ClientTweaks.MOD_ID, "general");
			@Override public Identifier getFabricId() { return ID; }
			@Override public void reload(ResourceManager manager) {
				updateConfig();
			}
		});
	}

	public static void updateConfig() {
		CosmeticsConfig.updateCosmetics();
		BoxOutlineConfig.updateCachedEdges();
	}
}