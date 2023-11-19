package me.tye.mine;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Mine extends JavaPlugin {

@Override
public void onEnable() {
    // Plugin startup logic

    //Commands
    Objects.requireNonNull(getCommand("mine")).setExecutor(new Commands());
    Objects.requireNonNull(getCommand("mine")).setTabCompleter(new TabComplete());

    //Events
    getServer().getPluginManager().registerEvents(new PlayerClick(), this);
}

@Override
public void onDisable() {
    // Plugin shutdown logic
}
}
