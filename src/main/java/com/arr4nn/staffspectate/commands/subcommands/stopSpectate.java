package com.arr4nn.staffspectate.commands.subcommands;

import com.arr4nn.staffspectate.VanishData;
import com.arr4nn.staffspectate.StaffSpectate;
import com.arr4nn.staffspectate.commands.SubCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class stopSpectate extends SubCommand {
   StaffSpectate plugin;

  public stopSpectate(StaffSpectate plugin) {
    this.plugin = plugin;
  }

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
    return "/ss leave";
  }

  @Override
  public void perform(Player player, String[] args) {

    Audience a = plugin.adventure().player(player);
    MiniMessage mm = MiniMessage.miniMessage();
    FileConfiguration config = plugin.getConfig();

    if (player.getGameMode() != GameMode.SPECTATOR){
      a.sendMessage(mm.deserialize(config.getString("language.commands.stopSpectate.not-spectating")));
      return;
    }
    VanishData vanishData = StaffSpectate.vanishedPlayers.get(player.getUniqueId());
    player.teleport(vanishData.getLocation());
    player.setGameMode(vanishData.getGameMode());
    a.sendMessage(mm.deserialize(config.getString("language.commands.stopSpectate.spectate-end")));
  }
}
