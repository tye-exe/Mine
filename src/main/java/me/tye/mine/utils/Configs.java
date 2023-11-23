package me.tye.mine.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

public enum Configs {

  lang;


/**
 Stores the configs for this plugin.
 */
private static final HashMap<Configs, Object> configs = new HashMap<>();


/**
 * @return Gets the config response for the selected enum.
 */
public @NotNull Object getConfig() {
  //Makes sure lang value always exists so that responses can happen.
  if (!configs.containsKey(Configs.lang)) {
    configs.put(Configs.lang, "eng");
    Util.log.warning(Lang.noKey.getResponse(Key.key.replaceWith(Configs.lang.toString())));
  }

  Object response = configs.get(this);

  if (response == null) {
    throw new RuntimeException("Unable to find key \""+this+"\" in config file.\nPlease inform the devs about this.");
  }

  return response;
}

/**
 * @return Gets the config response for the selected enum wrapped with String.valueOf().
 */
public @NotNull String getStringConfig() {
  return String.valueOf(getConfig());
}

/**
 Loads the default configs.
 */
public static void init() {
  //Loads the default values into the config.
  HashMap<String,Object> internalConfig = Util.parseInternalYaml("config.yml");
  internalConfig.forEach((String key, Object value) -> {
    String formattedKey = key.replace('.', '_');

    try {
      configs.put(Configs.valueOf(formattedKey), value);
    } catch (IllegalArgumentException e) {
      //Dev warning
      throw new RuntimeException(formattedKey + " isn't in default config file.");
    }
  });

  //Checks if any default values are missing.
  for (Configs config : Configs.values()) {
    if (configs.containsKey(config)) continue;

    //Dev warning.
    throw new RuntimeException(config+" isn't in default config file.");
  }
}

/**
  Puts the user - specified responses into the configs.
 */
public static void load() {
  //Loads in the user-set configs.
  File externalConfigFile = new File(Util.dataFolder.toPath()+File.separator+"config.yml");
  HashMap<String,Object> externalConfigs = Util.parseAndRepairExternalYaml(externalConfigFile, "config.yml");

  externalConfigs.forEach((String key, Object value) -> {
    String formattedKey = key.replace('.', '_');

    //logs an exception if the key doesn't exist.
    try {
      Configs config = Configs.valueOf(formattedKey);
      configs.put(config, value);
    } catch (IllegalArgumentException e) {
      Util.log.warning(Lang.invalidKey.getResponse(Key.key.replaceWith(key)));
    }
  });
}

}
