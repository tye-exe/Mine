package me.tye.mine.utils;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Sounds {

public static void confirm(@NotNull Player player) {
  player.playNote(player.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.C));
}

}
