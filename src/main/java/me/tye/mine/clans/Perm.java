package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class Perm {

private final UUID permID;

private String name;
private String description;

/**
 Gets the perm from the permID.
 * @param permID The uuid of the perm.
 * @return The perm with this uuid, or null if non exists.
 */
public static @Nullable Perm getPerm(@NotNull UUID permID) {
  //If the perm doesn't exist return null
  if (!Database.permExists(permID)) return null;

  return Database.getPerm(permID);
}


/**
 Creates a new perm object for an existing perm.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getPerm(UUID)} to get a perm.
 * @param name The name of the perm.
 * @param description The description of the perm.
 */
public Perm(@NotNull String name, @NotNull String description) {
  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.permExists(uuid)) {
    uuid = UUID.randomUUID();
  }

  this.permID = uuid;
  this.name = name;
  this.description = description;
}


/**
 Creates a new perm object for an existing perm.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getPerm(UUID)} to get a perm.
 * @param permID The uuid of the perm.
 * @param name The name of the perm.
 * @param description The description of the perm.
 */
public Perm(@NotNull UUID permID, @NotNull String name, @NotNull String description) {
  this.permID = permID;
  this.name = name;
  this.description = description;
}

public UUID getPermID() {
  return permID;
}


boolean breakableBlocksIsWhitelist = true;
private final ArrayList<Material> breakBlocks = new ArrayList<>();

/**
 Checks if this perm would allow for a material of block to be broken.
 * @param material The material to check.
 * @return True if the material can be broken.
 */
public boolean canBreak(@NotNull Material material) {
  return breakableBlocksIsWhitelist == breakBlocks.contains(material);
}

}
