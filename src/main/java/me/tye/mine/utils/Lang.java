package me.tye.mine.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import static me.tye.mine.utils.Util.dataFolder;
import static me.tye.mine.utils.Util.plugin;

public enum Lang {
  noKey,
  invalidKey,

  pointer_getItem,
  pointer_confirmSelection,
  pointer_missingSelection,

  excepts_fileCreation,
  excepts_fileRestore,
  excepts_parseYaml,
  excepts_noFile;

/**
 Stores the lang values.
 */
private static final HashMap<Lang, String> langs = new HashMap<>();


/**
 Gets the string response for the selected enum.
 * @param keys The keys to modify the response with.
 * @return The modified string.
 */
public @NotNull String getResponse(@NotNull Key... keys) {
  String response = this.getResponse();

  for (Key key : keys) {
    response = response.replaceAll(key.toString(), "\\{"+key.getReplaceWith()+"}");
  }

  return response;
}

/**
 * @return The string response without any keys being modified. This is the same as getResponse();
 */
public @NotNull String getResponse() {
  String response = langs.get(this);

  assert response != null;

  return response;
}

/**
 Loads the default lang.
 */
public static void init() {
  //Falls back to english if default values can't be found.
  String resourcePath = "lang/" + Configs.lang.getStringConfig() + ".yml";
  if (plugin.getResource(resourcePath) == null) {
    resourcePath = "lang/eng.yml";
  }

  //Loads the default values into lang.
  HashMap<String,Object> internalYaml = Util.parseInternalYaml(resourcePath);
  internalYaml.forEach((String key, Object value) -> {
    String formattedKey = key.replace('.', '_');

    try {
      langs.put(Lang.valueOf(formattedKey), value.toString());

    } catch (IllegalArgumentException e) {
      //Dev warning
      throw new RuntimeException(formattedKey + " isn't present in the lang enum.");
    }
  });

  //Checks if any default values are missing.
  for (Lang lang : Lang.values()) {
    if (langs.containsKey(lang)) continue;

    //Dev warning.
    throw new RuntimeException(lang+" isn't in default lang file.");
  }
}

/**
 Loads the user - selected lang values into the lang map.<br>
 To see available languages for a version see the src/main/resources/lang/ for your version on GitHub.
 */
public static void load() {
  //No repair is attempted if the internal file can't be found.
  String resourcePath = "lang/" + Configs.lang.getStringConfig() + ".yml";
  if (plugin.getResource(resourcePath) == null) {
    resourcePath = null;
  }

  //Loads the external lang responses. No file repairing is done if an internal lang can't be found.
  File externalFile = new File(dataFolder.toPath()+File.separator+Configs.lang.getStringConfig()+".yml");
  HashMap<String,Object> externalYaml = Util.parseAndRepairExternalYaml(externalFile, resourcePath);

  externalYaml.forEach((String key, Object value) -> {
    String formattedKey = key.replace('.', '_');

    //Logs an exception if the key doesn't exist.
    try {
      langs.put(Lang.valueOf(formattedKey), value.toString());
    } catch (IllegalArgumentException e) {
      Util.log.warning(Lang.invalidKey.getResponse(Key.key.replaceWith(key)));
    }
  });
}

}
