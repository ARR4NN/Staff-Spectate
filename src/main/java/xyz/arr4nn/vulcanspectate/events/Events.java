package xyz.arr4nn.vulcanspectate.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.arr4nn.vulcanspectate.VanishData;

import static xyz.arr4nn.vulcanspectate.VulcanSpectate.vanishedPlayers;

public class Events implements Listener {
  @EventHandler
  public static void onPlayerDisconnect(PlayerQuitEvent event){
      VanishData vanishData = vanishedPlayers.get(event.getPlayer().getUniqueId());
      if(vanishData.getLocation().length() > 1){
        event.getPlayer().teleport(vanishData.getLocation());
        event.getPlayer().setGameMode(vanishData.getGameMode());
      }
  }
}
