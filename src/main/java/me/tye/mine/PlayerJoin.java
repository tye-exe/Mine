package me.tye.mine;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.tye.mine.Database.memberCache;

public class PlayerJoin implements Listener {

@EventHandler
public static void playerJoin(PlayerJoinEvent e) {
  Player player = e.getPlayer();

  if (!Database.memberExists(player.getUniqueId())) {
    Database.createMember(player.getUniqueId());
  }

  memberCache.put()

}
}
