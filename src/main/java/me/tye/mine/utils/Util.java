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
import java.nio.charset.Charset;
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


//Configerable (in the future?)


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
 Copies the content of an internal file to a new external one.
 @param file     External file destination
 @param resource Input stream for the data to write, or null if target is an empty file/dir.
 @param isFile Set to true to create a file. Set to false to create a dir.*/
public static void makeRequiredFile(@NotNull File file, @Nullable InputStream resource, boolean isFile) throws IOException {
  if (file.exists())
    return;

  if (isFile) {
    if (!file.createNewFile())
      throw new IOException();
  }
  else {
    if (!file.mkdir())
      throw new IOException();
  }

  if (resource != null) {
    String text = new String(resource.readAllBytes());
    FileWriter fw = new FileWriter(file);
    fw.write(text);
    fw.close();
  }
}

/**
 Copies the content of an internal file to a new external one.
 @param file     External file destination
 @param resource Input stream for the data to write, or null if target is an empty file/dir.
 @param isFile Set to true to create a file. Set to false to create a dir.*/
public static void createFile(@NotNull File file, @Nullable InputStream resource, boolean isFile) {
  try {
    makeRequiredFile(file, resource, isFile);
  } catch (IOException e) {
    log.log(Level.WARNING, Lang.excepts_fileCreation.getResponse(Key.filePath.replaceWith(file.getAbsolutePath())), e);
  }
}


/**
 Parses & formats data from the given inputStream to a Yaml resource.
 * @param yamlInputStream The given inputStream to a Yaml resource.
 * @return The parsed values in the format key: "test1.log" value: "works!"<br>
 * Or an empty hashMap if the given inputStream is null.
 * @throws IOException If the data couldn't be read from the given inputStream.
 */
private static @NotNull HashMap<String, Object> parseYaml(@Nullable InputStream yamlInputStream) throws IOException {
  if (yamlInputStream == null) return new HashMap<>();

  byte[] resourceBytes = yamlInputStream.readAllBytes();

  String resourceContent = new String(resourceBytes, Charset.defaultCharset());

  return getKeysRecursive(new Yaml().load(resourceContent));
}

/**
 Parses the data from an internal YAML file.
 * @param resourcePath The path to the file from /src/main/resource/
 * @return The parsed values in the format key: "test1.log" value: "works!" <br>
 * Or an empty hashMap if the file couldn't be found or read.
 */
public static @NotNull HashMap<String, Object> parseInternalYaml(@NotNull String resourcePath) {
  try (InputStream resourceInputStream = plugin.getResource(resourcePath)) {
    return parseYaml(resourceInputStream);

  } catch (IOException e) {
    log.log(Level.SEVERE, "Unable to parse internal YAML files.\nConfig & lang might break.\n", e);
    return new HashMap<>();
  }

}


/**
 Parses the given external file into a hashMap. If the internal file contained keys that the external file didn't then the key-value pare is added to the external file.
 * @param externalFile The external file to parse.
 * @param pathToInternalResource The path to the internal resource to repair it with or fallback on if the external file is broken.
 * @return The key-value pairs from the external file. If any keys were missing from the external file then they are put into the hashMap with their default value.
 */
public static @NotNull HashMap<String, Object> parseAndRepairExternalYaml(@NotNull File externalFile, @Nullable String pathToInternalResource) {
  HashMap<String,Object> externalYaml;

  //tries to parse the external file.
  try (InputStream externalInputStream = new FileInputStream(externalFile)) {
    externalYaml = parseYaml(externalInputStream);

  } catch (FileNotFoundException e) {
    log.log(Level.SEVERE, Lang.excepts_noFile.getResponse(Key.filePath.replaceWith(externalFile.getAbsolutePath())), e);

    //returns an empty hashMap or the internal values if present.
    return pathToInternalResource == null ? new HashMap<>() : parseInternalYaml(pathToInternalResource);

  } catch (IOException e) {
    log.log(Level.SEVERE, Lang.excepts_parseYaml.getResponse(Key.filePath.replaceWith(externalFile.getAbsolutePath())), e);

    //returns an empty hashMap or the internal values if present.
    return pathToInternalResource == null ? new HashMap<>() : parseInternalYaml(pathToInternalResource);
  }


  //if there is no internal resource to compare against then only the external file data is returned.
  if (pathToInternalResource == null)
    return externalYaml;

  HashMap<String,Object> internalYaml = parseInternalYaml(pathToInternalResource);

  //gets the values that the external file is missing;
  HashMap<String,Object> missingPairsMap = new HashMap<>();
  internalYaml.forEach((String key, Object value) -> {
    if (externalYaml.containsKey(key))
      return;

    missingPairsMap.put(key, value);
  });

  //if no values are missing return
  if (missingPairsMap.keySet().isEmpty())
    return externalYaml;

  //Adds all the missing key-value pairs to a stringBuilder.
  StringBuilder missingPairs = new StringBuilder("\n");
  missingPairsMap.forEach((String key, Object value) -> {
    missingPairs.append(key)
                .append(": \"")
                .append(preserveEscapedQuotes(value))
                .append("\"\n");
  });

  //Adds al the missing pairs to the external Yaml.
  externalYaml.putAll(missingPairsMap);


  //Writes the missing pairs to the external file.
  try (FileWriter externalFileWriter = new FileWriter(externalFile, true)) {
    externalFileWriter.append(missingPairs.toString());

  }catch (IOException e) {
    //Logs a warning
    log.log(Level.WARNING, Lang.excepts_fileRestore.getResponse(Key.filePath.replaceWith(externalFile.getAbsolutePath())), e);

    //Logs the keys that couldn't be appended.
    missingPairsMap.forEach((String key, Object value) -> {
      log.log(Level.WARNING, key + ": " + value);
    });
  }

  return externalYaml;
}

/**
 Object.toString() changes \" to ". This method resolves this problem.
 * @param value The object to get the string from.
 * @return The correct string from the given object.
 */
private static String preserveEscapedQuotes(Object value) {
  char[] valueCharArray = value.toString().toCharArray();
  StringBuilder correctString = new StringBuilder();


  for (char character : valueCharArray) {
    if (character != '"') {
      correctString.append(character);
      continue;
    }

    correctString.append('\\');
    correctString.append('"');
  }

  return correctString.toString();
}

}
