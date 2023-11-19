package me.tye.mine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.tye.mine.Util.itemProperties;

public class Commands implements CommandExecutor {
@Override
public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
  if (args.length < 1) return true;

  if (args[0].equals("pointer")) {
    givePointer(sender);
  }

  return true;
}

private void givePointer(CommandSender sender) {
  if (sender instanceof Player player) {

    ItemStack pointer = itemProperties(Util.pointer, "Pointer", "pointer");
    if (Util.inventoryContainsIdententity(player.getInventory(), "pointer")) return;

    player.getInventory().addItem(pointer);
  }
}

}
