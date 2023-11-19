package me.tye.mine;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class Util {

public static JavaPlugin plugin = JavaPlugin.getPlugin(Mine.class);
public static NamespacedKey identifierKey = new NamespacedKey(plugin, "identifier");
public static Material pointer = Material.WOODEN_SWORD;


public static ItemStack itemProperties(Material material, String displayName, String identifier) {
  ItemStack itemStack = new ItemStack(material);
  ItemMeta itemMeta = itemStack.getItemMeta();

  itemMeta.displayName(Component.text(displayName).color(Colours.Green));
  itemMeta.getPersistentDataContainer().set(identifierKey, PersistentDataType.STRING, identifier);

  itemStack.setItemMeta(itemMeta);
  return itemStack;
}

public static String getIdentity(ItemStack item) {
  ItemMeta itemMeta = item.getItemMeta();
  if (itemMeta == null) return "";

  String identifier = itemMeta.getPersistentDataContainer().get(identifierKey, PersistentDataType.STRING);
  if (identifier == null) return  "";

  return identifier;
}


/**
 Checks if any items in a player inventory contains the given identifier.
 @param inv        The players inventory to check.
 @param identifier The give identifier.
 @return True if the inventory has an item with this identifier, false otherwise. */
public static boolean inventoryContainsIdentity(PlayerInventory inv, String identifier) {
  for (ItemStack itemStack : inv) {
    String itemIdentity = getIdentity(itemStack);

    if (itemIdentity.isEmpty()) continue;
    if (!itemIdentity.equals(identifier)) continue;

    return true;
  }
  return false;
}


/**
 Checks if an item was created by mine by checking if it has the persistent data of the identifier key.
 * @param item The item to check if it is from Mine.
 * @return True if it is from Mine.
 */
public static boolean isMineItem(ItemStack item) {
  ItemMeta itemMeta = item.getItemMeta();
  if (itemMeta == null) return false;

  String identifier = itemMeta.getPersistentDataContainer().get(identifierKey, PersistentDataType.STRING);
  return identifier != null;
}

/**
 This method <b>does</b> take into account diagonal blocks.
 * @param block The block to check the neighbours of.
 * @param material Only returns the neighboring blocks with this material. If this is set to null, then all the surrounding blocks will be returned.
 * @return A list of blocks directly touching the given block that have the given material.
 */
public static @NotNull List<Block> getSurrounding(@NotNull Block block, @Nullable Material material) {
  List<Block> surrounding = new ArrayList<>();

  Location cornerLocation = block.getLocation().subtract(1, 1, 1);

  //loops over all the blocks surrounding the given one
  for (int x = 0; x < 3; x++) {
    for (int y = 0; y < 3; y++) {
      for (int z = 0; z < 3; z++) {
        Location checkingLocation = cornerLocation.clone().add(x, y, z);

        if (checkingLocation.equals(block.getLocation())) continue;

        if (material == null || checkingLocation.getBlock().getType().equals(material)) {
          surrounding.add(checkingLocation.getBlock());
        }

      }
    }
  }

  return surrounding;
}

}
