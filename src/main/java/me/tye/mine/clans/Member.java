package me.tye.mine.clans;

import me.tye.mine.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Member {

@NotNull private final UUID MemberID;

@Nullable private UUID permLevel;
@NotNull private Perms defaultPerms;

@NotNull private Clan defaultLayout;

/**
 Gets the member from a player UUID.
 * @param playerId The UUID of a player.
 * @return The Member class for this player.
 */
public static Member getMember(UUID playerId) {
  Database.getMember(playerId);
}

/**
 Creates the data for a member on first use.
 */
public Member() {
  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.memberExists(uuid)) {
    uuid = UUID.randomUUID();
  }
  MemberID = uuid;
}

/**
 This method isn't intended for general use. Use {@link #getMember(UUID)} to get a member from their player ID.
 */
public Member(@NotNull UUID memberID, @Nullable UUID permLevel, @NotNull Perms defaultPerms, @NotNull Clan defaultLayout) {
  MemberID = memberID;
  this.permLevel = permLevel;
  this.defaultPerms = defaultPerms;
  this.defaultLayout = defaultLayout;
}

public UUID getMemberID() {
  return MemberID;
}

/**
 * @return The default permissions for this member.
 */
public Perms getDefaultPerms() {
  return defaultPerms;
}

/**
 * @return True if this member is already in a clan.
 */
public boolean isInClan() {
  return;
}

public Clan getDefaultLayout() {
  return defaultLayout;
}

public void setDefaultLayout(Clan defaultLayout) {
  this.defaultLayout = defaultLayout;
}
}
