package me.tye.mine;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Mine extends JavaPlugin {

@Override
public void onEnable() {
    // Plugin startup logic

    Objects.requireNonNull(getCommand("mine")).setExecutor(new Commands());
}

@Override
public void onDisable() {
    // Plugin shutdown logic
}
}
