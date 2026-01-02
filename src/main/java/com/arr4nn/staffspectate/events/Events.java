package com.arr4nn.staffspectate.events;

import com.arr4nn.staffspectate.Config;
import com.arr4nn.staffspectate.Logger;
import com.arr4nn.staffspectate.VanishData;
import com.arr4nn.staffspectate.StaffSpectate;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;
import java.util.UUID;

import static com.arr4nn.staffspectate.StaffSpectate.vanishedPlayers;

public class Events implements Listener {

  private final StaffSpectate plugin;

  public Events(StaffSpectate plugin) {
    this.plugin = plugin;
  }

  /* =========================
     PLAYER JOIN
     ========================= */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    VanishData vanishData = vanishedPlayers.get(uuid);
    if (vanishData == null) return;
    if (!plugin.getConfig().getBoolean(Config.RETURN_TO_LOCATION)) return;

    Logger.log(player.getName() + " rejoined while spectating and was restored.");

    StaffSpectate.gamemodeBypass.add(uuid);
    player.setGameMode(GameMode.SPECTATOR);

    // Restore POV if applicable
    UUID targetId = StaffSpectate.povTargets.get(uuid);
    if (targetId != null) {
      Player target = Bukkit.getPlayer(targetId);
      if (target != null && target.isOnline()) {
        player.setSpectatorTarget(target);
      }
    }

    // If not POV, restore normal spectate position
    if (player.getSpectatorTarget() == null) {
      player.teleport(vanishData.getLocation());
    }

    // Hide from everyone
    for (Player online : Bukkit.getOnlinePlayers()) {
      online.hidePlayer(plugin, player);
    }
  }

  /* =========================
     TARGET QUIT
     ========================= */
  @EventHandler
  public void onTargetQuit(PlayerQuitEvent event) {
    Player target = event.getPlayer();
    MiniMessage mm = MiniMessage.miniMessage();

    for (Player spectator : Bukkit.getOnlinePlayers()) {
      VanishData data = vanishedPlayers.get(spectator.getUniqueId());
      if (data == null) continue;
      if (!target.equals(spectator.getSpectatorTarget())) continue;

      Logger.log(spectator.getName() + " was removed from POV spectate (target quit: " + target.getName() + ").");

      spectator.setSpectatorTarget(null);
      spectator.teleport(data.getLocation());
      spectator.setGameMode(data.getGameMode());

      StaffSpectate.povTargets.remove(spectator.getUniqueId());

      for (Player online : Bukkit.getOnlinePlayers()) {
        online.showPlayer(plugin, spectator);
      }

      vanishedPlayers.remove(spectator.getUniqueId());

      plugin.adventure().player(spectator).sendMessage(
          mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.PLAYER_QUIT)))
      );
    }
  }

  /* =========================
     GAME MODE CHANGE
     ========================= */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    if (StaffSpectate.gamemodeBypass.remove(uuid)) return;

    if (vanishedPlayers.containsKey(uuid)) {
      if (event.getNewGameMode() != GameMode.SPECTATOR) {
        Logger.log(player.getName() + " attempted to change gamemode while spectating.");
        event.setCancelled(true);
      }
      return;
    }

    if (StaffSpectate.povTargets.containsValue(uuid)) {
      Logger.log(player.getName() + " attempted to change gamemode while being POV spectated.");
      event.setCancelled(true);
    }
  }

  /* =========================
     WORLD CHANGE
     ========================= */
  @EventHandler
  public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
    Player target = event.getPlayer();

    for (Player spectator : Bukkit.getOnlinePlayers()) {
      UUID spectatorId = spectator.getUniqueId();

      if (!StaffSpectate.povTargets.containsValue(target.getUniqueId())) continue;

      VanishData vanishData = vanishedPlayers.get(spectatorId);
      if (vanishData == null) continue;

      // Delay to allow portal teleport to finish
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        if (!spectator.isOnline() || !target.isOnline()) return;

        // Teleport spectator to target and restore POV
        spectator.teleport(target.getLocation());
        spectator.setSpectatorTarget(target);

        // Keep spectator hidden
        for (Player online : Bukkit.getOnlinePlayers()) {
          online.hidePlayer(plugin, spectator);
        }

        Logger.log(spectator.getName() + " followed " + target.getName() + " to new world and POV restored.");
      }, 10L); // ~0.5s delay (10 ticks)
    }
  }
}
