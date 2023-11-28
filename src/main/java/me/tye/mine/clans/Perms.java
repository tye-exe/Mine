package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.UUID;

public class Perms {

private final UUID permID;

private String name;
private String description;


public Perms() {
  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.permExists(uuid)) {
    uuid = UUID.randomUUID();
  }
  permID = uuid;
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
public boolean canBreak(Material material) {
  return breakableBlocksIsWhitelist == breakBlocks.contains(material);
}

}
