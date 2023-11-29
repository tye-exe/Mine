package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static me.tye.mine.Mine.loadedClaims;

public class Claim {

private @NotNull UUID clanID;
private @NotNull UUID claimID;

private UUID claimPerm;
private int claimImportance;

private @NotNull String worldName;
private double X1;
private double X2;
private double Y1;
private double Y2;
private double Z1;
private double Z2;


/**
 Gets the claim from the claim ID.
 * @param claimID The ID of the claim to get.
 * @return The claim, or null if the claim doesn't exist.
 */
public static @Nullable Claim getClaim(@NotNull UUID claimID) {
  //If the claim is loaded then it gets the claim object from the HashMap.
  if (loadedClaims.containsKey(claimID)) {
    return loadedClaims.get(claimID);
  }

  //If the claim isn't loaded but exists, gets it from the database & loads it.
  if (Database.claimExists(claimID)) {
    Claim claim = Database.getClaim(claimID);
    loadedClaims.put(claimID, claim);
    return claim;
  }

  //returns null if the claim can't be found.
  return null;
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
public Claim(@NotNull UUID clanID, @NotNull String worldName, double X1, double X2, double Y1, double Y2, double Z1, double Z2) {
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
 */
public Claim(@NotNull UUID clanID, @NotNull UUID claimID, @NotNull String worldName, double X1, double X2, double Y1, double Y2, double Z1, double Z2) {
  this.clanID = clanID;
  this.claimID = claimID;
  this.worldName = worldName;
  this.X1 = X1;
  this.X2 = X2;
  this.Y1 = Y1;
  this.Y2 = Y2;
  this.Z1 = Z1;
  this.Z2 = Z2;
}

/**
 * @return True is this claim contains any part of another claim within it.
 */
public boolean isOverlapping() {
  return false;
  //TODO: implement
}

/**
 * @return True if any of the claims corners are within loaded chunks.
 */
public boolean isLoaded() {
  World world = Bukkit.getWorld(getWorldName());
  return new Location(world, getX1(), getY1(), getZ1()).isWorldLoaded()
         || new Location(world, getX2(), getY1(), getZ1()).isWorldLoaded()
         || new Location(world, getX1(), getY1(), getZ2()).isWorldLoaded()
         || new Location(world, getX2(), getY1(), getZ2()).isWorldLoaded()
         || new Location(world, getX1(), getY2(), getZ1()).isWorldLoaded()
         || new Location(world, getX2(), getY2(), getZ1()).isWorldLoaded()
         || new Location(world, getX1(), getY2(), getZ2()).isWorldLoaded()
         || new Location(world, getX2(), getY2(), getZ2()).isWorldLoaded();
}

public @NotNull UUID getClaimID() {
  return claimID;
}

public @NotNull String getWorldName() {
  return worldName;
}

public double getX1() {
  return X1;
}

public double getX2() {
  return X2;
}

public double getY1() {
  return Y1;
}

public double getY2() {
  return Y2;
}

public double getZ1() {
  return Z1;
}

public double getZ2() {
  return Z2;
}
}
