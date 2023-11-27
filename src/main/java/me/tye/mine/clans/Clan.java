package me.tye.mine.clans;

import java.util.HashMap;
import java.util.UUID;

public class Clan {


private final HashMap<UUID,Claim> clanClaims = new HashMap<>();
private final HashMap<UUID, Member> clanMember = new HashMap<>();
private final HashMap<UUID,Perms> clanMemberPerms = new HashMap<>();

}
