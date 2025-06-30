package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.spit365.lulasmod.manager.ConfigManager;
import org.json.JSONArray;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientCommands {
     private static final int r = 0;

     public static void init(){
          ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> dispatcher
               .register(literal("tailed")
                    .then(literal("add")
                         .then(argument("category", StringArgumentType.word())
                              .then(argument("target", StringArgumentType.word())
                                   .executes(context -> {
                                        try {
                                             ConfigManager.ConfigFile c = Enum.valueOf(ConfigManager.ConfigFile.class, StringArgumentType.getString(context, "category"));
                                             String target = StringArgumentType.getString(context, "target");
                                             ConfigManager.write(c, new JSONArray(ConfigManager.read(c).put(target)));
                                             context.getSource().sendFeedback(Text.literal("Added " + target + " to the config. You will see the changes shortly"));
                                        } catch (IllegalArgumentException e) {
                                             context.getSource().sendFeedback(Text.literal("That Category doesn't exist"));
                                        }
                                        return r;
                                   }))))
                    .then(literal("remove")
                         .then(argument("category", StringArgumentType.word())
                              .then(argument("target", StringArgumentType.word())
                                   .executes(context -> {
                                        try {
                                             ConfigManager.ConfigFile c = Enum.valueOf(ConfigManager.ConfigFile.class, StringArgumentType.getString(context, "category"));
                                             List<Object> read = ConfigManager.read(c).toList();
                                             String target = StringArgumentType.getString(context, "target");
                                             read.removeIf(s -> s.equals(target));
                                             ConfigManager.write(c, new JSONArray(read));
                                             context.getSource().sendFeedback(Text.literal("Removed " + target + " from the config. You will see the changes shortly"));
                                        } catch (IllegalArgumentException e) {
                                             context.getSource().sendFeedback(Text.literal("That Category doesn't exist"));
                                        }
                                        return r;
                                   }))))
                    .then(literal("list")
                         .executes(context -> {
                              for (ConfigManager.ConfigFile c : ConfigManager.ConfigFile.values()){
                                   JSONArray read = ConfigManager.read(c);
                                   StringBuilder stringBuilder = new StringBuilder(c.name() + ": ");
                                   read.forEach(s -> stringBuilder.append(s).append(", "));
                                   context.getSource().sendFeedback(Text.literal(stringBuilder.toString()));
                              }
                              return r;
                         }))
          ));
     }
}
