package me.tye.mine;

import me.tye.mine.clans.Claim;
import me.tye.mine.clans.Clan;
import me.tye.mine.clans.Member;
import me.tye.mine.clans.Perm;
import me.tye.mine.utils.Configs;
import me.tye.mine.utils.Lang;
import me.tye.mine.utils.Unloader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static me.tye.mine.Selection.selections;
import static me.tye.mine.utils.Util.*;

public final class Mine extends JavaPlugin {

/**
 The clans that are online.
 */
public static final HashMap<UUID, Clan> loadedClans = new HashMap<>();

/**
 The claims that are loaded.
 */
public static final HashMap<UUID, Claim> loadedClaims = new HashMap<>();

/**
 Perms that are loaded.
 */
public static final HashMap<UUID, Perm> loadedPerms = new HashMap<>();

/**
 Members that are online.
 */
public static final HashMap<UUID, Member> onlineMembers = new HashMap<>();


@Override
public void onEnable() {

    //Creates required config folders & files. These files need to exist since the rest of the plugin expects them to.
    createRequiredConfigs();

    //loads default values into lang & config.
    Configs.init();
    Lang.init();

    //Loads user - selected values into lang & config.
    Configs.load();
    Lang.load();

    try {
        Database.init();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    //TODO: improve.
    Unloader.init();

    //Commands
    Objects.requireNonNull(getCommand("mine")).setExecutor(new Commands());
    Objects.requireNonNull(getCommand("mine")).setTabCompleter(new TabComplete());

    //Events
    getServer().getPluginManager().registerEvents(new PlayerClick(), this);
    getServer().getPluginManager().registerEvents(new PlayerDrop(), this);
    getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
    getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
    getServer().getPluginManager().registerEvents(new PlayerSwitch(), this);

}

@Override
public void onDisable() {
    // Plugin shutdown logic

    //Reload support - If a reload happens when blocks are selected then they are restored.
    selections.values().forEach((Selection::restore));

    //Reload support - destroys the unlaoder.
    Unloader.terminate();
}

/**
 Creates required config folders & files. These files need to exist since the rest of the plugin expects them to.
 */
private void createRequiredConfigs() {
    try {
        makeRequiredFile(dataFolder, null, false);
    } catch (IOException e) {
        throw new RuntimeException("\"" + dataFolder.getAbsolutePath() + "\" Couldn't be created. Please manually create this folder.", e);
    }

    try {
        makeRequiredFile(configFile, plugin.getResource("config.yml"), true);
    } catch (IOException e) {
        throw new RuntimeException("\"" + configFile.getAbsolutePath() + "\" Couldn't be created. Please manually create this file.", e);
    }

    try {
        makeRequiredFile(langFolder, null, false);
    } catch (IOException e) {
        throw new RuntimeException("\"" + langFolder.getAbsolutePath() + "\" Couldn't be created. Please manually create this folder.", e);
    }

    try {
        makeRequiredFile(new File(langFolder+File.separator+"eng.yml"), plugin.getResource("lang/eng.yml"), true);
    } catch (IOException e) {
        throw new RuntimeException("\"" + new File(langFolder+File.separator+"eng.yml").getAbsolutePath() + "\" Couldn't be created. Please manually create this folder.", e);
    }
}

}
