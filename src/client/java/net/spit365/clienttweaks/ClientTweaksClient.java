package net.spit365.clienttweaks;

import net.fabricmc.api.ClientModInitializer;
import net.spit365.clienttweaks.mod.ClientCommands;
import net.spit365.clienttweaks.mod.ClientKeybinds;
import net.spit365.clienttweaks.mod.ClientTick;
import net.spit365.clienttweaks.skillissue.SkillIssue;

public class ClientTweaksClient implements ClientModInitializer {

	@Override public void onInitializeClient() {
		SkillIssue.init();
		ClientCommands.init();
		ClientKeybinds.init();
		ClientTick.init();
	}
}