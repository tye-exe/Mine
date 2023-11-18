package me.tye.mine;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


public class Util {

public static JavaPlugin plugin = JavaPlugin.getPlugin(Mine.class);
public static NamespacedKey identifierKey = new NamespacedKey(plugin, "identifier");
public static Material selector = Material.WOODEN_SWORD;


public static ItemStack itemProperties(Material material, String displayName, String identifier) {
  ItemStack itemStack = new ItemStack(material);
  ItemMeta itemMeta = itemStack.getItemMeta();

  itemMeta.displayName(Component.text(displayName).color(Colours.Green));
  itemMeta.getPersistentDataContainer().set(identifierKey, PersistentDataType.STRING, identifier);

  itemStack.setItemMeta(itemMeta);
  return itemStack;
}

/**
 Checks if any items in a player inventory contains the given identifier.
 @param inv        The players inventory to check.
 @param identifier The give identifier.
 @return True if the inventory has an item with this identifier, false otherwise. */
public static boolean inventoryContainsIdent(PlayerInventory inv, String identifier) {
  for (ItemStack itemStack : inv) {

    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null)
      continue;

    String foundIdent = itemMeta.getPersistentDataContainer().get(identifierKey, PersistentDataType.STRING);
    if (foundIdent == null)
      continue;
    if (!foundIdent.equals(identifier))
      continue;

    return true;
  }
  return false;
}
}
