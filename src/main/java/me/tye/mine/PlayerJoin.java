package me.tye.mine;

import me.tye.mine.clans.Member;
import me.tye.mine.utils.Lang;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.tye.mine.Database.memberCache;

public class PlayerJoin implements Listener {

@EventHandler
public static void playerJoin(PlayerJoinEvent e) {
  Player player = e.getPlayer();

  if (!Database.memberExists(player.getUniqueId())) {
    Database.createMember(player.getUniqueId());
  }

  Member member = Database.getMember(player.getUniqueId());

  if (member == null) {
    player.kick(Component.text(Lang.member_badJoin.getResponse()));
    return;
  }

  memberCache.put(member.getMemberID(), member);

}
}
