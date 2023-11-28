package me.tye.mine.clans;

import me.tye.mine.Database;

import java.util.UUID;

public class Claim {

private UUID claimID;

private Perms claimPerms;
private int claimImportance;

private double X1;
private double X2;
private double Y1;
private double Y2;
private double Z1;
private double Z2;


///**
// Gets the claim from the claim ID.
// * @param claimID The ID of the claim to get.
// * @return The claim, or null if the claim doesn't exist.
// */
//public static @Nullable Claim getClaim(UUID claimID) {
//  //If the claim is loaded then it gets the claim object from the HashMap.
//  if (onlineMembers.containsKey(claimID)) {
//    return loadedClans.get(claimID);
//  }
//
//  //If the clan isn't loaded but exists, gets it from the database & loads it.
//  if (Database.memberExists(claimID)) {
//    Clan clan = Database.getClan(claimID);
//    loadedClans.put(claimID, clan);
//    return clan;
//  }
//
//  //returns null if the clan can't be found.
//  return null;
//}


public Claim() {
  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.claimExists(uuid)) {
    uuid = UUID.randomUUID();
  }
  claimID = uuid;

  
}

public UUID getClaimID() {
  return claimID;
}

/**
 * @return True is this claim contains any part of another claim within it.
 */
public boolean isOverlapping() {
  return;
}
}
