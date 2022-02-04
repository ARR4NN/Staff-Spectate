package xyz.arr4nn.vulcanspectate.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.arr4nn.vulcanspectate.VanishData;
import xyz.arr4nn.vulcanspectate.commands.SubCommand;

import static xyz.arr4nn.vulcanspectate.VulcanSpectate.vanishedPlayers;

public class stopSpectate extends SubCommand {
  @Override
  public String getName() {
    return "leave";
  }

  @Override
  public String getDescription() {
    return "Stop spectating the player";
  }

  @Override
  public String getSyntax() {
    return "/vs leave";
  }

  @Override
  public void perform(Player player, String[] args) {
    VanishData vanishData = vanishedPlayers.get(player.getUniqueId());
    player.teleport(vanishData.getLocation());
    player.setGameMode(vanishData.getGameMode());
    player.sendMessage(ChatColor.GREEN+ "Returned you to your previous location!");
  }
}
