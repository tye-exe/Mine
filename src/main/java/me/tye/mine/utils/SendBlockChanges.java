package me.tye.mine.utils;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SendBlockChanges implements Runnable {

Collection<BlockState> fakeBlocksToSend;
Player playerToSendBlocksTo;

public Runnable init(Collection<BlockState> fakeBlocksToSend, Player playerToSendBlocksTo) {
  this.fakeBlocksToSend = fakeBlocksToSend;
  this.playerToSendBlocksTo = playerToSendBlocksTo;
  return this;
}

@Override
public void run() {
  playerToSendBlocksTo.sendBlockChanges(fakeBlocksToSend);
}

}
