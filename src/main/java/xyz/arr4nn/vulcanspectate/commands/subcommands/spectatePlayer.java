package xyz.arr4nn.vulcanspectate.commands.subcommands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import xyz.arr4nn.vulcanspectate.VanishData;
import xyz.arr4nn.vulcanspectate.commands.SubCommand;

import static xyz.arr4nn.vulcanspectate.VulcanSpectate.vanishedPlayers;

public class spectatePlayer extends SubCommand {
  @Override
  public String getName() {
    return "spectate";
  }

  @Override
  public String getDescription() {
    return "Spectate a cheating player";
  }

  @Override
  public String getSyntax() {
    return "/vs spectate <player name>";
  }

  @Override
  public void perform(Player player, String[] args) {
    if(args.length > 1) {
      vanishedPlayers.put(player.getUniqueId(), new VanishData(player.getGameMode(), player.getLocation()));
      Player target = Bukkit.getPlayer(args[1]);
      player.setGameMode(GameMode.SPECTATOR);
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Spectating "+target.getDisplayName()));
      player.teleport(target.getLocation());
    }else if(args.length == 1) {
    player.sendMessage(ChatColor.RED + "Please provide a user to spectate! (/vs spectate <player>)");
    }

  }
}
