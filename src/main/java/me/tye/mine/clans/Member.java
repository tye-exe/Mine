package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.tye.mine.utils.TempConfigsStore.outlineMaterial;
import static me.tye.mine.utils.Util.getCoveredChunks;

public class Member {

private final @NotNull UUID memberID;

private @Nullable UUID clanPermID;
private @Nullable UUID clanID;


/**
 Gets the member from a player UUID.
 * @param playerId The UUID of a player.
 * @return The Member class for this player, or null if the member can't be gotten form the database.
 */
public static @Nullable Member getMember(@NotNull UUID playerId) {
  //If the member doesn't exist return null
  if (!Database.memberExists(playerId)) return null;

  return Database.getMember(playerId);
}


/**
 This method isn't intended for general use. Use {@link #getMember(UUID)} to get a member from their player ID.
 @param memberID The uuid of the member.
 @param clanID The clan uuid of the member.
 @param clanPermID The perm uuid for the members perms within the clan.
 */
public Member(@NotNull UUID memberID, @Nullable UUID clanID, @Nullable UUID clanPermID) {
  this.memberID = memberID;
  this.clanPermID = clanPermID;
  this.clanID = clanID;
}

/**
 * @return True if this member is already in a clan.
 */
public boolean isInClan() {
  return getClanID() != null;
}

/**
 * @return The offline player with this UUID
 */
public @NotNull OfflinePlayer getOfflinePlayer() {
  return Bukkit.getOfflinePlayer(getMemberID());
}

/**
 * @return The online player, or null if the player is offline.
 */
public @Nullable Player getPlayer() {
  return Bukkit.getPlayer(getMemberID());
}

public @NotNull UUID getMemberID() {
  return memberID;
}

public @Nullable UUID getClanPermID() {
  return clanPermID;
}

public @Nullable UUID getClanID() {
  return clanID;
}

public @Nullable Clan getClan() {
  UUID clanID = getClanID();
  if (clanID == null) return null;

  return Database.getClan(clanID);
}

/**
 Saves the changes made to the member.
 */
public void save() {
  Database.updateMember(this);
}

/**
 Stores a list of claim outlines that have been rendered for a member with there ID.*/
private static final @NotNull HashMap<UUID, List<BlockState>> nearbyOutlinesMap = new HashMap<>();

/**
 Renders the nearby claims for the member & only the member.
 * @param blockRadius The radius in which nearby claims should be rendered.
 */
public void outlineNearbyClaims(int blockRadius) {
  Player player = getPlayer();
  if (player == null) return;

  List<BlockState> nearbyOutlines = getNearbyClaimOutlines(blockRadius);

  Material clanOutline = outlineMaterial;

  //tries to get the outline material from a clan
  Clan clan = getClan();
  if (clan != null) {
    clanOutline = clan.getOutlineMaterial();
  }

  for (int i = 0; i < nearbyOutlines.size(); i++) {
    BlockState state = nearbyOutlines.get(i);
    state.setType(clanOutline);
    nearbyOutlines.set(i, state);
  }

  player.sendBlockChanges(nearbyOutlines);

  nearbyOutlinesMap.put(getMemberID(), nearbyOutlines);
}

/**
 Reverts the rendered claim outlines that have been rendered for this member to the server side world state.
 */
public void unoutlineClaims() {
  Player player = getPlayer();
  if (player == null) return;

  List<BlockState> nearbyOutlines = nearbyOutlinesMap.get(getMemberID());

  if (nearbyOutlines == null) return;

  //using player.sendBlockChanges() for the restoring bugs results in a bug within paper version 1.20.2 #318
  for (BlockState outlineState : nearbyOutlines) {
    player.sendBlockChange(outlineState.getLocation(), outlineState.getLocation().getBlock().getBlockData());
  }
}

private List<BlockState> getNearbyClaimOutlines(int blockRadius) {
  Player player = getPlayer();
  if (player == null) return new ArrayList<>();

  Location playerLocation = player.getLocation();

  Location cornerOne = player.getLocation();
  Location cornerTwo = player.getLocation();

  //Creates two location on opposite sides of each other to render the claims in.
  cornerOne.subtract(blockRadius, 0, blockRadius);
  cornerTwo.add(blockRadius, 0, blockRadius);

  //Gets the keys of all the chunks surrounding the player at the given radius.
  HashSet<Long> coveredChunks = getCoveredChunks(cornerOne, cornerTwo);


  ArrayList<Claim> claimsToRender = new ArrayList<>();

  //Adds all the claims surrounding the player.
  for (Long chunkKey : coveredChunks) {

    for (Long databaseChunkKeys : Database.getChunkKeys()) {
      if (!databaseChunkKeys.equals(chunkKey)) continue;

      claimsToRender.addAll(Database.getClaims(chunkKey));
    }

  }

  ArrayList<BlockState> renderedOutline = new ArrayList<>();

  //gets the parts of the claims to render
  for (Claim claim : claimsToRender) {

    for (Location location : claim.getOutlineWithin(cornerOne, cornerTwo)) {
      renderedOutline.add(location.getBlock().getState());
    }

  }

  return renderedOutline;
}
}
