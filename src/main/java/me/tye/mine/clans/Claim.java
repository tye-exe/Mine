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
