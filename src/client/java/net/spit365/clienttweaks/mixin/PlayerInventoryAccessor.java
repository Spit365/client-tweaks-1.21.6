package net.spit365.clienttweaks.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.entity.player.PlayerInventory.class)
public interface PlayerInventoryAccessor {
    @Accessor
    List<DefaultedList<ItemStack>> getCombinedInventory();
}
