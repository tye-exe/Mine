package me.tye.mine;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static me.tye.mine.PlayerClick.selections;
import static me.tye.mine.Util.dropRetryInterval;
import static me.tye.mine.Util.getIdentifier;
public class PlayerDrop implements Listener {

@EventHandler
public static void playerDrop(PlayerDropItemEvent e) {
  Item droppedItem = e.getItemDrop();
  Player player = e.getPlayer();

  if (dropPointer(droppedItem, player)) {
    return;
  }

  if (Util.isMineItem(droppedItem.getItemStack())) {
    e.setCancelled(true);
  }
}

/**
 Stores the last time a player tried to drop the pointer item.
 */
public static HashMap<UUID, Long> pointerDrop = new HashMap<>();

/**
 Performs the drop checks for the pointer item. Then deletes the pointer instead of throwing it.
 @param droppedItem The item to drop.
 @param player      The player who is dropping the item. */
private static boolean dropPointer(@NotNull Item droppedItem, @NotNull Player player) {
  if (!getIdentifier(droppedItem.getItemStack()).equals("pointer")) return false;

  if (!pointerDrop.containsKey(player.getUniqueId())) {
    pointerDrop.put(player.getUniqueId(), System.currentTimeMillis());
    return false;
  }

  Long lastDropAttempt = pointerDrop.get(player.getUniqueId());

  if (System.currentTimeMillis() - dropRetryInterval > lastDropAttempt) {
    pointerDrop.put(player.getUniqueId(), System.currentTimeMillis());
    return false;
  }

  pointerDrop.remove(player.getUniqueId());
  droppedItem.remove();

  if (selections.containsKey(player.getUniqueId())) {
    selections.remove(player.getUniqueId()).restore();
  }

  return true;
}
}
