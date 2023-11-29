package me.tye.mine;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.tye.mine.Mine.onlineMembers;
import static me.tye.mine.Selection.selections;

public class PlayerQuit implements Listener {

@EventHandler
public void playerQuit(PlayerQuitEvent e) {
  Player player = e.getPlayer();

  selections.remove(player.getUniqueId());
  onlineMembers.remove(player.getUniqueId());
}
}
