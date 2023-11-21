package me.tye.mine;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

import static me.tye.mine.Selection.selections;
import static me.tye.mine.utils.Util.*;

public final class Mine extends JavaPlugin {

@Override
public void onEnable() {
    // Plugin startup logic

    //Creates config folders & files.
    createFile(dataFolder, null, false);
    createFile(configFile, plugin.getResource("config.yml"), true);
    createFile(new File(langFolder + File.separator + "eng.yml"), plugin.getResource("config.yml"), true);

    //Commands
    Objects.requireNonNull(getCommand("mine")).setExecutor(new Commands());
    Objects.requireNonNull(getCommand("mine")).setTabCompleter(new TabComplete());

    //Events
    getServer().getPluginManager().registerEvents(new PlayerClick(), this);
    getServer().getPluginManager().registerEvents(new PlayerDrop(), this);

}

@Override
public void onDisable() {
    // Plugin shutdown logic

    //Reload support - If a reload happens when blocks are selected then they are restored.
    selections.values().forEach((Selection::restore));
}
}
