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


/**
 Sets some attributes of a new item in one method.
 * @param material The material of the new item.
 * @param displayName The display name of the item.
 * @param identifier The identifier of an item. This is used to uniquely identify types of items inside of Mine!.
 * @return A new item with the set properties.
 */
public static ItemStack itemProperties(Material material, String displayName, String identifier) {
  ItemStack itemStack = new ItemStack(material);
  ItemMeta itemMeta = itemStack.getItemMeta();

  itemMeta.displayName(Component.text(displayName).color(Colours.Green));
  itemMeta.getPersistentDataContainer().set(identifierKey, PersistentDataType.STRING, identifier);

  itemStack.setItemMeta(itemMeta);
  return itemStack;
}

/**
 * @param item The item to get the identifier from.
 * @return The identifier of an item, or an empty string is none is found.
 */
public static String getIdentifier(@Nullable ItemStack item) {
  if (item == null) return "";

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
public static boolean inventoryContainsIdentifier(PlayerInventory inv, String identifier) {
  for (ItemStack itemStack : inv) {
    String itemIdentity = getIdentifier(itemStack);

    if (itemIdentity.isEmpty()) continue;
    if (!itemIdentity.equals(identifier)) continue;

    return true;
  }
  return false;
}

/**
 Deletes an item in a players inventory if it has the given identifier.
 * @param inv The inventory to remove the item from.
 * @param identifier The identifier of items to remove from the inventory.
 */
public static void deleteItemByIdentifier(PlayerInventory inv, String identifier) {
  for (int i = 0; i < inv.getSize(); i++) {
    ItemStack item = inv.getItem(i);

    if (item == null) continue;

    String foundIdentifier = getIdentifier(item);
    if (!foundIdentifier.equals(identifier)) continue;

    inv.setItem(i, new ItemStack(Material.AIR));
  }
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
