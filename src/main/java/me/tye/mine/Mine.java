package me.tye.mine;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static me.tye.mine.PlayerClick.selections;

public final class Mine extends JavaPlugin {

@Override
public void onEnable() {
    // Plugin startup logic

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
