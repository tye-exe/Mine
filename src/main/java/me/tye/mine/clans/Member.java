package me.tye.mine.clans;

import me.tye.mine.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static me.tye.mine.Mine.onlineMembers;

public class Member {

@NotNull private final UUID memberID;

@Nullable private UUID clanPermID;
@Nullable private UUID clanID;


/**
 Gets the member from a player UUID.
 * @param playerId The UUID of a player.
 * @return The Member class for this player.
 */
public static Member getMember(UUID playerId) {
  //If the player is a member & is online then gets the member object from the HashMap.
  if (onlineMembers.containsKey(playerId)) {
    return onlineMembers.get(playerId);
  }

  //If the member isn't online but exists, get them from the database & load them.
  if (Database.memberExists(playerId)) {
    Member member = Database.getMember(playerId);
    onlineMembers.put(playerId, member);
    return member;
  }

  //If the member doesn't exist, create a new one & load it.
  Member member = new Member(playerId, null, null);
  Database.registerMember(playerId);
  onlineMembers.put(playerId, member);
  return member;
}


/**
 This method isn't intended for general use. Use {@link #getMember(UUID)} to get a member from their player ID.
 */
public Member(@NotNull UUID memberID, @Nullable UUID clanID, @Nullable UUID clanPermID) {
  this.memberID = memberID;
  this.clanPermID = clanPermID;
  this.clanID = clanID;
}

public @NotNull UUID getMemberID() {
  return memberID;
}

/**
 * @return True if this member is already in a clan.
 */
public boolean isInClan() {
  return clanID != null;
}

/**
 * @return The offline player with this UUID
 */
public @NotNull OfflinePlayer getPlayer() {
  return Bukkit.getOfflinePlayer(memberID);
}

}
