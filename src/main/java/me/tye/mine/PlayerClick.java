package me.tye.mine;

import me.tye.mine.utils.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.tye.mine.utils.Util.*;

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


  Selection selected = new Selection(player.getUniqueId());
  if (Selection.selections.containsKey(player.getUniqueId())) {
    selected = Selection.selections.get(player.getUniqueId());
  }

  Selection.selections.put(player.getUniqueId(), selected.setLocation(clickedBlock.getLocation(), action));


  //if the block is the same then use secondary
  BlockData cornerBlock = Material.MAGENTA_GLAZED_TERRACOTTA.createBlockData();

  if (clickedBlock.getType().equals(cornerBlock.getMaterial())) {
    cornerBlock = Material.PEARLESCENT_FROGLIGHT.createBlockData();
  }

  if (action.isRightClick()) {
    cornerBlock = Material.ORANGE_GLAZED_TERRACOTTA.createBlockData();

    //if the block is the same then use secondary
    if (clickedBlock.getType().equals(cornerBlock.getMaterial())) {
      cornerBlock = Material.OCHRE_FROGLIGHT.createBlockData();
    }
  }

  //sendBlockChange needs to be in a runnable as the server sends a block update packet to the client after processing this event.
  final BlockData changedBlock = cornerBlock;
  Bukkit.getScheduler().runTaskLater(plugin, () -> {
    player.sendBlockChange(clickedBlock.getLocation(), changedBlock);

    //makes the selected block glow.
    for (Block block : getSurrounding(clickedBlock, Material.AIR)) {
      player.sendBlockChange(block.getLocation(), Material.LIGHT.createBlockData());
    }

  }, 1);

}

}
