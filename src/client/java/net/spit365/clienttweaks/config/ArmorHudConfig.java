package net.spit365.clienttweaks.config;

import net.minecraft.entity.EquipmentSlot;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.gui.ArmorHud;
import net.spit365.clienttweaks.util.ConfigManager;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.function.BiConsumer;

public class ArmorHudConfig {
	public static JSONObject getArmorHudOptions() {
		return ConfigManager.read(ConfigManager.file(), ArmorHud.ARMOR_HUD_ID);
	}

	public static boolean isEnabled(String key) {
		JSONObject parent = getArmorHudOptions();
		if (parent == null || !parent.containsKey("enabled_" + key)) return false;
		if (parent.get("enabled_" + key) instanceof String string) return "true".equals(string);
		return false;
	}

	public static ArmorHudRenderer getArmorHudRenderer() {
		JSONObject parent = getArmorHudOptions();
		if (parent == null || !parent.containsKey("pos")) return ArmorHudRenderer.TOP_RIGHT;
        ArmorHudRenderer armorHudRenderer = null;
        try {
            armorHudRenderer = ArmorHudRenderer.valueOf((String) parent.get("pos"));
        } catch (Exception e) {
            ClientTweaks.LOGGER.error(e.getMessage());
        }
        if (armorHudRenderer != null) return armorHudRenderer;
        return ArmorHudRenderer.TOP_RIGHT;
	}

	public static void writeArmorHudOption(String key, String value) {
		JSONObject options = getArmorHudOptions();
		options.put(key, value);
		ConfigManager.write(ArmorHud.ARMOR_HUD_ID, options);
	}

    public enum ArmorHudRenderer {
	    TOP_RIGHT((arrowGroups, arrowIconRenderConsumer, windowSize) -> {
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArmorHud.ArrowGroup group = arrowGroups.get(i);
                arrowIconRenderConsumer.accept(new UiPos(windowSize.x - 20, 20 * (i + 4)), group);
            }
        }, (armorIconRenderConsumer, windowSize) -> {
            int windowWidth = windowSize.x - 20;
            for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS) {
                armorIconRenderConsumer.accept(
                    switch (slot){
                        case HEAD -> new UiPos(windowWidth, 0);
                        case CHEST -> new UiPos(windowWidth, 20);
                        case LEGS -> new UiPos(windowWidth, 40);
                        case FEET -> new UiPos(windowWidth, 60);
                        default -> throw new IllegalStateException("Unexpected value: " + slot);
                    }, slot
                );
            }
        }),
		TOP_LEFT((arrowGroups, arrowIconRenderConsumer, uiPos) -> {
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArmorHud.ArrowGroup group = arrowGroups.get(i);
                arrowIconRenderConsumer.accept(new UiPos(0, 20 * (i + 4)), group);
            }
        }, (armorIconRenderConsumer, uiPos) -> {
            for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS) {
                armorIconRenderConsumer.accept(
                    switch (slot){
                        case HEAD -> new UiPos(0, 0);
                        case CHEST -> new UiPos(0, 20);
                        case LEGS -> new UiPos(0, 40);
                        case FEET -> new UiPos(0, 60);
                        default -> throw new IllegalStateException("Unexpected value: " + slot);
                    }, slot
                );
            }
        }),
		BOTTOM_RIGHT((arrowGroups, arrowIconRenderConsumer, uiPos) -> {
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArmorHud.ArrowGroup group = arrowGroups.get(i);
                arrowIconRenderConsumer.accept(new UiPos(uiPos.x - 20, uiPos.y - (20 * (i + 1))), group);
            }
        }, (armorIconRenderConsumer, uiPos) -> {
            int x = uiPos.x - 20;
            int y = uiPos.y - 20;
            for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS) {
                armorIconRenderConsumer.accept(
                    switch (slot){
                        case HEAD -> new UiPos(x, y);
                        case CHEST -> new UiPos(x, y - 20);
                        case LEGS -> new UiPos(x, y - 40);
                        case FEET -> new UiPos(x, y - 60);
                        default -> throw new IllegalStateException("Unexpected value: " + slot);
                    }, slot
                );
            }
        }),
		BOTTOM_LEFT((arrowGroups, arrowIconRenderConsumer, uiPos) -> {
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArmorHud.ArrowGroup group = arrowGroups.get(i);
                arrowIconRenderConsumer.accept(new UiPos(0, uiPos.y - (20 * (i + 1))), group);
            }
        }, (armorIconRenderConsumer, uiPos) -> {
            int y = uiPos.y - 20;
            for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS) {
                armorIconRenderConsumer.accept(
                    switch (slot){
                        case HEAD -> new UiPos(0, y);
                        case CHEST -> new UiPos(0, y - 20);
                        case LEGS -> new UiPos(0, y - 40);
                        case FEET -> new UiPos(0, y - 60);
                        default -> throw new IllegalStateException("Unexpected value: " + slot);
                    }, slot
                );
            }
        }),
		HOTBAR((arrowGroups, arrowIconRenderConsumer, uiPos) -> {
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArmorHud.ArrowGroup group = arrowGroups.get(i);
                arrowIconRenderConsumer.accept(new UiPos(uiPos.x - 20, 20 * (i + 4)), group);

            }
        }, (armorIconRenderConsumer, uiPos) -> {
            int x = uiPos.x - 20;
            for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS) {
                armorIconRenderConsumer.accept(
                    switch (slot){
                        case HEAD -> new UiPos(x / 4 - 20, x - 40);
                        case CHEST -> new UiPos(x / 4 - 20, x - 20);
                        case LEGS -> new UiPos(x * 3 / 4 - 20, x - 40);
                        case FEET -> new UiPos(x * 3 / 4 - 20, x - 20);
                        default -> throw new IllegalStateException("Unexpected value: " + slot);
                    }, slot
                );
            }
        });

        public final TriConsumer<List<ArmorHud.ArrowGroup>, ArrowIconRenderConsumer, UiPos> arrowRenderer;
        public final BiConsumer<ArmorIconRenderConsumer, UiPos> armorHudRender;

        ArmorHudRenderer(TriConsumer<List<ArmorHud.ArrowGroup>, ArrowIconRenderConsumer, UiPos> arrowRenderer, BiConsumer<ArmorIconRenderConsumer, UiPos> armorHudRender){
            this.arrowRenderer = arrowRenderer;
            this.armorHudRender = armorHudRender;
        }

        public record UiPos(int x, int y) {}

        @FunctionalInterface public interface ArmorIconRenderConsumer {
            void accept(UiPos pos, EquipmentSlot slot);
        }
        @FunctionalInterface public interface ArrowIconRenderConsumer {
            void accept(UiPos pos, ArmorHud.ArrowGroup group);
        }
    }


}
