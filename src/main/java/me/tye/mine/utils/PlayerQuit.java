package me.tye.mine.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

@EventHandler
public void playerQuit(PlayerQuitEvent e) {
  Player player = e.getPlayer();


}
}
