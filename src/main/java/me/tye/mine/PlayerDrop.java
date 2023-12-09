package me.tye.mine;

import me.tye.mine.clans.Clan;
import me.tye.mine.clans.Member;
import me.tye.mine.utils.Identifier;
import me.tye.mine.utils.Key;
import me.tye.mine.utils.Lang;
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
import static me.tye.mine.utils.TempConfigsStore.dropRetryInterval;

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
    player.sendMessage(Lang.pointer_missingSelection.getResponse());
    return false;
  }

  Selection selection = selections.get(playerId);
  if (!(selection.hasSelection())) {
    player.sendMessage(Lang.pointer_missingSelection.getResponse());
    return false;
  }


  if (!pointerDrop.containsKey(playerId)) {
    pointerDrop.put(playerId, System.currentTimeMillis());
    player.sendMessage(Lang.pointer_confirmSelection.getResponse());
    return false;
  }

  Long lastDropAttempt = pointerDrop.get(playerId);

  if (System.currentTimeMillis() - dropRetryInterval > lastDropAttempt) {
    pointerDrop.put(playerId, System.currentTimeMillis());
    player.sendMessage(Lang.pointer_confirmSelection.getResponse());
    return false;
  }

  pointerDrop.remove(playerId);

  Member member = Member.getMember(playerId);
  if (member == null) {
    player.sendMessage(Lang.database_noMember.getResponse(Key.member.replaceWith(player.getName())));
    return false;
  }

  selections.remove(playerId).restore();
  droppedItem.remove();
  member.unoutlineClaims();

  if (!member.isInClan()) {
    Clan clan = Clan.createClan(member);

    if (clan == null) {
      player.sendMessage(Lang.database_noClan.getResponse(Key.member.replaceWith(player.getName())));
      return true;
    }

    clan.addClaim(selection.getStartLoc(), selection.getEndLoc());
    return true;
  }

  UUID clanID = member.getClanID();
  if (clanID == null) {
    player.sendMessage(Lang.database_noClan.getResponse(Key.member.replaceWith(player.getName())));
    return true;
  }

  Clan clan = Clan.getClan(clanID);
  if (clan == null) {
    player.sendMessage(Lang.database_noClan.getResponse(Key.member.replaceWith(player.getName())));
    return true;
  }

  clan.addClaim(selection.getStartLoc(), selection.getEndLoc());
  clan.save();


  Sounds.confirm(player);
  return true;
}
}
