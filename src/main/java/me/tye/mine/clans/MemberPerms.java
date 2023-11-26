package me.tye.mine.clans;

import org.bukkit.Material;

import java.util.ArrayList;

public class MemberPerms implements Perms {

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
