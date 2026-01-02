package com.arr4nn.staffspectate.commands;

import com.arr4nn.staffspectate.Config;
import com.arr4nn.staffspectate.Logger;
import com.arr4nn.staffspectate.StaffSpectate;
import com.arr4nn.staffspectate.VanishData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.arr4nn.staffspectate.StaffSpectate.povTargets;
import static com.arr4nn.staffspectate.StaffSpectate.vanishedPlayers;

public class spectatePOVCommand implements CommandExecutor, TabCompleter {

    private final StaffSpectate plugin;

    public spectatePOVCommand(StaffSpectate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Audience a = plugin.adventure().player(player);
        MiniMessage mm = MiniMessage.miniMessage();

        if (args.length == 0) {
            a.sendMessage(mm.deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.NO_USER_SPECTATE))
            ));
            return true;
        }

        /* =========================
           LEAVE
           ========================= */
        if (args[0].equalsIgnoreCase("leave")) {
            if (!player.hasPermission("staffspectate.leave")) {
                player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
                return true;
            }

            VanishData vanishData = vanishedPlayers.get(player.getUniqueId());
            if (vanishData == null) {
                a.sendMessage(mm.deserialize(
                    Objects.requireNonNull(plugin.getConfig().getString(Config.NOT_SPECTATING))
                ));
                return true;
            }

            Logger.log(player.getName() + " left POV spectate.");

// ✅ allow plugin gamemode change
            StaffSpectate.gamemodeBypass.add(player.getUniqueId());

// Stop POV first
            player.setSpectatorTarget(null);
            povTargets.remove(player.getUniqueId());

// Restore location & gamemode
            player.teleport(vanishedPlayers.get(player.getUniqueId()).getLocation());
            player.setGameMode(vanishedPlayers.get(player.getUniqueId()).getGameMode());

// Show staff to everyone
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }

// Cleanup
            vanishedPlayers.remove(player.getUniqueId());

            a.sendMessage(MiniMessage.miniMessage().deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.SPECTATE_END))
            ));

            return true;

        }

        /* =========================
           EXIT-HERE
           ========================= */
        if (args[0].equalsIgnoreCase("exit-here")) {
            if (!player.hasPermission("staffspectate.leavebypass")) {
                player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
                return true;
            }

            VanishData vanishData = vanishedPlayers.get(player.getUniqueId());
            if (vanishData == null) {
                a.sendMessage(mm.deserialize(
                    Objects.requireNonNull(plugin.getConfig().getString(Config.NOT_SPECTATING))
                ));
                return true;
            }

            Logger.log(player.getName() + " exited POV spectate at current location.");

// ✅ allow plugin gamemode change
            StaffSpectate.gamemodeBypass.add(player.getUniqueId());

// Stop POV first
            player.setSpectatorTarget(null);
            povTargets.remove(player.getUniqueId());

// Restore gamemode only (stay at current location)
            player.setGameMode(vanishedPlayers.get(player.getUniqueId()).getGameMode());

// Show staff to everyone
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }

// Cleanup
            vanishedPlayers.remove(player.getUniqueId());

            a.sendMessage(MiniMessage.miniMessage().deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.SPECTATE_END))
            ));

            return true;


        }

        /* =========================
           SWITCH POV
           ========================= */
        if (args[0].equalsIgnoreCase("switch")) {
            if (!player.hasPermission("staffspectate.povspectate")) {
                player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
                return true;
            }

            if (args.length < 2) {
                a.sendMessage(mm.deserialize(
                    Objects.requireNonNull(plugin.getConfig().getString(Config.NO_USER_SPECTATE))
                ));
                return true;
            }

            if (!vanishedPlayers.containsKey(player.getUniqueId())) {
                a.sendMessage(mm.deserialize(
                    Objects.requireNonNull(plugin.getConfig().getString(Config.NOT_SPECTATING))
                ));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline() || target.equals(player)) {
                a.sendMessage(mm.deserialize(
                    Objects.requireNonNull(plugin.getConfig().getString(Config.NO_USER_SPECTATE))
                ));
                return true;
            }

            Logger.log(player.getName() + " switched POV spectate to " + target.getName());
            player.setSpectatorTarget(target);
            povTargets.put(player.getUniqueId(), target.getUniqueId());



            a.sendActionBar(mm.deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.NOW_SPECTATING_SPECTATE))
                    .replace("{user}", target.getName())
            ));
            return true;
        }

        /* =========================
           START POV SPECTATE
           ========================= */
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            a.sendMessage(mm.deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.NO_USER_SPECTATE))
            ));
            return true;
        }

        if (!player.hasPermission("staffspectate.povspectate")) {
            player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
            return true;
        }

        if (target.equals(player)) {
            a.sendMessage(mm.deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.SELF_SPECTATE))
            ));
            return true;
        }

        if (vanishedPlayers.containsKey(player.getUniqueId())) {
            a.sendMessage(mm.deserialize(
                Objects.requireNonNull(plugin.getConfig().getString(Config.IN_USE_SPECTATE))
            ));
            return true;
        }

        vanishedPlayers.put(
            player.getUniqueId(),
            new VanishData(player.getGameMode(), player.getLocation())
        );

        povTargets.put(player.getUniqueId(), target.getUniqueId());


        Logger.log(player.getName() + " started POV spectating " + target.getName());

        // HIDE FROM TAB LIST

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.hidePlayer(plugin, player);
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(target);

        a.sendActionBar(mm.deserialize(
            Objects.requireNonNull(plugin.getConfig().getString(Config.NOW_SPECTATING_SPECTATE))
                .replace("{user}", target.getName())
        ));

        return true;
    }

    /* =========================
       TAB COMPLETION
       ========================= */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("leave");
            completions.add("switch");

            if (sender.hasPermission("staffspectate.leavebypass")) {
                completions.add("exit-here");
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().equals(sender.getName())) {
                    completions.add(p.getName());
                }
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("switch")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().equals(sender.getName())) {
                    completions.add(p.getName());
                }
            }
        }

        return completions;
    }
}
