package me.tye.mine.clans;

import me.tye.mine.Database;
import me.tye.mine.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static me.tye.mine.utils.Util.getBetween;
import static me.tye.mine.utils.Util.rearrangeCorners;

public class Claim {

private @NotNull UUID clanID;
private @NotNull UUID claimID;

private UUID claimPerm;
private int claimImportance;

private @NotNull String worldName;
private int X1;
private int X2;
private int Y1;
private int Y2;
private int Z1;
private int Z2;

private @NotNull HashSet<Long> chunkKeys;

/**
 Gets the claim from the claim ID.
 * @param claimID The ID of the claim to get.
 * @return The claim, or null if the claim doesn't exist.
 */
public static @Nullable Claim getClaim(@NotNull UUID claimID) {
  //If the claim doesn't exist return null
  if (!Database.claimExists(claimID)) return null;

  return Database.getClaim(claimID);
}


/**
 Creates a new claim & claim object.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getClaim(UUID)} to get a claim.
 * @param clanID The UUID of the clan this claim belongs to.
 * @param worldName The name of the world that this claim is in.
 * @param X1 The x position of the first corner of the claim.
 * @param X2 The x position of the second corner of the claim.
 * @param Y1 The y position of the first corner of the claim.
 * @param Y2 The y position of the second corner of the claim.
 * @param Z1 The z position of the first corner of the claim.
 * @param Z2 The z position of the second corner of the claim.
 */
public Claim(@NotNull UUID clanID, @NotNull String worldName, int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.claimExists(uuid)) {
    uuid = UUID.randomUUID();
  }
  claimID = uuid;

  this.clanID = clanID;
  this.worldName = worldName;
  this.X1 = X1;
  this.X2 = X2;
  this.Y1 = Y1;
  this.Y2 = Y2;
  this.Z1 = Z1;
  this.Z2 = Z2;

  this.chunkKeys = Util.getCoveredChunks(getCornerOne(), getCornerTwo());
}


/**
 Creates a new claim object for an existing claim.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getClaim(UUID)} to get a claim.
 * @param clanID The UUID of the clan this claim belongs to.
 * @param claimID The UUID of this claim.
 * @param worldName The name of the world that this claim is in.
 * @param X1 The x position of the first corner of the claim.
 * @param X2 The x position of the second corner of the claim.
 * @param Y1 The y position of the first corner of the claim.
 * @param Y2 The y position of the second corner of the claim.
 * @param Z1 The z position of the first corner of the claim.
 * @param Z2 The z position of the second corner of the claim.
 * @param chunkKeys The keys to the chunks that this claim is present in.
 */
public Claim(@NotNull UUID clanID, @NotNull UUID claimID, @NotNull String worldName, int X1, int X2, int Y1, int Y2, int Z1, int Z2, @NotNull HashSet<Long> chunkKeys) {
  this.clanID = clanID;
  this.claimID = claimID;
  this.worldName = worldName;
  this.X1 = X1;
  this.X2 = X2;
  this.Y1 = Y1;
  this.Y2 = Y2;
  this.Z1 = Z1;
  this.Z2 = Z2;
  this.chunkKeys = chunkKeys;
}

/**
 * @return True is this claim contains any part of another claim within it.
 */
public boolean isOverlapping() {
  return false;
  //TODO: implement
}

public @NotNull UUID getClaimID() {
  return claimID;
}

public @NotNull String getWorldName() {
  return worldName;
}

/**
 * @return The world this claim is in. Or null if the world can't be found.
 */
public @Nullable World getWorld() {
  return Bukkit.getWorld(getWorldName());
}

public int getX1() {
  return X1;
}

public int getX2() {
  return X2;
}

public int getY1() {
  return Y1;
}

public int getY2() {
  return Y2;
}

public int getZ1() {
  return Z1;
}

public int getZ2() {
  return Z2;
}

public @NotNull UUID getClanID() {
  return clanID;
}

/**
 * @return The clan this claim is part of or null if the claim can't be found.
 */
public @Nullable Clan getClan() {
  return Clan.getClan(clanID);
}

public UUID getClaimPerm() {
  return claimPerm;
}

public int getClaimImportance() {
  return claimImportance;
}

public @NotNull HashSet<Long> getChunkKeys() {
  return chunkKeys;
}

/**
 * @return The minecraft location of the first corner.
 */
public @NotNull Location getCornerOne() {
  return new Location(Bukkit.getWorld(getWorldName()), getX1(), getY1(), getZ1());
}

/**
 * @return The minecraft location of the second corner.
 */
public @NotNull Location getCornerTwo() {
  return new Location(Bukkit.getWorld(getWorldName()), getX2(), getY2(), getZ2());
}

/**
 * @return The full outline of the area the claim covers.
 */
public @NotNull List<Location> getRawOutline() {
  List<Location> rawOutline = new ArrayList<>();

  World world = getWorld();

  int startX = getX1();
  int startY = getY1();
  int startZ = getZ1();

  int endX = getX2();
  int endY = getY2();
  int endZ = getZ2();

  rawOutline.add(getCornerOne());
  rawOutline.add(getCornerTwo());


  //adds the outline for the X blocks
  getBetween(startX, endX).forEach((X) -> {
    rawOutline.add(new Location(world, X, startY, startZ));
    rawOutline.add(new Location(world, X, endY, endZ));
    rawOutline.add(new Location(world, X, startY, endZ));
    rawOutline.add(new Location(world, X, endY, startZ));
  });

  //adds the outline for the Y blocks
  getBetween(startY, endY).forEach((Y) -> {
    rawOutline.add(new Location(world, startX, Y, startZ));
    rawOutline.add(new Location(world, endX, Y, endZ));
    rawOutline.add(new Location(world, startX, Y, endZ));
    rawOutline.add(new Location(world, endX, Y, startZ));
  });

  //adds the outline for the Z blocks
  getBetween(startZ, endZ).forEach((Z) -> {
    rawOutline.add(new Location(world, startX, startY, Z));
    rawOutline.add(new Location(world, endX, endY, Z));
    rawOutline.add(new Location(world, startX, endY, Z));
    rawOutline.add(new Location(world, endX, startY, Z));
  });

  return rawOutline;
}

/**
 Gets the outline of this claim that is within the cube given.
 * @param firstCorner The first corner of the cube.
 * @param secondCorner The second corner of the cube.
 * @return The outline of this claim that is within the given cube.
 */
public @NotNull List<Location> getOutlineWithin(@NotNull Location firstCorner, @NotNull Location secondCorner) {
  List<Location> outline = getRawOutline();

  Location[] locations = rearrangeCorners(firstCorner, secondCorner);
  Location cornerOne = locations[0];
  Location cornerTwo = locations[1];

  //removes locations from the outline that aren't within the cube given.
  for (int i = 0; i < outline.size(); i++) {
    Location location = outline.get(i);

    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();

    if (cornerOne.getBlockX() >= x) continue;
    if (cornerOne.getBlockY() >= y) continue;
    if (cornerOne.getBlockZ() >= z) continue;

    if (cornerTwo.getBlockX() < x) continue;
    if (cornerTwo.getBlockY() < y) continue;
    if (cornerTwo.getBlockZ() < z) continue;

    outline.remove(i);
    i--;
  }

  return outline;
}
}