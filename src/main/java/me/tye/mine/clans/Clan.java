package me.tye.mine.clans;

import me.tye.mine.Database;
import me.tye.mine.errors.InvalidClanCreationException;
import me.tye.mine.utils.Key;
import me.tye.mine.utils.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static me.tye.mine.Mine.loadedClans;

public class Clan {

private final @NotNull UUID clanID;

private @NotNull String name;
private @NotNull String description;

private @NotNull Collection<UUID> clanClaims = new ArrayList<>();
private @NotNull Collection<UUID> clanMembers = new ArrayList<>();
private @NotNull Collection<UUID> clanPerms = new ArrayList<>();


/**
 Gets the clan from the clan ID.
 * @param clanID The ID of the clan to get.
 * @return The clan, or null if the clan doesn't exist.
 */
public static @Nullable Clan getClan(@NotNull UUID clanID) {
  //If the clan is loaded then it gets the clan object from the HashMap.
  if (loadedClans.containsKey(clanID)) {
    return loadedClans.get(clanID);
  }

  //If the clan isn't loaded but exists, gets it from the database & loads it.
  if (Database.clanExists(clanID)) {
    Clan clan = Database.getClan(clanID);
    loadedClans.put(clanID, clan);
    return clan;
  }

  //returns null if the clan can't be found.
  return null;
}

/**
 Creates a new clan with the given member as the owner.
 * @param creator The given member.
 * @throws InvalidClanCreationException Thrown if the given member is already in a clan.
 */
public static void createClan(@NotNull Member creator) throws InvalidClanCreationException {
  if (creator.isInClan()) {
    throw new InvalidClanCreationException(Lang.member_alreadyInClan.getResponse(Key.member.replaceWith(creator.getPlayer().getName())));
  }

  UUID clanID = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.clanExists(clanID)) {
    clanID = UUID.randomUUID();
  }

  String clanName = "The clan of "+creator.getPlayer().getName()+".";
  String clanDescription = "The clan description of "+creator.getPlayer().getName()+".";

  Clan createdClan = new Clan(clanID, clanName, clanDescription);

  Database.createClan(createdClan);
}


/**
 Creates a new clan & clan object.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getClan(UUID)} to get a clan or {@link #createClan(Member)} to create a clan.
 * @param clanID The uuid of the new clan.
 * @param clanName The name of the new clan.
 * @param clanDescription The description of the new clan.
 */
public Clan(@NotNull UUID clanID, @NotNull String clanName, @NotNull String clanDescription)  {
  this.clanID = clanID;
  this.name = clanName;
  this.description = clanDescription;
}

/**
 Creates a new clan object for an existing clan.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getClan(UUID)} to get a clan or {@link #createClan(Member)} to create a clan.
 * @param clanID The uuid of the clan.
 * @param clanName The name of the clan.
 * @param clanDescription The description of the clan.
 * @param clanClaims The claims of the clan.
 * @param clanMembers The member of the clan.
 * @param clanPerms The perms of the clan.
 */
public Clan(@NotNull UUID clanID, @NotNull String clanName, @NotNull String clanDescription, @NotNull Collection<UUID> clanClaims, @NotNull Collection<UUID> clanMembers, @NotNull Collection<UUID> clanPerms)  {
  this.clanID = clanID;
  this.name = clanName;
  this.description = clanDescription;
}

public @NotNull String getName() {
  return name;
}

public @NotNull String getDescription() {
  return description;
}

public @NotNull UUID getClanID() {
  return clanID;
}

public @NotNull ArrayList<Claim> getClanClaims() {
  ArrayList<Claim> claims = new ArrayList<>();

  clanClaims.forEach(uuid -> {
    claims.add(Claim.getClaim(uuid));
  });

  return claims;
}

public @NotNull ArrayList<Member> getClanMembers() {
  ArrayList<Member> members = new ArrayList<>();

  clanMembers.forEach(uuid -> {
    members.add(Member.getMember(uuid));

  });

  return members;
}

public @NotNull ArrayList<Perm> getClanPerms() {
  ArrayList<Perm> perms = new ArrayList<>();

  clanPerms.forEach(uuid -> {
    perms.add(Perm.getPerm(uuid));

  });

  return perms;
}

public @NotNull Collection<UUID> getMemberUUIDs() {
  return clanMembers;
}

}
