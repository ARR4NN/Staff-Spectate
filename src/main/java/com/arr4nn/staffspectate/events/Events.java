package com.arr4nn.staffspectate.events;

import com.arr4nn.staffspectate.VanishData;
import com.arr4nn.staffspectate.StaffSpectate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
  @EventHandler
  public static void onPlayerDisconnect(PlayerQuitEvent event){
      VanishData vanishData = StaffSpectate.vanishedPlayers.get(event.getPlayer().getUniqueId());
      if(vanishData == null) return;
      if(vanishData.getLocation().length() > 1){
        event.getPlayer().teleport(vanishData.getLocation());
        event.getPlayer().setGameMode(vanishData.getGameMode());
      }
  }
}
