package net.spit365.clienttweaks.mod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.spit365.clienttweaks.ClientTweaks;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.spit365.clienttweaks.manager.ConfigManager.*;

public class ClientCommands {
     private static final int r = 0;

     public static void init(){
          ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> dispatcher
               .register(literal(ClientTweaks.MOD_ID)
                    .then(literal("add")
                         .then(argument("category", StringArgumentType.word())
                              .then(argument("target", StringArgumentType.word())
                                   .executes(context -> {
                                        String category = StringArgumentType.getString(context, "category");
                                        String target = StringArgumentType.getString(context, "target");

                                        JSONObject file = file();
                                        JSONObject categoryObject = read(file, category);
                                        categoryObject.put(target, new JSONObject());
                                        write(category, categoryObject);

                                        context.getSource().sendFeedback(Text.literal("Added " + target + " to the config. You will see the changes shortly"));
                                        return r;
                                   }))))
                    .then(literal("remove")
                         .then(argument("category", StringArgumentType.word())
                              .then(argument("target", StringArgumentType.word())
                                   .executes(context -> {
                                        String category = StringArgumentType.getString(context, "category");
                                        String target = StringArgumentType.getString(context, "target");

                                        JSONObject file = file();
                                        JSONObject categoryObject = read(file, category);
                                        categoryObject.remove(target);
                                        write(category, categoryObject);

                                        context.getSource().sendFeedback(Text.literal("Removed " + target + " from the config. You will see the changes shortly"));
                                        return r;
                                   }))))
                    .then(literal("list")
                         .then(argument("target", StringArgumentType.word())
                              .executes(context -> {
                                   String targetKey = StringArgumentType.getString(context, "target");
                                   JSONObject file = file();
                                   for (String category : file.keySet()) {
                                        JSONObject categoryObject = (JSONObject) file.get(category);
                                        JSONObject targetObject = (JSONObject) categoryObject.get(targetKey);

                                        StringBuilder sb = new StringBuilder(category + ": \n");
                                        if (targetObject != null) for (String key : targetObject.keySet())
                                             sb.append(key).append(": ").append(targetObject.get(key)).append("\n");
                                        else sb.append("Target not found\n");
                                        context.getSource().sendFeedback(Text.literal(sb.toString()));
                                   }
                                   return r;
                              }))
                         .then(literal("json")
                              .executes(context -> {
                                   context.getSource().sendFeedback(Text.literal(file().toJSONString(JSONStyle.NO_COMPRESS)));
                                   return r;
                              }))
                              .executes(context -> {
                                   JSONObject file = file();
                                   for (String category : file.keySet()) {
								StringBuilder sb = new StringBuilder(category + ": ");
                                        for (String target : ((JSONObject) file.get(category)).keySet())
                                                 sb.append(target).append(", ");
                                        context.getSource().sendFeedback(Text.literal(sb.toString()));
                                   }
                                   return r;
                              }))
                    .then(literal("options")
                         .then(argument("category", StringArgumentType.word())
                              .then(argument("target", StringArgumentType.word())
                                   .then(argument("key", StringArgumentType.string())
                                        .then(argument("value", StringArgumentType.string())
                                             .executes(context -> {
                                                  String category = StringArgumentType.getString(context, "category");
                                                  String target = StringArgumentType.getString(context, "target");
                                                  String key = StringArgumentType.getString(context, "key");
                                                  String value = StringArgumentType.getString(context, "value");

										JSONObject categoryObject = read(file(), category);
                                                  JSONObject targetObject = read(categoryObject, target);
                                                  targetObject.put(key, value);
                                                  categoryObject.put(target, targetObject);
                                                  write(category, categoryObject);

                                                  context.getSource().sendFeedback(Text.literal("Set " + key + " to " + value));
                                                  return r;
                                             }))))))));
     }
}
