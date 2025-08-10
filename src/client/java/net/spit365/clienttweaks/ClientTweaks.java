package net.spit365.clienttweaks;

import net.fabricmc.api.ClientModInitializer;
import net.spit365.clienttweaks.mod.ClientCommands;
import net.spit365.clienttweaks.mod.ClientTick;
import net.spit365.clienttweaks.mod.ModParticles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTweaks implements ClientModInitializer {

	public static final String MOD_ID = "client-tweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override public void onInitializeClient() {
		ClientCommands.init();
		ClientTick.init();
		ModParticles.init();
	}
}