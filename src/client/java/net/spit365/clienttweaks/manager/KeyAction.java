package net.spit365.clienttweaks.manager;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public record KeyAction<T>(KeyBinding key, Action<T> action) {
	public @FunctionalInterface interface Action<T>{void execute(T input);}
	public void tick(T input){if (key.wasPressed()) action.execute(input);}
	public static <T> KeyAction<T> register(Identifier id, int key, Action<T> action){
		return new KeyAction<>(KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key." + id.getNamespace() + "." + id.getPath(),
			InputUtil.Type.KEYSYM,
			key,
			"key.categories." + id.getNamespace()
		)), action);
	}
}
