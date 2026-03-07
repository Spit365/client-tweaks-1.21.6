package net.spit365.clienttweaks.mod;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.config.ArmorHudConfig;
import net.spit365.clienttweaks.config.BoxOutlineConfig;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import net.spit365.clienttweaks.util.BoxContext;
import net.spit365.clienttweaks.util.ModUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.spit365.clienttweaks.util.ConfigManager.*;

@Environment(EnvType.CLIENT)
public class ClientCommands {
    private static final int r = 1;

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> dispatcher
            .register(literal(ClientTweaks.MOD_ID)
                .then(literal(BoxOutlineConfig.BOX_OUTLINES_ID)
                    .then(literal("add")
                        .then(argument("color", ColorArgumentType.color())
                            .then(argument("pos1", BlockPosArgumentType.blockPos())
                                .then(argument("pos2", BlockPosArgumentType.blockPos())
                                    .executes(context -> {
                                        Set<BoxContext> permanentState = ModUtil.makeMutable(BoxOutlineConfig.getPermanentBoxOutlineState());
                                        FabricClientCommandSource source = context.getSource();
                                        Integer color = context.getArgument("color", Formatting.class).getColorValue();
                                        if (color == null) {
                                            source.sendFeedback(Text.literal("That color doesn't have a color value associated with it"));
                                            return r;
                                        }
                                        permanentState.add(new BoxContext(
                                            getBox(context, source.getPosition(), source.getRotation()), color
                                        ));
                                        BoxOutlineConfig.setPermanentBoxOutlineState(permanentState);
                                        source.sendFeedback(Text.literal("Added box"));
                                        return r;
                                    })
                                )
                            )
                        )
                    )
                    .then(literal("remove")
                        .then(literal("all")
                            .executes(context -> {
                                BoxOutlineConfig.setPermanentBoxOutlineState(Set.of());
                                return r;
                            })
                        )
                        .then(literal("box")
                            .then(argument("pos1", BlockPosArgumentType.blockPos())
                                .then(argument("pos2", BlockPosArgumentType.blockPos())
                                    .executes(context -> {
                                        Set<BoxContext> permanentState = ModUtil.makeMutable(BoxOutlineConfig.getPermanentBoxOutlineState());
                                        FabricClientCommandSource source = context.getSource();
                                        Box box = getBox(context, source.getPosition(), source.getRotation());
                                        if (permanentState.removeIf(boxContext -> boxContext.box().equals(box))) {
                                            BoxOutlineConfig.setPermanentBoxOutlineState(permanentState);
                                            source.sendFeedback(Text.literal("Removed"));
                                        }
                                        else source.sendFeedback(Text.literal("Couldn't find any box with those measurements"));
                                        return r;
                                    })
                                )
                            )
                        )
                        .then(literal("color")
                            .then(argument("color", ColorArgumentType.color())
                                .executes(context -> {
                                    Set<BoxContext> permanentState = ModUtil.makeMutable(BoxOutlineConfig.getPermanentBoxOutlineState());
                                    Formatting colorArgument = context.getArgument("color", Formatting.class);
                                    Integer color = colorArgument.getColorValue();
                                    FabricClientCommandSource source = context.getSource();
                                    if (color == null) {
                                        source.sendFeedback(Text.literal("That color doesn't have a color value associated with it"));
                                        return r;
                                    }
                                    int[] removed = {0};
                                    permanentState.removeIf(boxContext -> {
                                        boolean result = boxContext.color() == color;
                                        if (result) removed[0]++;
                                        return result;
                                    });
                                    BoxOutlineConfig.setPermanentBoxOutlineState(permanentState);
                                    source.sendFeedback(Text.literal("Color " + colorArgument.getName() + " with " + removed[0] + " members has been removed"));
                                    return r;
                                })
                            )
                            .then(literal("value")
                                .then(argument("hex-value", StringArgumentType.string())
                                    .executes(context -> {
                                        Set<BoxContext> permanentState = ModUtil.makeMutable(BoxOutlineConfig.getPermanentBoxOutlineState());
                                        String color = StringArgumentType.getString(context, "hex-value");
                                        FabricClientCommandSource source = context.getSource();
                                        try {
                                            int colorValue = Integer.parseInt(color, 16);
                                            int[] removed = {0};
                                            permanentState.removeIf(boxContext -> {
                                                boolean result = boxContext.color() == colorValue;
                                                if (result) removed[0]++;
                                                return result;
                                            });
                                            BoxOutlineConfig.setPermanentBoxOutlineState(permanentState);
                                            source.sendFeedback(Text.literal("Color " + color + " with " + removed[0] + " members has been removed"));
                                        } catch (NumberFormatException ignored) {
                                            source.sendFeedback(Text.literal("Not a valid hex-value"));
                                        }
                                        return r;
                                    })
                                )
                            )
                        )
                        .then(literal("radius")
                            .then(argument("radius", FloatArgumentType.floatArg(0))
                                .executes(context -> {
                                    Set<BoxContext> permanentState = ModUtil.makeMutable(BoxOutlineConfig.getPermanentBoxOutlineState());
                                    FabricClientCommandSource source = context.getSource();
                                    Vec3d position = source.getPosition();
                                    int[] removed = {0};
                                    permanentState.removeIf(boxContext ->
                                    {
                                        boolean result = position.distanceTo(boxContext.box().getCenter()) <= FloatArgumentType.getFloat(context, "radius");
                                        if (result) removed[0]++;
                                        return result;
                                    });
                                    BoxOutlineConfig.setPermanentBoxOutlineState(permanentState);
                                    source.sendFeedback(Text.literal("Removed " + removed[0]));
                                    return r;
                                })
                            )
                        )
                    )
                )
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
                .then(literal(ArmorHudConfig.ARMOR_HUD_ID)
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
            )
        );
    }

    private static @NotNull Box getBox(CommandContext<FabricClientCommandSource> context, Vec3d position, Vec2f rotation) {
        ServerCommandSource fakeSource = new ServerCommandSource(null, position, rotation, null, 0, "", Text.empty(), null, null);
        Box box = new Box(
            new Vec3d(context.getArgument("pos1", PosArgument.class).toAbsoluteBlockPos(fakeSource)),
            new Vec3d(context.getArgument("pos2", PosArgument.class).toAbsoluteBlockPos(fakeSource))
        );
        Box box1 = new Box(
            box.minX,
            box.minY,
            box.minZ,
            box.maxX + 1,
            box.maxY + 1,
            box.maxZ + 1
        );
        return box1;
    }
}
