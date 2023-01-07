package com.arr4nn.staffspectate.events;

import com.arr4nn.staffspectate.Config;
import com.arr4nn.staffspectate.VanishData;
import com.arr4nn.staffspectate.StaffSpectate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
              event.getPlayer().teleport(vanishData.getLocation());
              event.getPlayer().setGameMode(vanishData.getGameMode());
              StaffSpectate.vanishedPlayers.remove(event.getPlayer().getUniqueId());
          }
      }
  }
}
