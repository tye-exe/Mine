package me.tye.mine;

import me.tye.mine.utils.Identifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerClick implements Listener {

@EventHandler
public static void playerClick(PlayerInteractEvent e) {
  Player player = e.getPlayer();

  ItemStack heldItem = player.getInventory().getItemInMainHand();
  String identity = Identifier.getIdentifier(heldItem);
  if (identity.isEmpty()) return;

  if (identity.equals("pointer")) {
    select(e);
  }


}

private static void select(@NotNull PlayerInteractEvent e) {
  Player player = e.getPlayer();
  Block clickedBlock = e.getClickedBlock();
  Action action = e.getAction();

  if (clickedBlock == null) return;


  Selection selected = new Selection(player);
  if (Selection.selections.containsKey(player.getUniqueId())) {
    selected = Selection.selections.get(player.getUniqueId());
  }

  Selection.selections.put(player.getUniqueId(), selected.setLocation(clickedBlock.getLocation(), action));

}

}
