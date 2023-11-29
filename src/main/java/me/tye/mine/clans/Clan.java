package me.tye.mine.clans;

import me.tye.mine.Database;
import me.tye.mine.errors.InvalidClanCreationException;
import me.tye.mine.utils.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static me.tye.mine.Mine.loadedClans;

public class Clan {

private final UUID clanID;

private String name;
private String description;

private Collection<UUID> clanClaims = new ArrayList<>();
private Collection<UUID> clanMembers = new ArrayList<>();
private Collection<UUID> clanPerms = new ArrayList<>();


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


public Clan(@NotNull UUID clanID, @NotNull String clanName, @NotNull String clanDescription, @NotNull Collection<UUID> clanClaims, @NotNull Collection<UUID> clanMembers, @NotNull Collection<UUID> clanPerms)  {
  this.clanID = clanID;
  this.name = clanName;
  this.description = clanDescription;
  this.clanClaims =  clanClaims;
  this.clanMembers = clanMembers;
  this.clanPerms = clanPerms;
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

public ArrayList<Claim> getClanClaims() {
  ArrayList<Claim> claims = new ArrayList<>();

  clanClaims.forEach(uuid -> {
    claims.add(Claim.getClaim(uuid));
  });

  return claims;
}

public ArrayList<Member> getClanMembers() {
  ArrayList<Member> members = new ArrayList<>();

  clanMembers.forEach(uuid -> {
    members.add(Member.getMember(uuid));

  });

  return members;
}

public ArrayList<Perms> getClanPerms() {
  ArrayList<Perms> perms = new ArrayList<>();

  clanPerms.forEach(uuid -> {
    perms.add(Perms.getPerm(uuid));

  });

  return perms;
}

public ArrayList<UUID> getMemberUUIDs() {
  return clanMembers;
}

}
