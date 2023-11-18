package me.tye.mine;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.tye.mine.Util.itemProperties;
import static me.tye.mine.Util.selector;

public class Commands implements CommandExecutor {
@Override
public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
  if (args.length < 1)
    return true;

  if (args[0].equals("select")) {
    giveSelector(sender);
  }

  return true;
}

private void giveSelector(CommandSender sender) {
  if (sender instanceof Player player) {

    ItemStack selector = itemProperties(Util.selector, "Selector", "selector");
    if (Util.inventoryContainsIdent(player.getInventory(), "selector"))
      return;

    player.getInventory().addItem(selector);
  }
}

}
