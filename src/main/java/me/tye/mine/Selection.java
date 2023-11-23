package me.tye.mine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static me.tye.mine.utils.Util.getSurrounding;

public class Selection {

public static final HashMap<UUID, Selection> selections = new HashMap<>();
private final UUID playerID;

private Location startLoc = new Location(Bukkit.getWorlds().get(0), 0, Double.MAX_VALUE, 0);
private Location endLoc = new Location(Bukkit.getWorlds().get(0), 0, Double.MAX_VALUE, 0);

private final ArrayList<Location> selected = new ArrayList<>();


public Selection(@NotNull UUID playerID) {
  this.playerID = playerID;
}

/**
 Moves the end location to a list of selected positions. And sets the given location to be the end location.
 * @param selectedLocation The given location.
 */
public void add(@NotNull Location selectedLocation) {
  selected.add(endLoc);
  setEndLoc(selectedLocation);
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

public boolean hasSetEndLocation() {
  return endLoc.getY() != Double.MAX_VALUE;
}

public boolean hasSetStartLocation() {
  return endLoc.getY() != Double.MAX_VALUE;
}

/**
 Changes all the blocks selected by a player to the server-side state.
 */
public void restore() {
  restoreBlocks(getStartLoc(), true);
  restoreBlocks(getEndLoc(), true);

  for (Location restoreLocation : selected) {
    restoreBlocks(restoreLocation, true);
  }
}

public @NotNull Location getEndLoc() {
  return endLoc;
}

public @NotNull Location getStartLoc() {
  return startLoc;
}

public void setStartLoc(@NotNull Location startLoc) {
  restoreBlocks(this.startLoc, false);
  this.startLoc = startLoc;
}

public void setEndLoc(@NotNull Location endLoc) {
  restoreBlocks(this.endLoc, false);
  this.endLoc = endLoc;
}

/**
 Resends the block data of a three by three area centered on the given location to the client.
 * @param locationToRestore The given location.
 * @param includeAdjacentSelected If set to true then all blocks will be resent.<br>
 *                          If set to false then adjacent selection blocks won't be resent.
 */
private void restoreBlocks(@NotNull Location locationToRestore, boolean includeAdjacentSelected) {
  Player player = Bukkit.getPlayer(playerID);
  if (player == null) return;

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
}
