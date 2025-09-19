package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.ClientTweaks;

import java.util.*;

public class ClientGui {
	private record ItemArmorContext(ItemStack stack, EquippableComponent equippableComponent, AttributeModifiersComponent attributeModifiersComponent) {}
	private record CompareStats(int better, int equal, int worse, double sumDelta, int totalCandMods) {}

	public static void init() {
		HudElementRegistry.addFirst(Identifier.of(ClientTweaks.MOD_ID, "armor_hud"), (context, tickCounter) -> {
			MinecraftClient instance = MinecraftClient.getInstance();
			ClientPlayerEntity player = instance.player;
			if (player == null) return;
			PlayerInventory inventory = player.getInventory();
			int x = context.getScaledWindowWidth() - 20;
			int y = 0;

			y = renderArmorHud(context, inventory, x, y, instance);
			renderArrows(inventory, context, instance, x, y);
		});
	}

	private static int renderArmorHud(DrawContext context, PlayerInventory inventory, int x, int y, MinecraftClient instance) {
		List<EquipmentSlot> armorSlots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
		Map<EquipmentSlot, ItemStack> equippedMap = collectEquipped(inventory, armorSlots);
		List<ItemArmorContext> equippables = collectCandidates(inventory, armorSlots, equippedMap);

		for (EquipmentSlot slot : armorSlots) {
			ItemStack equipped = equippedMap.get(slot);
			ItemArmorContext best = findBestUpgrade(slot, equipped, equippables);
			if (best != null) {
				context.drawItem(best.stack, x - 20, y);
				context.drawStackOverlay(instance.textRenderer, best.stack, x - 20, y);
			}
			if (!equipped.isEmpty()) {
				context.drawItem(equipped, x, y);
				context.drawStackOverlay(instance.textRenderer, equipped, x, y);
			}
			y += 20;
		}
		return y;
	}

	private static Map<EquipmentSlot, ItemStack> collectEquipped(PlayerInventory inventory, List<EquipmentSlot> armorSlots) {
		Map<EquipmentSlot, ItemStack> equippeds = new EnumMap<>(EquipmentSlot.class);
		for (EquipmentSlot slot : armorSlots) equippeds.put(slot, inventory.getStack(slot.getOffsetEntitySlotId(36)));
		return equippeds;
	}
	private static List<ItemArmorContext> collectCandidates(PlayerInventory inventory, List<EquipmentSlot> armorSlots, Map<EquipmentSlot, ItemStack> equippeds) {
		List<ItemArmorContext> equippables = new ArrayList<>();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;
			EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
			AttributeModifiersComponent at = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
			if (equippableComponent == null || at == null) continue;
			if (!armorSlots.contains(equippableComponent.slot())) continue;
			boolean isEquippedStack = equippeds.values().stream().anyMatch(es -> ItemStack.areItemsAndComponentsEqual(es, stack));
			if (isEquippedStack) continue;
			equippables.add(new ItemArmorContext(stack, equippableComponent, at));
		}
		return equippables;
	}
	private static ItemArmorContext findBestUpgrade(EquipmentSlot slot, ItemStack equipped, List<ItemArmorContext> equippables) {
		EquippableComponent equippedEquippable = equipped.get(DataComponentTypes.EQUIPPABLE);
		Map<String, Double> equippedMap = toAttributeMap(equipped, equippedEquippable);
		ItemArmorContext best = null;
		CompareStats bestStats = null;
		for (ItemArmorContext candidate : equippables) {
			if (candidate.equippableComponent.slot() != slot) continue;
			Map<String, Double> candMap = toAttributeMap(candidate.stack, candidate.equippableComponent);
			CompareStats stats = compare(candMap, equippedMap);
			if (stats.better() <= 0) continue;
			if (best == null || betterThan(stats, bestStats)) {
				best = candidate;
				bestStats = stats;
			}
		}
		return best;
	}
	private static void renderArrows(PlayerInventory inventory, DrawContext context, MinecraftClient instance, int x, int y) {
		if (!inventory.contains(s -> s.isIn(ItemTags.ARROWS))) return;

		Map<String, Integer> arrows = new LinkedHashMap<>();
		Map<String, ItemStack> iconStack = new HashMap<>();

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty() || !stack.isIn(ItemTags.ARROWS)) continue;

			String key = arrowKey(stack);
			arrows.merge(key, stack.getCount(), Integer::sum);
			iconStack.putIfAbsent(key, stack);
		}

		for (Map.Entry<String, Integer> e : arrows.entrySet()) {
			ItemStack icon = iconStack.get(e.getKey());


			context.drawItem(icon, x, y);
			String text = String.valueOf(e.getValue());
			context.drawText(instance.textRenderer, text, x - 5 - 5 * text.length(), y + 5, Colors.WHITE, true);
			y += 20;
		}
	}

	private static String arrowKey(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof net.minecraft.item.TippedArrowItem)) return Objects.requireNonNull(Registries.ITEM.getId(item)).toString();

		StringBuilder key = new StringBuilder(Objects.requireNonNull(Registries.ITEM.getId(item)).toString());
		PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
		if (potionContentsComponent != null) {
			potionContentsComponent.potion().ifPresent(potionRegistryEntry -> key.append("|").append(Registries.POTION.getId(potionRegistryEntry.value())));
			for (net.minecraft.entity.effect.StatusEffectInstance effect : potionContentsComponent.customEffects()) {
				key.append("|").append(Registries.STATUS_EFFECT.getId(effect.getEffectType().value()))
					.append(":").append(effect.getAmplifier());
			}
		}
		return key.toString();
	}

	private static Map<String, Double> toAttributeMap(ItemStack stack, EquippableComponent equippableComponent) {
		Map<String, Double> map = new HashMap<>();
		if (stack == null || stack.isEmpty() || equippableComponent == null) return map;
		AttributeModifiersComponent attrs = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		if (attrs == null) return map;
		for (Entry e : attrs.modifiers()) {
			if (e.modifier().operation() != EntityAttributeModifier.Operation.ADD_VALUE) continue;
			if (e.slot() != null && !e.slot().matches(equippableComponent.slot())) continue;
			String key = attributeKey(e);
			map.merge(key, e.modifier().value(), Double::sum);
		}
		return map;
	}
	private static String attributeKey(Entry e) {
		try {
			return Objects.requireNonNull(Registries.ATTRIBUTE.getId(e.attribute().value())).toString();
		} catch (Throwable ignore) {
			return String.valueOf(e.modifier().id());
		}
	}
	private static CompareStats compare(Map<String, Double> cand, Map<String, Double> eq) {
		int better = 0, equal = 0, worse = 0;
		double sumDelta = 0.0;
		Set<String> keys = new HashSet<>(cand.keySet());
		keys.addAll(eq.keySet());
		for (String k : keys) {
			double cv = cand.getOrDefault(k, 0.0);
			double ev = eq.getOrDefault(k, 0.0);
			double d = cv - ev;
			sumDelta += d;
			if (d > 0) better++;
			else if (d < 0) worse++;
			else equal++;
		}
		return new CompareStats(better, equal, worse, sumDelta, cand.size());
	}
	private static boolean betterThan(CompareStats a, CompareStats b) {
		if (b == null) return true;
		if (a.better() != b.better()) return a.better() > b.better();
		if (a.worse() != b.worse()) return a.worse() < b.worse();
		if (Double.compare(a.sumDelta(), b.sumDelta()) != 0) return a.sumDelta() > b.sumDelta();
		return a.totalCandMods() > b.totalCandMods();
	}
}
