package me.tye.mine;

import me.tye.mine.clans.Member;
import me.tye.mine.utils.Identifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import static me.tye.mine.utils.TempConfigsStore.selectionRenderRadius;

public class PlayerSwitch implements Listener {

@EventHandler
public static void SelectPointer(PlayerItemHeldEvent e) {
  Player player = e.getPlayer();

  if (!Identifier.inventoryContainsIdentifier(player.getInventory(), "pointer")) return;

  Member member = Member.getMember(player.getUniqueId());
  if (member == null) return;
  
  member.renderNearbyClaims(selectionRenderRadius);
}

}
