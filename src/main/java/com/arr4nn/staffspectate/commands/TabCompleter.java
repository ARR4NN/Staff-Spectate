package com.arr4nn.staffspectate.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    final List<String> tab = new ArrayList<>();
    if (args.length == 1) {
      return StringUtil.copyPartialMatches(args[0], Arrays.asList("spectate","leave"), tab);
    }
    if (args.length == 2) {
      List<String> names = new ArrayList<>();
      for (Player player : Bukkit.getOnlinePlayers()) {
        names.add(player.getName());
      }
      return StringUtil.copyPartialMatches(args[1], names, tab);
    }
    return tab;
  }
}
