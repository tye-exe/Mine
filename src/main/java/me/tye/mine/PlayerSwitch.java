package me.tye.mine;

import me.tye.mine.clans.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import static me.tye.mine.utils.Identifier.getIdentifier;
import static me.tye.mine.utils.TempConfigsStore.selectionRenderRadius;

public class PlayerSwitch implements Listener {

@EventHandler
public static void SelectPointer(PlayerItemHeldEvent e) {
  Player player = e.getPlayer();

  ItemStack heldItem = player.getInventory().getItem(e.getNewSlot());
  ItemStack previousItem = player.getInventory().getItem(e.getPreviousSlot());

  String heldIdentifier = getIdentifier(heldItem);
  String previousIdentifier = getIdentifier(previousItem);

  if (heldIdentifier.isEmpty() && previousIdentifier.isEmpty()) return;

  if (!(previousIdentifier.equals("pointer") || heldIdentifier.equals("pointer"))) return;

  Member member = Member.getMember(player.getUniqueId());
  if (member == null) return;


  //Makes the clan outlines render.
  if (heldIdentifier.equals("pointer")) {
    member.outlineNearbyClaims(selectionRenderRadius);
    return;
  }

  //restores the outlines to the default state
  member.unoutlineClaims();
}

}
