package com.arr4nn.staffspectate.events;

import com.arr4nn.staffspectate.Config;
import com.arr4nn.staffspectate.Logger;
import com.arr4nn.staffspectate.VanishData;
import com.arr4nn.staffspectate.StaffSpectate;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {


    static StaffSpectate plugin;

    public Events(StaffSpectate plugin) {
        this.plugin = plugin;
    }
  @EventHandler
  public static void onPlayerConnect(PlayerJoinEvent event){
      VanishData vanishData = StaffSpectate.vanishedPlayers.get(event.getPlayer().getUniqueId());
      if(vanishData == null) {
          return;
      };
      if(vanishData.getLocation().length() > 1) {
          if (plugin.getConfig().getBoolean(Config.RETURN_TO_LOCATION)) {
              Logger.log(event.getPlayer().getName()+" rejoined game and was returned to original location and gamemode.");
              event.getPlayer().teleport(vanishData.getLocation());
              event.getPlayer().setGameMode(vanishData.getGameMode());
              StaffSpectate.vanishedPlayers.remove(event.getPlayer().getUniqueId());
          }
      }
  }

  @EventHandler
    public static void PlayerGameModeChangeEvent(PlayerGameModeChangeEvent event){
      VanishData vanishData = StaffSpectate.vanishedPlayers.get(event.getPlayer().getUniqueId());
      if(vanishData == null) {
          return;
      };
      if(event.getNewGameMode() != GameMode.SPECTATOR){
          Logger.log(event.getPlayer().getName()+" changed gamemode to "+event.getNewGameMode().name()+". Their spectator session was canceled.");
          StaffSpectate.vanishedPlayers.remove(event.getPlayer().getUniqueId());
      }

  }
}
