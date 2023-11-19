package me.tye.mine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
@Override
public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
  ArrayList<String> completions = new ArrayList<>();

  if (args.length == 1) {
    return StringUtil.copyPartialMatches(args[0], List.of("pointer"), completions);
  }

  return completions;
}
}
