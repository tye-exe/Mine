package me.tye.mine.clans;

import me.tye.mine.Database;
import me.tye.mine.errors.InvalidClanCreationException;
import me.tye.mine.utils.Lang;

import java.util.HashMap;
import java.util.UUID;

public class Clan {

private final UUID clanID;

private String name;
private String description;

private final HashMap<UUID, Claim> clanClaims = new HashMap<>();
private final HashMap<UUID, Member> clanMember = new HashMap<>();
private final HashMap<UUID, Perms> clanMemberPerms = new HashMap<>();


public Clan(Claim firstClaim, Member owner) throws InvalidClanCreationException {
  if (firstClaim.isOverlapping()) {
    throw new InvalidClanCreationException(Lang.claim_areaAlreadyClaimed.getResponse());
  }

  if (owner.isInClan()) {
    throw new InvalidClanCreationException(Lang.member_alreadyInClan.getResponse());
  }


  UUID uuid = UUID.randomUUID();
  //ensures that the UUID is unique
  while (Database.clanExists(uuid)) {
    uuid = UUID.randomUUID();
  }
  clanID = uuid;

  setName(owner.getDefaultLayout().getName());
  setDescription(owner.getDefaultLayout().getDescription());

  clanClaims.put(firstClaim.getClaimID(), firstClaim);
  clanMember.put(owner.getMemberID(), owner);

  Perms defaultPerms = owner.getDefaultPerms();
  clanMemberPerms.put(defaultPerms.getPermID(), defaultPerms);

  Database.
}

public String getName() {
  return name;
}

public void setName(String name) {
  this.name = name;
}

public String getDescription() {
  return description;
}

public void setDescription(String description) {
  this.description = description;
}

public UUID getClanID() {
  return clanID;
}
}
