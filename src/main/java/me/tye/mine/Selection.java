package me.tye.mine;

import me.tye.mine.utils.SendBlockChanges;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.tye.mine.utils.Util.*;

public class Selection {

public static final HashMap<UUID, Selection> selections = new HashMap<>();
private final Player player;

private Location startLoc = new Location(Bukkit.getWorlds().get(0), 0, Double.MAX_VALUE, 0);
private Location endLoc = new Location(Bukkit.getWorlds().get(0), 0, Double.MAX_VALUE, 0);

private final ArrayList<Location> selected = new ArrayList<>();

/**
 Stores the current outline of the selection that is shown to the player. The specific selected blocks are not included in this.
 */
private final LinkedHashSet<Location> selectionOutline = new LinkedHashSet<>();


//boiler plate start
public Selection(@NotNull Player player) {
  this.player = player;
}


public @NotNull Location getEndLoc() {
  return endLoc;
}

public @NotNull Location getStartLoc() {
  return startLoc;
}

/**
 * @return True if the user has set an end location.
 */
public boolean hasSetEndLocation() {
  return getEndLoc().getY() != Double.MAX_VALUE;
}

/**
 * @return True if the user has set a start location.
 */
public boolean hasSetStartLocation() {
  return getStartLoc().getY() != Double.MAX_VALUE;
}

/**
 * @return True if the user has a start location selected & an end location selected.
 */
public boolean hasSelection() {
  return hasSetEndLocation() && hasSetStartLocation();
}

//boiler plate end


/**
 Sets the start location that the player selected & sends a fake block packet to the player, so that they can see the block they've selected.<br>
 If the player has made two or more selections then the outline of the selected area is rendered client-side.
 * @param startLoc The start location selected.
 */
public void setStartLoc(@NotNull Location startLoc) {
  restoreBlocks(getStartLoc(), false);

  Location oldLocation = getStartLoc();
  this.startLoc = startLoc;

  //If the player has made a selection of two or more blocks then the selection is rendered.
  if (hasSelection()) {
    renderSelection(oldLocation, getStartLoc(), firstSelectedMaterial);
    return;
  }

  Collection<BlockState> cornerStates = getNewCornerSurroundingStates(getStartLoc());

  BlockState state = getStartLoc().getBlock().getState();
  state.setType(firstSelectedMaterial);
  cornerStates.add(state);

  //Sends the new update blocks a tick later, since when the player clicks on a block, a sends a block update packet.
  Bukkit.getScheduler().runTaskLater(plugin, new SendBlockChanges().init(cornerStates, player), 1);
}

/**
 Sets the end location that the player selected & sends a fake block packet to the player, so that they can see the block they've selected.<br>
 If the player has made two or more selections then the outline of the selected area is rendered client-side.
 * @param endLoc The end location selected.
 */
public void setEndLoc(@NotNull Location endLoc) {
  restoreBlocks(getEndLoc(), false);

  Location oldLocation = getEndLoc();
  this.endLoc = endLoc;

  //If the player has made a selection of two or more blocks then the selection is rendered.
  if (hasSelection()) {
    renderSelection(oldLocation, getEndLoc(), lastSelectedMaterial);
    return;
  }

  Collection<BlockState> cornerStates = getNewCornerSurroundingStates(getEndLoc());

  BlockState state = getEndLoc().getBlock().getState();
  state.setType(lastSelectedMaterial);
  cornerStates.add(state);

  //Sends the new update blocks a tick later, since when the player clicks on a block, a sends a block update packet.
  Bukkit.getScheduler().runTaskLater(plugin, new SendBlockChanges().init(cornerStates, player), 1);
}


/**
 Sets the start or end location to the given location based on the given action.<br>
 If the given action is a left click then the start location is set.<br>
 If the given action is a right click then the end location is set.<br>
 <br>
 If a user already has a location selected then a block change packet is sent to restore old block states.
 * @param location The given location.
 * @param action The given action.
 * @return The modified object.
 */
public @NotNull Selection setLocation(@NotNull Location location, @NotNull Action action) {
  if (action.isLeftClick()) {
    setStartLoc(location);
  }
  else if (action.isRightClick()) {
    setEndLoc(location);
  }

  return this;
}

/**
 Checks if a location has been set or not.<br>
 <br>
 The method works by checking if the location has a y of Double.MAX_VALUE. As the locations are initialized with a y of that value.
 * @param action The action the user is performing. This will affect whether the start location is checked or the end location is checked.
 * @return True if the determined location has been set.
 */
public boolean hasSetLocation(@NotNull Action action) {
  if (action.isLeftClick()) {
    return hasSetStartLocation();
  }
  else if (action.isRightClick()) {
    return hasSetEndLocation();
  }

  return false;
}


/**
 Changes all the blocks selected by a player to the server-side state.
 */
public void restore() {
  //Restores the blocks that a player has selected.
  restoreBlocks(getStartLoc(), true);
  restoreBlocks(getEndLoc(), true);

  for (Location restoreLocation : selected) {
    restoreBlocks(restoreLocation, true);
  }

  //Restores the outline of the selection area.
  if (player == null) return;
  if (selectionOutline.isEmpty()) return;

  Collection<BlockState> refreshBlocks = new ArrayList<>();

  selectionOutline.forEach(location -> {
    refreshBlocks.add(location.getBlock().getState());
  });

  player.sendBlockChanges(refreshBlocks);
}


/**
 Resends the block data of a three by three area centered on the given location to the client.
 * @param locationToRestore The given location.
 * @param includeAdjacentSelected If set to true then all blocks will be resent.<br>
 *                          If set to false then adjacent selection blocks won't be resent.
 */
private void restoreBlocks(@NotNull Location locationToRestore, boolean includeAdjacentSelected) {
  Collection<BlockState> restoreBlocks = new ArrayList<>();

  for (Block block : getSurrounding(locationToRestore.getBlock(), null)) {


    if (!includeAdjacentSelected) {
      if (block.getLocation().equals(getStartLoc())
          || block.getLocation().equals(getEndLoc())) continue;
    }

    restoreBlocks.add(block.getState());
  }

  restoreBlocks.add(locationToRestore.getBlock().getState());

  player.sendBlockChanges(restoreBlocks);
}



/**
 Sends packets to the player restoring the old selected area & the corner to restore to the server-side state.<br>
 Then sends fake block packets to the player for the new selection outline & the new selected corner.<br>
 * @param cornerToReRestore The location of the old selected corner that should be restored to a server-side state.
 * @param newSelectedCorner The location of the new selected corner to change client-side.
 * @param newSelectedCornerMaterial The material to change the new selected corner to.
 */
private void renderSelection(Location cornerToReRestore, Location newSelectedCorner, Material newSelectedCornerMaterial) {
  //Removes the old outline & corner for the player
  Collection<BlockState> refreshBlocks = new ArrayList<>();

  selectionOutline.forEach(location -> {
    refreshBlocks.add(location.getBlock().getState());
  });

  refreshBlocks.add(cornerToReRestore.getBlock().getState());

  player.sendBlockChanges(refreshBlocks);


  selectionOutline.clear();

  World world = player.getWorld();

  int startX = getEndLoc().getBlockX();
  int startY = getEndLoc().getBlockY();
  int startZ = getEndLoc().getBlockZ();

  int endX = getStartLoc().getBlockX();
  int endY = getStartLoc().getBlockY();
  int endZ = getStartLoc().getBlockZ();


  //adds the outline for the X blocks
  getBetween(startX, endX).forEach((X) -> {
    selectionOutline.add(new Location(world, X, startY, startZ));
    selectionOutline.add(new Location(world, X, endY, endZ));
    selectionOutline.add(new Location(world, X, startY, endZ));
    selectionOutline.add(new Location(world, X, endY, startZ));
  });

  //adds the outline for the Y blocks
  getBetween(startY, endY).forEach((Y) -> {
    selectionOutline.add(new Location(world, startX, Y, startZ));
    selectionOutline.add(new Location(world, endX, Y, endZ));
    selectionOutline.add(new Location(world, startX, Y, endZ));
    selectionOutline.add(new Location(world, endX, Y, startZ));
  });

  //adds the outline for the Z blocks
  getBetween(startZ, endZ).forEach((Z) -> {
    selectionOutline.add(new Location(world, startX, startY, Z));
    selectionOutline.add(new Location(world, endX, endY, Z));
    selectionOutline.add(new Location(world, startX, endY, Z));
    selectionOutline.add(new Location(world, endX, startY, Z));
  });


  //removes the selection blocks if they got added.
  selectionOutline.remove(getStartLoc());
  selectionOutline.remove(getEndLoc());


  //Sends the new outline & corner to the player
  Collection<BlockState> updateBlocks = new ArrayList<>();

  selectionOutline.forEach(location -> {
    BlockState state = location.getBlock().getState();
    state.setType(outlineMaterial);
    updateBlocks.add(state);
  });

  //Adds the new corner
  BlockState cornerState = newSelectedCorner.getBlock().getState();
  cornerState.setType(newSelectedCornerMaterial);
  updateBlocks.add(cornerState);

  //Makes the new corner glow
  updateBlocks.addAll(getNewCornerSurroundingStates(newSelectedCorner));

  //Sends the new update blocks a tick later, since when the player clicks on a block, a sends a block update packet.
  Bukkit.getScheduler().runTaskLater(plugin, new SendBlockChanges().init(updateBlocks, player), 1);

}

/**
 Gets the numbers between two ints. This method works with positive & negative ints in any order.
 * @param first One of the ints.
 * @param second The other int.
 * @return The numbers between the two ints.
 */
private List<Integer> getBetween(int first, int second) {
  ArrayList<Integer> between = new ArrayList<>();

  int diff = first - second;

  if (diff > 0) {
    for (int i = second; i < first; i++) {
      between.add(i);
    }
  }

  if (diff < 0) {
    for (int i = second; i > first; i--) {
      between.add(i);
    }
  }

  return between;
}

/**
 Gets the blocks surround the given location that won't be an outline & makes a copy of them, with their material type as LIGHT.
 * @param getSurroundingOf The location to get the surrounding blocks of.
 * @return The surrounding blocks with a material type of LIGHT.
 */
private Collection<BlockState> getNewCornerSurroundingStates(Location getSurroundingOf) {
  Collection<BlockState> cornerStates = new ArrayList<>();

  //makes the selected block glow.
  for (Block block : getSurrounding(getSurroundingOf.getBlock(), Material.AIR)) {
    //doesn't change a block to be light if it should be an outline.
    if (selectionOutline.contains(block.getLocation())) continue;

    BlockState blockState = block.getState();
    blockState.setType(Material.LIGHT);
    cornerStates.add(blockState);
  }

  return cornerStates;
}
}

