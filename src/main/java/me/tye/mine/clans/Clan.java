package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static me.tye.mine.Mine.loadedClans;
import static me.tye.mine.Mine.onlineMembers;
import static me.tye.mine.utils.TempConfigsStore.outlineMaterial;

public class Clan {

private final @NotNull UUID clanID;

private @NotNull String name;
private @NotNull String description;
private @NotNull Material renderingOutline = outlineMaterial;

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
 Saves the changes made to the clan.
 */
public void save() {
  loadedClans.put(clanID, this);
  Database.updateClan(this);
}


/**
 Creates a new clan with the given member as the owner.<br>
 <b>If the member is already in a clan then this method will return null.</b>
 * @param creator The given member.
 * @return The new clan, or null if the member is already in a clan.
 */
public static @Nullable Clan createClan(@NotNull Member creator) {
  if (creator.isInClan()) return null;

  UUID clanID = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.clanExists(clanID)) {
    clanID = UUID.randomUUID();
  }

  Clan createdClan = new Clan(clanID, creator);

  Database.createClan(createdClan);
  //invalidate the member cache since the member is now in a clan.
  onlineMembers.remove(creator.getMemberID());

  return createdClan;
}


/**
 Creates a new clan & clan object.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getClan(UUID)} to get a clan or {@link #createClan(Member)} to create a clan.
 * @param clanID The uuid of the new clan.
 * @param creator The creator of the new clan.
 */
public Clan(@NotNull UUID clanID, @NotNull Member creator)  {
  this.clanID = clanID;
  this.clanMembers.add(creator.getClanID());
  this.name = "The clan of "+creator.getOfflinePlayer().getName()+".";
  this.description = "The clan description of "+creator.getOfflinePlayer().getName()+".";
}

/**
 Creates a new clan object for an existing clan.<br>
 <b>This method is not intended for general use.</b> Please use {@link #getClan(UUID)} to get a clan or {@link #createClan(Member)} to create a clan.
 * @param clanID The uuid of the clan.
 * @param clanClaims The claims of the clan.
 * @param clanMembers The member of the clan.
 * @param clanPerms The perms of the clan.
 * @param clanName The name of the clan.
 * @param clanDescription The description of the clan.
 * @param renderingOutline The material to render the outline of this clan with.
 */
public Clan(@NotNull UUID clanID, @NotNull Collection<UUID> clanClaims, @NotNull Collection<UUID> clanMembers, @NotNull Collection<UUID> clanPerms, @NotNull String clanName, @NotNull String clanDescription, @NotNull Material renderingOutline)  {
  this.clanID = clanID;
  this.clanClaims = clanClaims;
  this.clanMembers = clanMembers;
  this.clanPerms = clanPerms;

  this.name = clanName;
  this.description = clanDescription;
  this.renderingOutline = renderingOutline;
}

/**
 Adds a new claim for this clan.
 * @param cornerOne One corner of the claim.
 * @param cornerTwo The second corner of the claim.
 */
public void addClaim(Location cornerOne, Location cornerTwo) {
  Claim claim = new Claim(getClanID(), cornerOne.getWorld().getName(), cornerOne.getBlockX(), cornerTwo.getBlockX(), cornerOne.getBlockY(), cornerTwo.getBlockY(), cornerOne.getBlockZ(), cornerTwo.getBlockZ());
  Database.createClaim(claim);
  clanClaims.add(claim.getClaimID());
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

/**
 * @return The material that the outline of this clan should be rendered in.
 */
public @NotNull Material getOutlineMaterial() {
  return renderingOutline;
}
}
