package xyz.arr4nn.vulcanspectate.commands;

import org.bukkit.entity.Player;

public abstract class SubCommand {
  public abstract String getName();

  public abstract String getDescription();

  public abstract String getSyntax();

  public abstract void perform(Player player, String args[]);
}
