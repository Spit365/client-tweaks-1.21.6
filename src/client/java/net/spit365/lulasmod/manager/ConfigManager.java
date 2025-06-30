package net.spit365.lulasmod.manager;

import net.spit365.lulasmod.Lulasmod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class ConfigManager {
     public static String tailedPlayersJson = "config/tailed.json";

     private static void error(Exception e) {
          Lulasmod.LOGGER.error("Something went wrong with the tailed config file."); e.printStackTrace();}

     public enum ConfigFile{
          SPORES,
          TAILED
     }

     public static JSONArray read(ConfigFile part){
          File f = new File(tailedPlayersJson);
          if (!f.exists()) return new JSONArray();
          try (BufferedReader reader = new BufferedReader(new FileReader(tailedPlayersJson))){
               StringBuilder stringBuilder = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) stringBuilder.append(line);
               JSONObject o = new JSONObject(stringBuilder.toString());
               return o.optJSONArray(part.name());
          }
          catch (Exception e) {error(e);}
          return new JSONArray();
     }

     public static void write(ConfigFile part, JSONArray jsonArray){
          File f = new File(tailedPlayersJson);
          try(BufferedWriter writer = new BufferedWriter(new FileWriter(tailedPlayersJson))) {
               if (!f.exists() && f.createNewFile()) Lulasmod.LOGGER.error("Could not create new config file");
               JSONObject jsonObject = new JSONObject();
               for (ConfigManager.ConfigFile c : ConfigManager.ConfigFile.values())
                    if(!c.equals(part)) jsonObject.put(c.name(), read(c));
               jsonObject.put(part.name(), jsonArray);
               writer.write(jsonObject.toString(4));
          }
          catch (Exception e) {error(e);}
     }
}
