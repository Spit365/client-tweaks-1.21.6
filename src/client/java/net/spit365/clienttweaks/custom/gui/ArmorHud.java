package net.spit365.clienttweaks.custom.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.manager.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class ArmorHud {
	public static final String ARMOR_HUD_ID = "armor_hud";
	public static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	public static void init() {
		HudElementRegistry.addFirst(Identifier.of(ClientTweaks.MOD_ID, ARMOR_HUD_ID), (context, tickCounter) -> {
			MinecraftClient instance = MinecraftClient.getInstance();
			ClientPlayerEntity player = instance.player;
			if (player == null) return;
			PlayerInventory inventory = player.getInventory();

			if (Boolean.parseBoolean(getArmorHudOption("enabled_armor"))) renderArmorHud(context, inventory, instance);
			if (Boolean.parseBoolean(getArmorHudOption("enabled_arrows"))) renderArrows(context, inventory, instance);
		});
	}

	private static void renderArmorHud(DrawContext context, PlayerInventory inventory, MinecraftClient client) {
		ArmorData data = computeArmorData(inventory);
		ArmorHudPos armorHudPos = getArmorHudPos();
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			ItemStack equipped = data.equipped().get(slot);
			ItemStack best = data.bestUpgrade().get(slot);
			ArmorHudPos.UiPos uiPos = ArmorHudPos.getFromScaled(armorHudPos.armorPos,  context.getScaledWindowWidth(), context.getScaledWindowHeight(), slot);
			if (uiPos != null) {
				if (best != null) {
					context.drawItem(best, uiPos.x() - 20, uiPos.y());
					context.drawStackOverlay(client.textRenderer, best, uiPos.x() - 20, uiPos.y());
				}
				if (!equipped.isEmpty()) {
					context.drawItem(equipped, uiPos.x(), uiPos.y());
					context.drawStackOverlay(client.textRenderer, equipped, uiPos.x(), uiPos.y());
				}
			}
		}
	}

	private static void renderArrows(DrawContext context, PlayerInventory inventory, MinecraftClient mc) {
		if (!inventory.contains(s -> s.isIn(ItemTags.ARROWS))) return;
		LinkedHashMap<String, ArrowAgg> groups = computeArrowGroups(inventory);
		ArmorHudPos armorHudPos = getArmorHudPos();
		int i = 0;
		ArmorHudPos.UiPos uiPos = ArmorHudPos.getFromScaled(armorHudPos.arrowStart,  context.getScaledWindowWidth(), context.getScaledWindowHeight(), null);
		if (uiPos != null) for (Map.Entry<String, ArrowAgg> e : groups.entrySet()) {
				int x = uiPos.x() + switch (armorHudPos.arrowDirection) {
					case LEFT -> -i;
					case RIGHT -> i;
					default -> 0;
				};
				int y = uiPos.y() + switch (armorHudPos.arrowDirection) {
					case UP -> -i;
					case DOWN -> i;
					default -> 0;
				};
				ItemStack icon = e.getValue().icon();
				String text = String.valueOf(e.getValue().count());
				context.drawItem(icon, x, y);
				context.drawText(mc.textRenderer, text, x - 5 - 5 * text.length(), y + 5, Colors.WHITE, true);
				i += armorHudPos.arrowStep;
		}
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

	public static JSONObject getArmorHudOptions() {
		return ConfigManager.read(ConfigManager.file(), ARMOR_HUD_ID);
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
		ConfigManager.write(ARMOR_HUD_ID, options);
	}

	private record ArmorData(Map<EquipmentSlot, ItemStack> equipped, Map<EquipmentSlot, ItemStack> bestUpgrade) {}

	private static ArmorData computeArmorData(PlayerInventory inventory) {
		Map<EquipmentSlot, ItemStack> equippedMap = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS)
			equippedMap.put(slot, inventory.getStack(slot.getOffsetEntitySlotId(36)));
		List<ItemStack> candidates = new ArrayList<>();
		outer:
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack s = inventory.getStack(i);
			if (s.isEmpty()) continue;
			EquippableComponent eq = s.get(DataComponentTypes.EQUIPPABLE);
			AttributeModifiersComponent am = s.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
			if (eq == null || am == null) continue;
			if (!ArmorHud.ARMOR_SLOTS.contains(eq.slot())) continue;
			for (ItemStack worn : equippedMap.values())
				if (ItemStack.areItemsAndComponentsEqual(worn, s)) continue outer;
			candidates.add(s);
		}

		Map<EquipmentSlot, ItemStack> best = new EnumMap<>(EquipmentSlot.class);

		final BiFunction<ItemStack, EquipmentSlot, Map<String, Double>> toAttrMap = (stack, slot) -> {
			Map<String, Double> map = new HashMap<>();
			if (stack == null || stack.isEmpty()) return map;
			AttributeModifiersComponent attrs = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
			if (attrs == null) return map;
			for (AttributeModifiersComponent.Entry e : attrs.modifiers()) {
				if (e.modifier().operation() != EntityAttributeModifier.Operation.ADD_VALUE) continue;
				if (e.slot() != null && !e.slot().matches(slot)) continue;
				String key;
				try {
					key = String.valueOf(Objects.requireNonNull(Registries.ATTRIBUTE.getId(e.attribute().value())));
				} catch (Throwable ignore) {
					key = String.valueOf(e.modifier().id());
				}
				map.merge(key, e.modifier().value(), Double::sum);
			}
			return map;
		};

		final BiFunction<Map<String, Double>, Map<String, Double>, double[]> compare = (cand, eq) -> {
			int better = 0, worse = 0;
			double sumDelta = 0.0;
			Set<String> keys = new HashSet<>(cand.keySet());
			keys.addAll(eq.keySet());
			for (String k : keys) {
				double d = cand.getOrDefault(k, 0.0) - eq.getOrDefault(k, 0.0);
				sumDelta += d;
				if (d > 0) better++;
				else if (d < 0) worse++;
			}
			return new double[]{better, -worse, sumDelta, cand.size()};
		};

		for (EquipmentSlot slot : ArmorHud.ARMOR_SLOTS) {
			ItemStack equipped = equippedMap.get(slot);
			Map<String, Double> eqMap = toAttrMap.apply(equipped, slot);
			ItemStack bestStack = null;
			double[] bestScore = null;

			for (ItemStack cand : candidates) {
				EquippableComponent eq = cand.get(DataComponentTypes.EQUIPPABLE);
				if (eq == null || eq.slot() != slot) continue;
				Map<String, Double> candMap = toAttrMap.apply(cand, slot);
				double[] score = compare.apply(candMap, eqMap);
				if (score[0] <= 0) continue;
				if (bestStack == null) {
					bestStack = cand;
					bestScore = score;
				} else {
					//0: better, 1: worse, 2: sumDelta, 3: candSize
					boolean greater =
						(score[0] > bestScore[0]) ||
							(score[0] == bestScore[0] && score[1] > bestScore[1]) ||
							(score[0] == bestScore[0] && score[1] == bestScore[1] && score[2] > bestScore[2]) ||
							(score[0] == bestScore[0] && score[1] == bestScore[1] && score[2] == bestScore[2] && score[3] > bestScore[3]);

					if (greater) {
						bestStack = cand;
						bestScore = score;
					}
				}
			}
			if (bestStack != null) best.put(slot, bestStack);
		}

		return new ArmorData(equippedMap, best);
	}

	private record ArrowAgg(ItemStack icon, int count) {
	}

	private static LinkedHashMap<String, ArrowAgg> computeArrowGroups(PlayerInventory inventory) {
		LinkedHashMap<String, ArrowAgg> result = new LinkedHashMap<>();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack s = inventory.getStack(i);
			if (s.isEmpty() || !s.isIn(ItemTags.ARROWS)) continue;

			Item item = s.getItem();
			String base = Objects.requireNonNull(Registries.ITEM.getId(item)).toString();
			String key = base;
			if (item instanceof TippedArrowItem) {
				StringBuilder sb = new StringBuilder(base);
				PotionContentsComponent pc = s.get(DataComponentTypes.POTION_CONTENTS);
				if (pc != null) {
					pc.potion().ifPresent(p -> sb.append("|").append(Registries.POTION.getId(p.value())));
					for (StatusEffectInstance eff : pc.customEffects()) {
						sb.append("|").append(Registries.STATUS_EFFECT.getId(eff.getEffectType().value()))
							.append(":").append(eff.getAmplifier());
					}
				}
				key = sb.toString();
			}

			ArrowAgg cur = result.get(key);
			if (cur == null) result.put(key, new ArrowAgg(s, s.getCount()));
			else result.put(key, new ArrowAgg(cur.icon(), cur.count() + s.getCount()));
		}
		return result;
	}
}
