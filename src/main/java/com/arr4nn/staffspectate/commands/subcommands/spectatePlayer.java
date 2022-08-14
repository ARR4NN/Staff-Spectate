package com.arr4nn.staffspectate.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.arr4nn.staffspectate.VanishData;
import com.arr4nn.staffspectate.StaffSpectate;
import com.arr4nn.staffspectate.commands.SubCommand;

import static com.arr4nn.staffspectate.StaffSpectate.vanishedPlayers;

public class spectatePlayer extends SubCommand {

  StaffSpectate plugin;

  public spectatePlayer(StaffSpectate plugin) {
    this.plugin = plugin;
  }

  @Override
  public String getName() {
    return "spectate";
  }

  @Override
  public String getDescription() {
    return "Spectate a player";
  }

  @Override
  public String getSyntax() {
    return "/ss spectate <player name>";
  }

  @Override
  public void perform(Player player, String[] args) {

    Audience a = plugin.adventure().player(player);
    MiniMessage mm = MiniMessage.miniMessage();
    FileConfiguration config = plugin.getConfig();

    if(args.length > 1) {
      Player target = Bukkit.getPlayer(args[1]);

      if(vanishedPlayers.containsKey(player.getUniqueId())){
        vanishedPlayers.replace(player.getUniqueId(),new VanishData(player.getGameMode(), player.getLocation()));
      }else{
        vanishedPlayers.put(player.getUniqueId(), new VanishData(player.getGameMode(), player.getLocation()));
      }
      if(target == player){
        a.sendMessage(mm.deserialize(config.getString("language.commands.spectate.self-spectate")));
        return;
      }
      player.setGameMode(GameMode.SPECTATOR);
      a.sendActionBar(mm.deserialize(config.getString("language.commands.spectate.spectating").replaceAll("\\{user}",target.getName())));
      player.teleport(target.getLocation());
    }else if(args.length == 1) {
      a.sendMessage(mm.deserialize(config.getString("language.commands.spectate.no-user")));
    }

  }
}
