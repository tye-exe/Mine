package me.tye.mine.clans;

import me.tye.mine.Database;
import me.tye.mine.errors.InvalidClanCreationException;
import me.tye.mine.utils.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.tye.mine.Mine.loadedClans;

public class Clan {

private final UUID clanID;

private String name;
private String description;

private final HashMap<UUID, Claim> clanClaims = new HashMap<>();
private final ArrayList<UUID> clanMembers = new ArrayList<>();
private final ArrayList<UUID> clanPerms = new ArrayList<>();


/**
 Gets the clan from the clan ID.
 * @param clanID The ID of the clan to get.
 * @return The clan, or null if the clan doesn't exist.
 */
public static @Nullable Clan getClan(UUID clanID) {
  //If the clan is loaded then it gets the clan object from the HashMap.
  if (loadedClans.containsKey(clanID)) {
    return loadedClans.get(clanID);
  }

  //If the clan isn't loaded but exists, gets it from the database & loads it.
  if (Database.memberExists(clanID)) {
    Clan clan = Database.getClan(clanID);
    loadedClans.put(clanID, clan);
    return clan;
  }

  //returns null if the clan can't be found.
  return null;
}

public static Clan createClan(@Nullable Claim firstClaim, @NotNull Member owner) throws InvalidClanCreationException {
  if (firstClaim != null && firstClaim.isOverlapping()) {
    throw new InvalidClanCreationException(Lang.claim_areaAlreadyClaimed.getResponse());
  }

  if (owner.isInClan()) {
    throw new InvalidClanCreationException(Lang.member_alreadyInClan.getResponse());
  }


  UUID clanID = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.clanExists(clanID)) {
    clanID = UUID.randomUUID();
  }

  String clanName = "The clan of "+owner.getPlayer().getName()+".";
  String clanDescription = "The clan description of "+owner.getPlayer().getName()+".";

  Collection<Claim> claims = new ArrayList<>();
  if (firstClaim != null) {
    claims.add(firstClaim);
  }


  Clan clan = new Clan(clanID, clanName, clanDescription, claims, List.of(owner), List.of());
  Database.createClan(clan);
  loadedClans.put(clanID, clan);
  return clan;
}


public Clan(@NotNull UUID clanID, @NotNull String clanName, @NotNull String clanDescription, @NotNull Collection<Claim> clanClaims, @NotNull Collection<Member> clanMembers, @NotNull Collection<Perms> clanPerms)  {
  this.clanID = clanID;
  this.name = clanName;
  this.description = clanDescription;

  clanClaims.forEach(claim -> {
    this.clanClaims.put(claim.getClaimID(), claim);
  });

  clanMembers.forEach((member -> {
    this.clanMembers.add(member.getMemberID());
  }));

  clanPerms.forEach((perm -> {
    this.clanPerms.add(perm.getPermID());
  }));
}

public String getName() {
  return name;
}

public String getDescription() {
  return description;
}

public UUID getClanID() {
  return clanID;
}

public HashMap<UUID, Claim> getClanClaims() {
  return clanClaims;
}

public ArrayList<UUID> getClanMembers() {
  return clanMembers;
}

public ArrayList<UUID> getClanPerms() {
  return clanPerms;
}

}
