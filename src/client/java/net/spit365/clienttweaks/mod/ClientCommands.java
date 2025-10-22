package net.spit365.clienttweaks.mod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.config.ArmorHudConfig;
import net.spit365.clienttweaks.custom.gui.ArmorHud;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import net.spit365.clienttweaks.manager.MethodManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.spit365.clienttweaks.custom.gui.ConfigManager.*;

public class ClientCommands {
    private static final int r = 1;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> dispatcher
            .register(literal(ClientTweaks.MOD_ID)
                .then(literal("cosmetic")
                    .then(literal("add")
                        .then(argument("cosmetic", StringArgumentType.word())
                            .then(argument("target", StringArgumentType.word())
                                .executes(context -> {
                                    String cosmeticKey = StringArgumentType.getString(context, "cosmetic");
                                    String target = StringArgumentType.getString(context, "target");
                                    JSONObject cosmetic = CosmeticsConfig.getEnabledCosmetic(cosmeticKey);
    
                                    cosmetic.put(target, new JSONObject());
                                    CosmeticsConfig.writeCosmetic(cosmeticKey, cosmetic);
    
                                    context.getSource().sendFeedback(Text.literal("Added " + target + " to the config. You will see the changes shortly"));
                                    return r;
                                })
                            )
                        )
                    )
                    .then(literal("remove")
                        .then(argument("cosmetic", StringArgumentType.word())
                            .then(argument("target", StringArgumentType.word())
                                .executes(context -> {
                                    String cosmeticKey = StringArgumentType.getString(context, "cosmetic");
                                    String target = StringArgumentType.getString(context, "target");
                                    JSONObject cosmetic = CosmeticsConfig.getEnabledCosmetic(cosmeticKey);

                                    cosmetic.remove(target);
                                    CosmeticsConfig.writeCosmetic(cosmeticKey, cosmetic);
    
                                    context.getSource().sendFeedback(Text.literal("Removed " + target + " from the config. You will see the changes shortly"));
                                    return r;
                                })
                            )
                        )
                    )
                    .then(literal("list")
                        .executes(context -> {
                            JSONObject cosmetics = CosmeticsConfig.getEnabledCosmetics();
                            for (String cosmetic : cosmetics.keySet()) {
                                StringBuilder sb = new StringBuilder(cosmetic + ": ");
                                for (String target : ((JSONObject) cosmetics.get(cosmetic)).keySet())
                                    sb.append(target).append(", ");
                                context.getSource().sendFeedback(Text.literal(sb.toString()));
                            }
                            return r;
                        })
                        .then(literal("json")
                            .executes(context -> {
                                context.getSource().sendFeedback(Text.literal(CosmeticsConfig.getEnabledCosmetics().toJSONString(JSONStyle.NO_COMPRESS)));
                                return r;
                            })
                        )
                    )
                    .then(literal("options")
                        .then(argument("cosmetic", StringArgumentType.word())
                            .then(argument("target", StringArgumentType.word())
                                .then(argument("key", StringArgumentType.string())
                                    .then(argument("value", StringArgumentType.string())
                                        .executes(context -> {
                                            String cosmeticKey = StringArgumentType.getString(context, "cosmetic");
                                            String targetKey = StringArgumentType.getString(context, "target");
                                            String key = StringArgumentType.getString(context, "key");
                                            String value = StringArgumentType.getString(context, "value");
    
                                            JSONObject cosmetic = CosmeticsConfig.getEnabledCosmetic(cosmeticKey);
                                            JSONObject target = read(cosmetic, targetKey);
                                            target.put(key, value);
                                            cosmetic.put(targetKey, target);
                                            CosmeticsConfig.writeCosmetic(cosmeticKey, cosmetic);
    
                                            context.getSource().sendFeedback(Text.literal("Set " + key + " to " + value));
                                            return r;
                                        })
                                    )
                                )
                            )
                        )
                    )
                )
                .then(literal(ArmorHud.ARMOR_HUD_ID)
                    .then(argument("key", StringArgumentType.string())
                        .then(argument("value", StringArgumentType.string())
                            .executes(context -> {
                                ArmorHudConfig.writeArmorHudOption(
                                    StringArgumentType.getString(context, "key"),
                                    StringArgumentType.getString(context, "value")
																  );
                                return r;
                            })
                        )
                    )
                )
                .then(literal("method")
                    .then(literal("add")
                        .then(argument("name", StringArgumentType.word())
                            .then(literal("source")
                                .then(argument("path", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name");
                                        String path = StringArgumentType.getString(ctx, "path");
                                        MethodManager.ensureMethodsFile();
                                        MethodManager.addMethodFromFile(name, path);
                                        ctx.getSource().sendFeedback(Text.literal("Added method " + name + " from " + path));
                                        return r;
                                    })
                                )
                            )
                            .then(argument("code", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    MethodManager.ensureMethodsFile();
                                    MethodManager.addMethodFromCode(name, StringArgumentType.getString(ctx, "code"));
                                    ctx.getSource().sendFeedback(Text.literal("Added method " + name + " from inline code"));
                                    return r;
                                })
                            )
                        )
                    )
                    .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                MethodManager.ensureMethodsFile();
                                ctx.getSource().sendFeedback(Text.literal(MethodManager.removeMethod(name) ? "Removed " + name : "Method not found: " + name));
                                return r;
                            })
                        )
                    )
                    .then(literal("execute")
                        .then(argument("name", StringArgumentType.word())
                            .executes(ctx -> {
                                MethodManager.ensureMethodsFile();
                                ctx.getSource().sendFeedback(Text.literal(MethodManager.compileAndExecute(StringArgumentType.getString(ctx, "name"))));
                                return r;
                            })
                        )
                    )
                )
            )
        );
    }
}
