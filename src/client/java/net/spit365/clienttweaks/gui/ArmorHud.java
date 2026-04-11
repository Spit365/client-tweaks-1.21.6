package net.spit365.clienttweaks.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.config.ArmorHudConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ArmorHud {
    public static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	public static void init() {
		HudElementRegistry.addFirst(Identifier.of(ClientTweaks.MOD_ID, ArmorHudConfig.ARMOR_HUD_ID), (context, tickCounter) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity player = client.player;
			if (player == null) return;
			PlayerInventory inventory = player.getInventory();
            ArmorHudRenderer armorHudRenderer = ArmorHudConfig.getArmorHudRenderer();

            if (ArmorHudConfig.isEnabled("armor")) armorHudRenderer.armorHudRender.accept(context, client);
			if (ArmorHudConfig.isEnabled("arrows") && inventory.contains(s -> s.isIn(ItemTags.ARROWS))) armorHudRenderer.arrowRenderer.accept(context, client);
		});
	}

    public enum ArmorHudRenderer {
	    TOP_RIGHT((context, client) -> {
            List<ArrowGroup> arrowGroups = computeArrowGroups(client.player.getInventory());
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArrowGroup group = arrowGroups.get(i);
                renderArrowIcon(context, client, group, context.getScaledWindowWidth() - 30, 20 * (i + 4));
            }
        }, (context, client) -> {
            ArmorData data = computeArmorData(client.player.getInventory());
            int windowWidth = context.getScaledWindowWidth() - 20;
            int durabilityLength = calcDurabilityLength(data);
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                int y = switch (slot){
                    case HEAD -> 0;
                    case CHEST -> 20;
                    case LEGS -> 40;
                    case FEET -> 60;
                    default -> throw new IllegalStateException("Unexpected value: " + slot);
                };
                renderArmorIcon(context, data, windowWidth - 40, y, slot, client.textRenderer, Alignment.RIGHT, durabilityLength);

            }
        }),
		TOP_LEFT((context, client) -> {
            List<ArrowGroup> arrowGroups = computeArrowGroups(client.player.getInventory());
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArrowGroup group = arrowGroups.get(i);
                renderArrowIcon(context, client, group, 0, 20 * (i + 4));
            }
        }, (context, client) -> {
            ArmorData data = computeArmorData(client.player.getInventory());
            int durabilityLength = calcDurabilityLength(data);
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                int y = switch (slot){
                    case HEAD -> 0;
                    case CHEST -> 20;
                    case LEGS -> 40;
                    case FEET -> 60;
                    default -> throw new IllegalStateException("Unexpected value: " + slot);
                };
                renderArmorIcon(context, data, 0, y, slot, client.textRenderer, Alignment.LEFT, durabilityLength);
            }
        }),
		BOTTOM_RIGHT((context, client) -> {
            List<ArrowGroup> arrowGroups = computeArrowGroups(client.player.getInventory());
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArrowGroup group = arrowGroups.get(i);
                renderArrowIcon(context, client, group, context.getScaledWindowWidth() - 20, context.getScaledWindowHeight() - (20 * (i + 1)));
            }
        }, (context, client) -> {
            int x = context.getScaledWindowWidth() - 60;
            ArmorData data = computeArmorData(client.player.getInventory());
            int durabilityLength = calcDurabilityLength(data);
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                int y = context.getScaledWindowHeight() - switch (slot){
                    case HEAD -> 80;
                    case CHEST -> 60;
                    case LEGS -> 40;
                    case FEET -> 20;
                    default -> throw new IllegalStateException("Unexpected value: " + slot);
                };
                renderArmorIcon(context, data, x, y, slot, client.textRenderer, Alignment.RIGHT, durabilityLength);
            }
        }),
		BOTTOM_LEFT((context, client) -> {
            List<ArrowGroup> arrowGroups = computeArrowGroups(client.player.getInventory());
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArrowGroup group = arrowGroups.get(i);
                renderArrowIcon(context, client, group, 0, context.getScaledWindowHeight() - (20 * (i + 1)));
            }
        }, (context, client) -> {
            int height = context.getScaledWindowHeight();
            ArmorData data = computeArmorData(client.player.getInventory());
            int durabilityLength = calcDurabilityLength(data);
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                int y = height - switch (slot){
                    case HEAD -> 80;
                    case CHEST -> 60;
                    case LEGS -> 40;
                    case FEET -> 20;
                    default -> throw new IllegalStateException("Unexpected value: " + slot);
                };
                renderArmorIcon(context, data, 0, y, slot, client.textRenderer, Alignment.LEFT, durabilityLength);
            }
        }),
		HOTBAR((context, client) -> {
            List<ArrowGroup> arrowGroups = computeArrowGroups(client.player.getInventory());
            for (int i = 0; i < arrowGroups.size(); i++) {
                ArrowGroup group = arrowGroups.get(i);
                renderArrowIcon(context, client, group, context.getScaledWindowWidth() - 20, 20 * (i + 4));
            }
        }, (context, client) -> {
            ArmorData data = computeArmorData(client.player.getInventory());
            int width = context.getScaledWindowWidth() / 4;
            int height = context.getScaledWindowHeight();
            System.out.println(height);
            int durabilityLength = calcDurabilityLength(data);
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                int x;
                int y;
                Alignment alignment;
                switch (slot) {
                    case HEAD -> {
                        x = width - 20;
                        y = height - 40;
                        alignment = Alignment.LEFT;
                    }
                    case CHEST -> {
                        x = width - 20;
                        y = height - 20;
                        alignment = Alignment.LEFT;
                    }
                    case LEGS -> {
                        x = width * 3 - 20;
                        y = height - 40;
                        alignment = Alignment.RIGHT;
                    }
                    case FEET -> {
                        x = width * 3 - 20;
                        y = height - 20;
                        alignment = Alignment.RIGHT;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + slot);
                }
                renderArmorIcon(context, data, x, y, slot, client.textRenderer, alignment, durabilityLength);
            }
        });

        public final BiConsumer<DrawContext, MinecraftClient> arrowRenderer;
        public final BiConsumer<DrawContext, MinecraftClient> armorHudRender;

        ArmorHudRenderer(BiConsumer<DrawContext, MinecraftClient> arrowRenderer, BiConsumer<DrawContext, MinecraftClient> armorHudRender){
            this.arrowRenderer = arrowRenderer;
            this.armorHudRender = armorHudRender;
        }


        private static int calcDurabilityLength(ArmorData data) {
            Map<EquipmentSlot, ItemStack> equipped = data.equipped();
            int durabilityLength = 0;
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack stack = equipped.get(slot);
                if (stack != null) {
                    int stackDurabilityLength = String.valueOf(stack.getMaxDamage() - stack.getDamage()).length();
                    if (stackDurabilityLength > durabilityLength) durabilityLength = stackDurabilityLength;
                }

            }
            return durabilityLength;
        }

        private static void renderArmorIcon(DrawContext context, @NotNull ArmorData data, int x, int y, EquipmentSlot slot, TextRenderer textRenderer, Alignment alignment, int durabilityLength) {
            ItemStack equipped = data.equipped().get(slot);
            String literalDurability = "";
            int color = Colors.BLACK;

            if (!equipped.isEmpty()) {
                int maxDamage = equipped.getMaxDamage();
                int durability = maxDamage - equipped.getDamage();
                float doubledPercent = MathHelper.clamp(durability * 2f / maxDamage, 0, 2);

                int green;
                int blue;
                if (doubledPercent > 1) {
                    green = 255;
                    blue = MathHelper.lerp(doubledPercent - 1, 0, 255);
                } else {
                    blue = 0;
                    green = MathHelper.lerp(doubledPercent, 0, 255);
                }
                color = 0xFFFF0000 | (green << 8) | blue;

                literalDurability = String.valueOf(durability);
            }

            int xText = x;
            int xEqItem = x;
            int xBItem = x;

            switch (alignment) {
                case LEFT -> {
                    xEqItem = x + 5 + 5 * durabilityLength;
                    xBItem = x + (!equipped.isEmpty() ? 25 + 5 * durabilityLength : 0);
                }
                case RIGHT -> {
                    xEqItem = x + 20;
                    xText = x + 40;
                }
            }

            if (!equipped.isEmpty()) {
                context.drawText(textRenderer, Text.literal(literalDurability), xText, y + 5, color, true);
                context.drawItem(equipped, xEqItem, y);
                context.drawStackOverlay(textRenderer, equipped, xEqItem, y);
            }

            ItemStack best = data.bestUpgrade().get(slot);
            if (best != null) {
                context.drawItem(best, xBItem, y);
                context.drawStackOverlay(textRenderer, best, xBItem, y);
            }
        }

        private static void renderArrowIcon(@NotNull DrawContext context, @NotNull MinecraftClient mc, @NotNull ArrowGroup arrowGroup, int x, int y) {
            ItemStack icon = arrowGroup.icon();
            String text = String.valueOf(arrowGroup.count());
            context.drawText(mc.textRenderer, text, x, y + 5, Colors.WHITE, true);
            context.drawItem(icon, x + (5 * (text.length() + 1)), y);
        }

        @Contract("_ -> new")
        private static @NotNull ArmorData computeArmorData(PlayerInventory inventory) {
            Map<EquipmentSlot, ItemStack> equipped = new EnumMap<>(EquipmentSlot.class);
            for (EquipmentSlot slot : ARMOR_SLOTS)
                equipped.put(slot, inventory.getStack(slot.getOffsetEntitySlotId(36)));

            List<ItemStack> candidates = new ArrayList<>();

            outer:
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack s = inventory.getStack(i);
                if (s.isEmpty()) continue;

                EquippableComponent eq = s.get(DataComponentTypes.EQUIPPABLE);
                AttributeModifiersComponent am = s.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

                if (eq == null || am == null) continue;
                if (!ARMOR_SLOTS.contains(eq.slot())) continue;

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
                    EquippableComponent eqC = candidate.get(DataComponentTypes.EQUIPPABLE);
                    if (eqC == null || eqC.slot() != slot) continue;

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

        private static @NotNull LinkedList<ArrowGroup> computeArrowGroups(@NotNull PlayerInventory inventory) {
            var groups = new LinkedHashMap<Set<StatusEffect>, Integer>();
            for (ItemStack itemStack : inventory) {
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
                        new PotionContentsComponent(Optional.empty(), Optional.empty(), effects.stream().map(effect -> new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(effect))).toList(), Optional.empty())
                    );
                }
                result.add(new ArrowGroup(icon, entry.getValue()));
            }
            return result;
        }

        private record ArmorData(Map<EquipmentSlot, ItemStack> equipped, Map<EquipmentSlot, ItemStack> bestUpgrade) {}

        private record ArrowGroup(ItemStack icon, int count) {}

        private enum Alignment { LEFT, RIGHT }
    }
}
