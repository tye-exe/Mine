package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static me.tye.mine.Mine.loadedClaims;

public class Claim {

private UUID claimID;

private Perms claimPerms;
private int claimImportance;

private String worldName;

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
public static @Nullable Claim getClaim(UUID claimID) {
  //If the claim is loaded then it gets the claim object from the HashMap.
  if (loadedClaims.containsKey(claimID)) {
    return loadedClaims.get(claimID);
  }

  //If the claim isn't loaded but exists, gets it from the database & loads it.
  if (Database.memberExists(claimID)) {
    Claim claim = Database.getClaim(claimID);
    loadedClaims.put(claimID, claim);
    return claim;
  }

  //returns null if the claim can't be found.
  return null;
}


public Claim() {
  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.claimExists(uuid)) {
    uuid = UUID.randomUUID();
  }
  claimID = uuid;


}


/**
 * @return True is this claim contains any part of another claim within it.
 */
public boolean isOverlapping() {
  return;
}

/**
 * @return True if the any of the claims corners are within loaded chunks.
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

public UUID getClaimID() {
  return claimID;
}

public String getWorldName() {
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
