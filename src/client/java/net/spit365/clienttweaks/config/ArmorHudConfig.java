package net.spit365.clienttweaks.config;

import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.entity.EquipmentSlot;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.gui.ArmorHud;
import net.spit365.clienttweaks.gui.ConfigManager;
import org.jetbrains.annotations.Nullable;

public class ArmorHudConfig {
	public static JSONObject getArmorHudOptions() {
		return ConfigManager.read(ConfigManager.file(), ArmorHud.ARMOR_HUD_ID);
	}

	public static String getArmorHudOption(String key) {
		JSONObject parent = getArmorHudOptions();
		if (parent == null || !parent.containsKey(key)) return "";
		if (parent.get(key) instanceof String string) return string;
		return "";
	}

	public static ArmorHudPos getArmorHudPos() {
		JSONObject parent = getArmorHudOptions();
		if (parent == null || !parent.containsKey("pos")) return ArmorHudPos.TOP_RIGHT;
		if (parent.get("pos") instanceof String string) {
			ArmorHudPos armorHudPos = ArmorHudPos.valueOf(string);
			if (armorHudPos.armorPos != null) return armorHudPos;
		}
		return ArmorHudPos.TOP_RIGHT;
	}

	public static void writeArmorHudOption(String key, String value) {
		JSONObject options = getArmorHudOptions();
		options.put(key, value);
		ConfigManager.write(ArmorHud.ARMOR_HUD_ID, options);
	}

	public enum ArmorHudPos {
		TOP_RIGHT((armorSlot, windowWidth, windowHeight) -> new UiPos(windowWidth, 80), NavigationDirection.DOWN, 20, (armorSlot, windowWidth, windowHeight) -> switch (armorSlot) {
			case HEAD -> new UiPos(windowWidth, 0);
			case CHEST -> new UiPos(windowWidth, 20);
			case LEGS -> new UiPos(windowWidth, 40);
			case FEET -> new UiPos(windowWidth, 60);
			default -> null;
		}),
		TOP_LEFT((armorSlot, windowWidth, windowHeight) -> new UiPos(20, 80), NavigationDirection.DOWN, 20, (armorSlot, windowWidth, windowHeight) -> switch (armorSlot) {
			case HEAD -> new UiPos(0, 0);
			case CHEST -> new UiPos(0, 20);
			case LEGS -> new UiPos(0, 40);
			case FEET -> new UiPos(0, 60);
			default -> null;
		}),
		BOTTOM_RIGHT((armorSlot, windowWidth, windowHeight) -> new UiPos(windowWidth, windowHeight - 80), NavigationDirection.UP, 20, (armorSlot, windowWidth, windowHeight) -> switch (armorSlot) {
			case HEAD -> new UiPos(windowWidth, windowHeight);
			case CHEST -> new UiPos(windowWidth, windowHeight - 20);
			case LEGS -> new UiPos(windowWidth, windowHeight - 40);
			case FEET -> new UiPos(windowWidth, windowHeight - 60);
			default -> null;
		}),
		BOTTOM_LEFT((armorSlot, windowWidth, windowHeight) -> new UiPos(20, windowHeight - 80), NavigationDirection.UP, 20, (armorSlot, windowWidth, windowHeight) -> switch (armorSlot) {
			case HEAD -> new UiPos(0, windowHeight);
			case CHEST -> new UiPos(0, windowHeight - 20);
			case LEGS -> new UiPos(0, windowHeight - 40);
			case FEET -> new UiPos(0, windowHeight - 60);
			default -> null;
		}),
		HOTBAR((armorSlot, windowWidth, windowHeight) -> new UiPos(windowWidth, 80), NavigationDirection.DOWN, 20, (armorSlot, windowWidth, windowHeight) -> switch (armorSlot) {
			case HEAD -> new UiPos(windowWidth / 4, windowHeight - 20);
			case CHEST -> new UiPos(windowWidth / 4, windowHeight);
			case LEGS -> new UiPos(windowWidth * 3 / 4, windowHeight  - 20);
			case FEET -> new UiPos(windowWidth * 3 / 4, windowHeight);
			default -> null;
		});

		public final ScaledUiPos armorPos;
		public final ScaledUiPos arrowStart;
		public final NavigationDirection arrowDirection;
		public final int arrowStep;

		ArmorHudPos(ScaledUiPos arrowStart, NavigationDirection arrowDirection, int arrowStep, ScaledUiPos armorPos) {
			this.arrowStep = arrowStep;
			this.arrowDirection = arrowDirection;
			this.arrowStart = arrowStart;
			this.armorPos = armorPos;
		}

		public static @Nullable UiPos getFromScaled(ScaledUiPos pos, int width, int height, EquipmentSlot slot){
			UiPos original = pos.get(slot, width - 40, height - 40);
			if (original != null)
				return new UiPos(original.x() + 20, original.y() + 20);
			return null;
		}

		public record UiPos(int x, int y) {}

		@FunctionalInterface
		public interface ScaledUiPos {
			UiPos get(EquipmentSlot armorSlot, int windowWidth, int windowHeight);
		}
	}
}
