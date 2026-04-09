package net.spit365.clienttweaks.config;

import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.gui.ArmorHud;
import net.spit365.clienttweaks.util.ConfigManager;

public class ArmorHudConfig {
    public static final String ARMOR_HUD_ID = "armor_hud";

    public static JSONObject getArmorHudOptions() {
		return ConfigManager.read(ConfigManager.file(), ARMOR_HUD_ID);
	}

	public static boolean isEnabled(String key) {
		JSONObject parent = getArmorHudOptions();
		if (parent == null || !parent.containsKey("show_" + key)) return false;
		if (parent.get("show_" + key) instanceof String string) return "true".equals(string);
		return false;
	}

	public static ArmorHud.ArmorHudRenderer getArmorHudRenderer() {
		JSONObject parent = getArmorHudOptions();
		if (parent == null || !parent.containsKey("pos")) return ArmorHud.ArmorHudRenderer.TOP_RIGHT;
        ArmorHud.ArmorHudRenderer armorHudRenderer = null;
        try {
            armorHudRenderer = ArmorHud.ArmorHudRenderer.valueOf((String) parent.get("pos"));
        } catch (Exception e) {
            ClientTweaks.LOGGER.error(e.getMessage());
        }
        if (armorHudRenderer != null) return armorHudRenderer;
        return ArmorHud.ArmorHudRenderer.TOP_RIGHT;
	}

	public static void writeArmorHudOption(String key, String value) {
		JSONObject options = getArmorHudOptions();
		options.put(key, value);
		ConfigManager.write(ARMOR_HUD_ID, options);
	}


}
