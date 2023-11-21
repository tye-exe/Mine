package me.tye.mine.utils;

import me.tye.mine.Mine;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Util {

/**
 This plugin.
 */
public static final JavaPlugin plugin = JavaPlugin.getPlugin(Mine.class);


/**
 The data folder.
 */
public static final File dataFolder = plugin.getDataFolder();

/**
 The config file for this plugin.
 */
public static final File configFile = new File(dataFolder.toPath() + File.separator + "config.yml");

/**
 The lang folder for this plugin.
 */
public static File langFolder = new File(dataFolder.toPath() + File.separator + "langFiles");


/**
 The logger for this plugin.
 */
public static final Logger log = plugin.getLogger();

/**
 The NamespacedKey for "Mine.identifier".
 */
public static final NamespacedKey identifierKey = new NamespacedKey(plugin, "identifier");

/**
 The material the pointer item should be.
 */
public static final Material pointer = Material.WOODEN_SWORD;

/**
 How quickly the pointer has to be dropped in succession for a confirmation of a selection. (In milliseconds).
 */
public static final Long dropRetryInterval = 500L;


/**
 Sets some attributes of a new item in one method.
 * @param material The material of the new item.
 * @param displayName The display name of the item.
 * @param identifier The identifier of an item. This is used to uniquely identify types of items inside of Mine!.
 * @return A new item with the set properties.
 */
public static @NotNull ItemStack itemProperties(@NotNull Material material, @Nullable String displayName, @Nullable String identifier) {
  ItemStack itemStack = new ItemStack(material);
  ItemMeta itemMeta = itemStack.getItemMeta();

  if (displayName != null) {
    itemMeta.displayName(Component.text(displayName).color(Colours.Green));
  }

  if (identifier != null) {
    itemMeta.getPersistentDataContainer().set(identifierKey, PersistentDataType.STRING, identifier);
  }

  itemStack.setItemMeta(itemMeta);
  return itemStack;
}


/**
 This method <b>does</b> take into account diagonal blocks.
 * @param block The block to check the neighbours of.
 * @param material Only returns the neighboring blocks with this material. If this is set to null, then all the surrounding blocks will be returned.
 * @return A list of blocks directly touching the given block that have the given material.<br>
 * This <b>doesn't</b> include the given block.
 */
public static @NotNull List<Block> getSurrounding(@NotNull Block block, @Nullable Material material) {
  List<Block> surrounding = new ArrayList<>();

  Location cornerLocation = block.getLocation().subtract(1, 1, 1);

  //loops over all the blocks surrounding the given one
  for (int x = 0; x < 3; x++) {
    for (int y = 0; y < 3; y++) {
      for (int z = 0; z < 3; z++) {
        Location checkingLocation = cornerLocation.clone().add(x, y, z);

        if (checkingLocation.equals(block.getLocation())) continue;

        if (material == null || checkingLocation.getBlock().getType().equals(material)) {
          surrounding.add(checkingLocation.getBlock());
        }

      }
    }
  }

  return surrounding;
}



//lang & config
private static HashMap<String,Object> lang = new HashMap<>();
private static HashMap<String,Object> config = new HashMap<>();

/**
 Sets the lang responses the to the given HashMap.
 @param lang New lang map. */
public static void setLang(@Nullable HashMap<String,Object> lang) {
  Util.lang = getKeysRecursive(lang);
}

/**
 Sets the config responses to the given HashMap.
 @param config New config map. */
public static void setConfig(@Nullable HashMap<String,Object> config) {
  Util.config = getKeysRecursive(config);
}

/**
 Formats the Map returned from Yaml.load() into a hashmap where the exact key corresponds to the value.<br>
 E.G: key: "example.response" value: "test".
 @param baseMap The Map from Yaml.load().
 @return The formatted Map. */
public static @NotNull HashMap<String,Object> getKeysRecursive(@Nullable Map<?,?> baseMap) {
  HashMap<String,Object> map = new HashMap<>();
  if (baseMap == null) return map;

  for (Object key : baseMap.keySet()) {
    Object value = baseMap.get(key);

    if (value instanceof Map<?,?> subMap) {
      map.putAll(getKeysRecursive(String.valueOf(key), subMap));
    } else {
      map.put(String.valueOf(key), String.valueOf(value));
    }

  }

  return map;
}

/**
 Formats the Map returned from Yaml.load() into a hashmap where the exact key corresponds to the value.
 @param keyPath The path to append to the starts of the key. (Should only be called internally).
 @param baseMap The Map from Yaml.load().
 @return The formatted Map. */
public static @NotNull HashMap<String,Object> getKeysRecursive(@NotNull String keyPath, @NotNull Map<?,?> baseMap) {
  if (!keyPath.isEmpty()) keyPath += ".";

  HashMap<String,Object> map = new HashMap<>();
  for (Object key : baseMap.keySet()) {
    Object value = baseMap.get(key);

    if (value instanceof Map<?,?> subMap) {
      map.putAll(getKeysRecursive(keyPath+key, subMap));
    } else {
      map.put(keyPath+key, String.valueOf(value));
    }

  }

  return map;
}

/**
 Gets value from loaded lang file.
 If no external value for this key can be found it will attempt to get the lang response from the internal file. If there is no internal file for the selected lang then it will fall back to english.<br>
 If there is still no response found for the key an error message will be sent into console.
 @param key     Key to the value from the loaded lang file.
 @param replace Should be inputted in "valueToReplace0", valueToReplaceWith0", "valueToReplace1", valueToReplaceWith2"... etc
 @return The lang response with the specified values replaced. */
public static @NotNull String getLang(@NotNull String key, @Nullable String... replace) {
  String rawResponse = String.valueOf(lang.get(key));

  //if config doesn't contain the key it falls back to the built-in one.
  if (rawResponse.equals("null")) {

    InputStream defultLandInputStream = plugin.getResource("langFiles/"+getConfig("lang")+".yml");
    HashMap<String,Object> defaultLang;

    //falls back to the eng lang file if the one for the selected lang couldn't be found
    if (defultLandInputStream == null) {
      defaultLang = getKeysRecursive(new Yaml().load(plugin.getResource("langFiles/eng.yml")));
    }
    else {
      defaultLang = getKeysRecursive(new Yaml().load(defultLandInputStream));
    }

    rawResponse = String.valueOf(defaultLang.get(key));

    if (rawResponse.equals("null")) {

      if (key.equals("exceptions.noSuchResponse")) {
        return "Unable to get key \"lang.noSuchResponse\" from lang file. This message is in english to prevent a stack overflow error.";
      }
      else {
        rawResponse = getLang("exceptions.noSuchResponse", "key", key);
      }
    }
    else {
      lang.put(key, defaultLang.get(key));
    }

    log.warning(getLang("exceptions.noExternalResponse", "key", key));
  }

  for (int i = 0; i <= replace.length-1; i += 2) {
    if (replace[i+1] == null) continue;

    rawResponse = rawResponse.replaceAll("\\{"+replace[i]+"}", replace[i+1]);
  }

  //the A appears for some reason?
  return rawResponse.replaceAll("Â§", "§");
}

/**
 Gets a value from the config file.<br>
 If no external value can be found it will fall back onto the default internal value. If there is still no value it will return true and log a severe error.
 @param key Key for the config to get the value of.
 @return The value from the file. */
public static @Nullable Object getConfig(@NotNull String key) {
  Object response;

  //if config doesn't contain the key it checks if it is present in default config files.
  if (!config.containsKey(key)) {
    HashMap<String,Object> defaultConfig = getKeysRecursive(new Yaml().load(plugin.getResource("config.yml")));
    response = defaultConfig.get(key);

    if (response == null) {
      log.warning(getLang("exceptions.noSuchResponse", "key", key));
      return Boolean.TRUE;
    }

    config.put(key, response);
    log.warning(getLang("exceptions.noExternalResponse", "key", key));

  } else
    response = String.valueOf(config.get(key));

  switch (key) {
  case "lang", "keepDeleted.time", "keepDeleted.size" -> {
    return String.valueOf(response);
  }
  case "showErrorTrace", "showOpErrorSummary", "ADR" -> {
    return Boolean.valueOf(String.valueOf(response));
  }
  }

  log.warning(getLang("exceptions.noConfigMatch", "key", key));
  return null;
}


/**
 @param filepath Path to the file inside the resource folder.
 @return The default YAML values of the resource. */
public static @NotNull HashMap<String,Object> getDefault(@Nullable String filepath) {
  if (filepath == null) return new HashMap<>();

  InputStream resourceInputSteam = plugin.getResource(filepath);
  if (resourceInputSteam == null) return new HashMap<>();

  return new Yaml().load(resourceInputSteam);
}

/**
 Copies the content of an internal file to a new external one.
 @param file     External file destination
 @param resource Input stream for the data to write, or null if target is an empty file/dir.
 @param isFile Set to true to create a file. Set to false to create a dir.*/
public static void createFile(@NotNull File file, @Nullable InputStream resource, boolean isFile) {
  if (file.exists()) return;

  try {
    if (isFile) {
      if (!file.createNewFile()) throw new IOException();
    }
    else {
      if (!file.mkdir()) throw new IOException();
    }

    if (resource != null) {
      String text = new String(resource.readAllBytes());
      FileWriter fw = new FileWriter(file);
      fw.write(text);
      fw.close();
    }

  } catch (IOException e) {
    log.log(Level.WARNING, getLang("exceptions.fileCreation", "filePath", file.getAbsolutePath()), e);
  }
}

/**
 Reads the data from an external specified yaml file and returns the data in a hashmap of Key, Value. Appending any missing values to the external file, making use of the resourcePath of the file inside the jar.<br>
 If the resource path doesn't return any files then no repairing will be done to the file.
 @param externalFile External config file.
 @param resourcePath Path to the internal file from the resource folder.
 @return The data from the external file with any missing values being loaded in as defaults. */
public static @NotNull HashMap<String,Object> returnFileConfigs(@NotNull File externalFile, @Nullable String resourcePath) {
  HashMap<String,Object> loadedValues;

  try {
    //reads data from config file and formats it
    FileReader fr = new FileReader(externalFile);
    HashMap<String,Object> unformattedLoadedValues = new Yaml().load(fr);
    fr.close();

    if (unformattedLoadedValues == null) {
      unformattedLoadedValues = new HashMap<>();
    }

    loadedValues = getKeysRecursive(unformattedLoadedValues);
    HashMap<String,Object> defaultValues = getKeysRecursive(getDefault(resourcePath));

    //checks if there is a key missing in the file
    if (loadedValues.keySet().containsAll(defaultValues.keySet())) return loadedValues;
    assert resourcePath != null;

    //gets the missing keys
    HashMap<String,Object> missing = new HashMap<>();
    for (String key : defaultValues.keySet()) {
      if (loadedValues.containsKey(key)) continue;

      missing.put(key, defaultValues.get(key));
    }

    StringBuilder toAppend = new StringBuilder();
    InputStream resourceInputStream = plugin.getResource(resourcePath);
    if (resourceInputStream == null) return new HashMap<>();

    Object[] internalFileText = new String(resourceInputStream.readAllBytes(), StandardCharsets.UTF_8).lines().toArray();


    //appends the missing keys with default values and comments that are above them in the default file.
    for (String missingKey : missing.keySet()) {
      toAppend.append("\n");

      if (missingKey.contains(".")) {
        toAppend.append(missingKey).append(": \"")
                .append(defaultValues.get(missingKey).toString().replace("\"", "\\\""))
                .append("\"");
      }
      else {
        //searches though internal file to retrieve keys, values, & comments
        for (int i = 0; i < internalFileText.length; i++) {
          if (!internalFileText[i].toString().startsWith(missingKey)) continue;

          //search up for start of comments
          int ii = 0;
          while (i+ii-1 > 0 && internalFileText[i+ii-1].toString().startsWith("#")) {
            ii--;
          }

          //appends all of the comments in correct order
          while (ii < 0) {
            toAppend.append(internalFileText[i+ii]).append("\n");
            ii++;
          }

          toAppend.append(internalFileText[i].toString());
        }
      }

    }

    //writes the missing data (if present) to the config file.
    if (!toAppend.isEmpty()) {
      loadedValues.putAll(missing);
      FileWriter fw = new FileWriter(externalFile, true);
      fw.write(toAppend.toString());
      fw.close();
    }

  } catch (Exception e) {
    loadedValues = getKeysRecursive(getDefault(resourcePath));

    if (resourcePath != null && resourcePath.equals("config.yml")) {
      Util.setConfig(getDefault(resourcePath));
    }

    log.log(Level.SEVERE, getLang("exceptions.errorWritingConfigs", "filePath", externalFile.getAbsolutePath()), e);
  }

  return loadedValues;
}
}
