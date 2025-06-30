package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.spit365.lulasmod.mod.ClientCommands;
import net.spit365.lulasmod.mod.ClientTick;

public class LulasmodClient implements ClientModInitializer {

	@Override public void onInitializeClient() {
		ClientCommands.init();
		ClientTick.init();
	}
}