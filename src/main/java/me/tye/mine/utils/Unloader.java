package me.tye.mine.utils;

import me.tye.mine.clans.Member;

import java.util.Collection;
import java.util.Iterator;

import static me.tye.mine.Database.*;
import static me.tye.mine.utils.TempConfigsStore.unloaderSleepTime;

public class Unloader implements Runnable {

private static Thread unloader = null;

/**
 Creates a new unloader thread. This thread clears the caches every five mins.
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

    try {
      Thread.sleep(unloaderSleepTime);
    } catch (InterruptedException e) {
      return;
    }

    clansCache.clear();

    claimsCache.clear();

    permsCache.clear();

    Collection<Member> values = memberCache.values();
    synchronized (memberCache) {
      Iterator<Member> iterator = values.iterator();

      //not enhanced for loop to avoid potential concurrent modification exception.
      while (iterator.hasNext()) {
        Member member = iterator.next();

        if (member.getOfflinePlayer().isOnline()) return;

        memberCache.remove(member.getMemberID());
      }
    }

  }

}
}
