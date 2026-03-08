package net.spit365.clienttweaks.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.collection.DefaultedList;
import net.spit365.clienttweaks.config.ArmorHudConfig;
import net.spit365.clienttweaks.mixin.PlayerInventoryAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ArmorHud {
    public static class Layout {
        public static final int Y_SIZE = 80;

        public static int getXSize(int textLength) {
            return textLength * 5 + 40;
        }
    }
    public static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	public static void init() {
        HudRenderCallback.EVENT.register((context, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            PlayerInventory inventory = player.getInventory();
            ArmorHudConfig.ArmorHudRenderer armorHudRenderer = ArmorHudConfig.getArmorHudRenderer();

            if (ArmorHudConfig.isEnabled("armor")) {
                armorHudRenderer.armorHudRender.accept((pos, slot) -> {
                    renderArmorIcon(context, client, computeArmorData(inventory), pos, slot);
                }, new ArmorHudConfig.ArmorHudRenderer.UiPos(context.getScaledWindowWidth(), context.getScaledWindowHeight()));
            }
            if (ArmorHudConfig.isEnabled("arrows") && inventory.contains(s -> s.isIn(ItemTags.ARROWS)))
                armorHudRenderer.arrowRenderer.accept(computeArrowGroups(inventory), (pos, group) -> renderArrowIcon(context, client, group, pos), new ArmorHudConfig.ArmorHudRenderer.UiPos(context.getScaledWindowWidth(), context.getScaledWindowHeight()));
        });
    }

    private static void renderArmorIcon(DrawContext context, MinecraftClient client, ArmorData data, ArmorHudConfig.ArmorHudRenderer.UiPos pos, EquipmentSlot slot) {
        ItemStack equipped = data.equipped().get(slot);
        int textLength = 0;
        if (!equipped.isEmpty()) {
            String durability = String.valueOf(equipped.getMaxDamage() - equipped.getDamage());
            textLength = durability.length();
            context.drawText(client.textRenderer, Text.literal(durability), pos.x(), pos.y() + 5, Colors.WHITE, true);
            context.drawItem(equipped, pos.x() + 25 + 5 * textLength, pos.y());
            context.drawItemInSlot(client.textRenderer, equipped, pos.x() + 25 + 5 * textLength, pos.y());
        }
        ItemStack best = data.bestUpgrade().get(slot);
        if (best != null) {
            context.drawItem(best, pos.x() + 5 + 5 * textLength, pos.y());
            context.drawItemInSlot(client.textRenderer, best, pos.x() + 5 + 5 * textLength, pos.y());
        }
    }

    private static void renderArrowIcon(DrawContext context, MinecraftClient mc, ArrowGroup arrowGroups, ArmorHudConfig.ArmorHudRenderer.UiPos pos) {
        ItemStack icon = arrowGroups.icon();
        String text = String.valueOf(arrowGroups.count());
        context.drawText(mc.textRenderer, text, pos.x(), pos.y() + 5, Colors.WHITE, true);
        context.drawItem(icon, pos.x() + (5 * (text.length() + 1)), pos.y());
    }

    public static ArmorData computeArmorData(PlayerInventory inventory) {
        Map<EquipmentSlot, ItemStack> equipped = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : ARMOR_SLOTS)
            equipped.put(slot, inventory.getStack(slot.getOffsetEntitySlotId(36)));

        List<ItemStack> candidates = new ArrayList<>();

        outer:
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack s = inventory.getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof Equipment equipment)) continue;

            AttributeModifiersComponent am = s.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

            if (am == null) continue;
            if (!ARMOR_SLOTS.contains(equipment.getSlotType())) continue;

            for (ItemStack worn : equipped.values()) {
                if (ItemStack.areItemsAndComponentsEqual(worn, s)) continue outer;
            }

            candidates.add(s);
        }

        Map<EquipmentSlot, ItemStack> best = new EnumMap<>(EquipmentSlot.class);

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack eq = equipped.get(slot);
            Map<String, Double> equippedAttr = buildAttributeMap(eq, slot);

            ItemStack bestStack = null;
            double[] bestScore = null;

            for (ItemStack candidate : candidates) {
                if (!(candidate.getItem() instanceof Equipment equipment) || equipment.getSlotType() != slot) continue;

                Map<String, Double> candidateAttr = buildAttributeMap(candidate, slot);
                int better = 0;
                int worse = 0;
                double sum = 0;
                Set<String> keys = new HashSet<>(candidateAttr.keySet());
                keys.addAll(equippedAttr.keySet());

                for (String k : keys) {
                    double diff = candidateAttr.getOrDefault(k, 0.0) - equippedAttr.getOrDefault(k, 0.0);
                    sum += diff;
                    if (diff > 0) better++;
                    else if (diff < 0) worse++;
                }
                double[] sc = new double[]{better, -worse, sum, candidateAttr.size()};

                if (sc[0] <= 0) continue;

                //0: better, 1: worse, 2: sumDelta, 3: candSize
                if (bestScore == null ||
                    sc[0] > bestScore[0] ||
                    (sc[0] == bestScore[0] && sc[1] > bestScore[1]) ||
                    (sc[0] == bestScore[0] && sc[1] == bestScore[1] && sc[2] > bestScore[2]) ||
                    (sc[0] == bestScore[0] && sc[1] == bestScore[1] && sc[2] == bestScore[2] && sc[3] > bestScore[3])) {

                    bestStack = candidate;
                    bestScore = sc;
                }
            }

            if (bestStack != null) best.put(slot, bestStack);
        }

        return new ArmorData(equipped, best);
    }

    private static @NotNull Map<String, Double> buildAttributeMap(ItemStack stack, EquipmentSlot slot) {
        Map<String, Double> map = new HashMap<>();
        if (stack == null || stack.isEmpty()) return map;

        AttributeModifiersComponent attrs = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attrs == null) return map;

        for (AttributeModifiersComponent.Entry e : attrs.modifiers()) {
            if (e.modifier().operation() != EntityAttributeModifier.Operation.ADD_VALUE || (e.slot() != null && !e.slot().matches(slot))) continue;
            String key;
            try {
                key = String.valueOf(Registries.ATTRIBUTE.getId(e.attribute().value()));
            } catch (Throwable ignore) {
                key = String.valueOf(e.modifier().id());
            }

            map.merge(key, e.modifier().value(), Double::sum);
        }
        return map;
    }

    public static LinkedList<ArrowGroup> computeArrowGroups(PlayerInventory inventory) {
        var groups = new LinkedHashMap<Set<StatusEffect>, Integer>();
        for (DefaultedList<ItemStack> itemStacks : ((PlayerInventoryAccessor) inventory).getCombinedInventory()) for (ItemStack itemStack  : itemStacks) {
            if (itemStack.isEmpty() || !itemStack.isIn(ItemTags.ARROWS)) continue;

            PotionContentsComponent potionContentsComponent = itemStack.get(DataComponentTypes.POTION_CONTENTS);
            Set<StatusEffect> effects = potionContentsComponent == null?
                new HashSet<>(0) :
                StreamSupport.stream(potionContentsComponent.getEffects().spliterator(), false).map(statusEffectInstance -> statusEffectInstance.getEffectType().value()).collect(Collectors.toSet());

            groups.merge(effects, itemStack.getCount(), Integer::sum);
        }

        var result = new LinkedList<ArrowGroup>();
        for (Map.Entry<Set<StatusEffect>, Integer> entry : groups.entrySet()) {
            ItemStack icon;
            Set<StatusEffect> effects = entry.getKey();
            if (effects.isEmpty()) icon = new ItemStack(Items.ARROW);
            else {
                icon = new ItemStack(Items.TIPPED_ARROW);
                icon.set(
                    DataComponentTypes.POTION_CONTENTS,
                    new PotionContentsComponent(Optional.empty(), Optional.empty(), effects.stream().map(effect -> new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(effect))).toList())
                );
            }
            result.add(new ArrowGroup(icon, entry.getValue()));
        }
        return result;
    }

    public record ArmorData(Map<EquipmentSlot, ItemStack> equipped, Map<EquipmentSlot, ItemStack> bestUpgrade) {}

    public record ArrowGroup(ItemStack icon, int count) {}
}
