package me.tye.mine;

import me.tye.mine.utils.Identifier;
import me.tye.mine.utils.Sounds;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static me.tye.mine.Selection.selections;
import static me.tye.mine.utils.Util.*;

public class PlayerDrop implements Listener {

@EventHandler
public static void playerDrop(PlayerDropItemEvent e) {
  Item droppedItem = e.getItemDrop();
  Player player = e.getPlayer();

  if (dropPointer(droppedItem, player)) {
    return;
  }

  if (Identifier.isMineItem(droppedItem.getItemStack())) {
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
  if (!Identifier.getIdentifier(droppedItem.getItemStack()).equals("pointer")) return false;

  UUID playerId = player.getUniqueId();

  if (!selections.containsKey(playerId)) {
    player.sendMessage(getLang("pointer.missingSelection"));
    return false;
  }

  Selection selection = selections.get(playerId);
  if (!(selection.hasSetStartLocation() && selection.hasSetEndLocation())) {
    player.sendMessage(getLang("pointer.missingSelection"));
    return false;
  }


  if (!pointerDrop.containsKey(playerId)) {
    pointerDrop.put(playerId, System.currentTimeMillis());
    player.sendMessage(getLang("pointer.confirmSelection"));
    return false;
  }

  Long lastDropAttempt = pointerDrop.get(playerId);

  if (System.currentTimeMillis() - dropRetryInterval > lastDropAttempt) {
    pointerDrop.put(playerId, System.currentTimeMillis());
    player.sendMessage(getLang("pointer.confirmSelection"));
    return false;
  }

  selections.remove(playerId).restore();
  pointerDrop.remove(playerId);
  droppedItem.remove();


  Sounds.confirm(player);
  return true;
}
}
