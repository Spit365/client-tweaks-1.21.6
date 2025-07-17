package net.spit365.clienttweaks.manager;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.spit365.clienttweaks.ClientTweaks;

import java.io.*;
import java.util.Objects;

public class ConfigManager {
     public static final String path = "config/tailed.json";

     public interface DefaultedJsonReader {
          default Integer intOption(JSONObject options, String value){
               try {
                    return Integer.parseInt(Objects.requireNonNull(options.get(value).toString()));
               }
               catch (Exception e1) {
                    try {
                         return Integer.parseInt(Objects.requireNonNull(defaults(value).toString()));}
                    catch (Exception e2) {
                         ClientTweaks.LOGGER.error("Your defaults couldn't be casted to Integer");
                              return 0;
                    }
               }
          }
          default Double doubleOption(JSONObject options, String value){
               try {
                    return Double.parseDouble(Objects.requireNonNull(options.get(value).toString()));
               }
               catch (Exception e1) {
                    try {
                         return Double.parseDouble(Objects.requireNonNull(defaults(value).toString()));}
                    catch (Exception e2) {
                         ClientTweaks.LOGGER.error("Your defaults couldn't be casted to Double");
                              return 0d;
                    }
               }
          }
          default Float floatOption(JSONObject options, String value){
               try {
                    return Float.parseFloat(Objects.requireNonNull(options.get(value).toString()));
               }
               catch (Exception e1) {
                    try {
                         return Float.parseFloat(Objects.requireNonNull(defaults(value).toString()));}
                    catch (Exception e2) {
                         ClientTweaks.LOGGER.error("Your defaults couldn't be casted to Float");
                              return 0f;
                    }
               }
          }
          default String stringOption(JSONObject options, String value){
               try {
                    return Objects.requireNonNull(options.get(value).toString());
               }
               catch (Exception e1) {
                    try {
                         return Objects.requireNonNull(defaults(value).toString());}
                    catch (Exception e2) {
                         ClientTweaks.LOGGER.error("Your defaults couldn't be casted to String");
                              return "";
                    }
               }
          }
          Object defaults(String value);
     }

     public static JSONObject read(JSONObject parent, String key) {
          if (parent == null || !parent.containsKey(key)) return new JSONObject();
          if (parent.get(key) instanceof JSONObject jsonObject) return jsonObject;
          return new JSONObject();
     }
     public static JSONObject file() {
          File f = new File(path);
          if (!f.exists()) return new JSONObject();
          try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
               StringBuilder stringBuilder = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) stringBuilder.append(line);
               if (!stringBuilder.toString().isEmpty())
                    return (JSONObject) JSONValue.parseWithException(stringBuilder.toString());
          }
          catch (Exception e) {
               ClientTweaks.LOGGER.error("Failed to read config file: {}", e.getMessage());}
          return new JSONObject();
     }
     public static void write(String category, JSONObject categoryContent) {
          JSONObject file = file();
          file.put(category, categoryContent);
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
               writer.write(file.toJSONString(JSONStyle.NO_COMPRESS));
          }
          catch (Exception e) {
               ClientTweaks.LOGGER.error("Failed to write to the config file: {}", e.getMessage());}
     }
}
