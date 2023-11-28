package me.tye.mine;

import me.tye.mine.utils.Identifier;
import me.tye.mine.utils.TempConfigsStore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.tye.mine.Selection.selections;
import static me.tye.mine.utils.Util.itemProperties;

public class Commands implements CommandExecutor {
@Override
public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
  if (args.length < 1) return true;

  if (args[0].equals("pointer")) {
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;

    if (!Identifier.playerHasIdentifier(player, "pointer")) {
      givePointer(player);
      return true;
    }

    deletePointer(player);
  }

  return true;
}

private void givePointer(@NotNull Player player) {
    ItemStack pointer = itemProperties(TempConfigsStore.pointer, "Pointer", "pointer");
    player.getInventory().addItem(pointer);
}

private void deletePointer(@NotNull Player player) {
  Identifier.playerDeleteIdentifier(player, "pointer");

  if (selections.containsKey(player.getUniqueId())) {
    selections.get(player.getUniqueId()).restore();
  }
}

}
