package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.ClientTweaks;

public class ClientGui {
	public static void init(){
		HudElementRegistry.addFirst(Identifier.of(ClientTweaks.MOD_ID, "armor_hud"), (context, tickCounter) -> {
			MinecraftClient instance = MinecraftClient.getInstance();
			ClientPlayerEntity player = instance.player;
			if (player != null) {
				PlayerInventory inventory = player.getInventory();
				int x = context.getScaledWindowWidth() -20;
				renderItem(context, inventory, x, 0, instance, EquipmentSlot.HEAD);
				renderItem(context, inventory, x, 20, instance, EquipmentSlot.BODY);
				renderItem(context, inventory, x, 40, instance, EquipmentSlot.LEGS);
				renderItem(context, inventory, x, 60, instance, EquipmentSlot.FEET);
			}
		});
	}

	private static void renderItem(DrawContext context, PlayerInventory inventory, int x, int y, MinecraftClient instance, EquipmentSlot equipmentSlot) {
		ItemStack stack = inventory.getStack(equipmentSlot.getOffsetEntitySlotId(36));
		context.drawItem(stack, x, y);
		context.drawStackOverlay(instance.textRenderer, stack, x, y);
	}
}
