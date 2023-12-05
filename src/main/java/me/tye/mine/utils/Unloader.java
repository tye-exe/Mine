package me.tye.mine.utils;

import me.tye.mine.clans.Claim;
import me.tye.mine.clans.Member;

import java.util.ArrayList;
import java.util.List;

import static me.tye.mine.Mine.loadedClaims;
import static me.tye.mine.Mine.onlineMembers;

public class Unloader implements Runnable {

private static Thread unloader = null;

/**
 Creates a new unloader thread. This thread checks the cache that stores loaded clans, claims, members, & perms. Then removes any ones that are unloaded from that cache.
 */
public static void init() {
  if (unloader != null) return;

  unloader = new Thread(new Unloader());
}

/**
 Stops the unloader thread & then assigns it to null for garbage collection.
 */
public static void terminate() {
  if (unloader == null) return;

  unloader.interrupt();
  unloader = null;
}

@Override
public void run() {

  while (true) {
    //runs once a minuet
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      return;
    }

//    for (Clan clan : loadedClans.values()) {
//      if (clan.isLoaded()) return;
//    }

    //can't be enhanced for due to concurrent modification exception.
    List<Claim> claims = new ArrayList<>(loadedClaims.values());
    for (int i = 0; i < claims.size(); i++) {
      if (claims.get(i).isLoaded()) continue;

      loadedClaims.remove(claims.get(i).getClaimID());
    }

    //can't be enhanced for due to concurrent modification exception.
    List<Member> members = new ArrayList<>(onlineMembers.values());
    for (int i = 0; i < members.size(); i++) {
      if (members.get(i).getOfflinePlayer().isOnline()) continue;

      onlineMembers.remove(members.get(i).getMemberID());
    }

    //can't be enhanced for due to concurrent modification exception.
//    List<Perm> perms = new ArrayList<>(loadedPerms.values());
//    for (int i = 0; i < perms.size(); i++) {
//      if (perms.get(i).isLoaded()) continue;
//
//      loadedPerms.remove(perms.get(i).getPermID());
//    }
  }

}
}
