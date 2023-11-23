package me.tye.mine.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Identifier {

/**
 * @param item The item to get the identifier from.
 * @return The identifier of an item, or an empty string is none is found.
 */
public static @NotNull String getIdentifier(@Nullable ItemStack item) {
  if (item == null) return "";

  ItemMeta itemMeta = item.getItemMeta();
  if (itemMeta == null) return "";

  String identifier = itemMeta.getPersistentDataContainer().get(Util.identifierKey, PersistentDataType.STRING);
  if (identifier == null) return  "";

  return identifier;
}

/**
 Checks if any items in the given players inventory contains the given identifier.
 @param player The given player
 @param identifier The give identifier.
 @return True if the inventory has an item with this identifier, false otherwise. */
public static boolean playerHasIdentifier(@NotNull Player player, @Nullable String identifier) {
  return inventoryContainsIdentifier(player.getInventory(), identifier);
}

/**
 Checks if any items in an inventory contains the given identifier.
 @param inv        The inventory to check.
 @param identifier The give identifier.
 @return True if the inventory has an item with this identifier, false otherwise. */
public static boolean inventoryContainsIdentifier(@NotNull Inventory inv, @Nullable String identifier) {
  for (ItemStack itemStack : inv) {
    String itemIdentity = getIdentifier(itemStack);

    if (itemIdentity.isEmpty()) continue;
    if (!itemIdentity.equals(identifier)) continue;

    return true;
  }
  return false;
}

/**
 Deletes items from the given players inventory that contain the given identifier.
 * @param player The given player.
 * @param identifier The given identifier.
 */
public static void playerDeleteIdentifier(@NotNull Player player, @Nullable String identifier) {
  deleteItemByIdentifier(player.getInventory(), identifier);
}

/**
 Deletes an item in an inventory if it has the given identifier.
 * @param inv The inventory to remove the item from.
 * @param identifier The identifier of items to remove from the inventory.
 */
public static void deleteItemByIdentifier(@NotNull Inventory inv, @Nullable String identifier) {
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
public static boolean isMineItem(@Nullable ItemStack item) {
  if (item == null) return false;

  ItemMeta itemMeta = item.getItemMeta();
  if (itemMeta == null) return false;

  String identifier = itemMeta.getPersistentDataContainer().get(Util.identifierKey, PersistentDataType.STRING);
  return identifier != null;
}

}
