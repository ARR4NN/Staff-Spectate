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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.arr4nn.staffspectate.StaffSpectate.vanishedPlayers;

public class spectateCommand implements CommandExecutor, TabCompleter {

    StaffSpectate plugin;

    public spectateCommand(StaffSpectate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Audience a = plugin.adventure().player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        FileConfiguration config = plugin.getConfig();
            if(!(args.length > 0)){
                a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.NO_USER_SPECTATE))));
                return false;
            }
            if (args[0].equals("leave")) {
                if(!player.hasPermission("staffspectate.leave")){
                    player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
                    return false;
                }
                // Is the leave command
                VanishData vanishData = vanishedPlayers.get(player.getUniqueId());
                if (vanishData == null) {
                    a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.NOT_SPECTATING))));
                    return false;
                }
                Logger.log(player.getName()+" used the leave spectate command and was returned to original location and gamemode.");
                player.teleport(vanishData.getLocation());
                player.setGameMode(vanishData.getGameMode());
                a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.SPECTATE_END))));
                vanishedPlayers.remove(player.getUniqueId());

            } else if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))) {
                if(!player.hasPermission("staffspectate.spectate")){
                    player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
                    return false;
                }
                // Is an online player that's been mentioned.
                Player target = Bukkit.getPlayer(args[0]);
                if(target == player){
                    a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.SELF_SPECTATE))));
                    return false;
                }
                if(vanishedPlayers.containsKey(player.getUniqueId())){
                    a.sendMessage(mm.deserialize(plugin.getConfig().getString(Config.IN_USE_SPECTATE)));
                    return false;
                }else{
                    Logger.log(player.getName()+" started spectating player "+target.getName());
                    vanishedPlayers.put(player.getUniqueId(), new VanishData(player.getGameMode(), player.getLocation()));
                    player.setGameMode(GameMode.SPECTATOR);
                    a.sendActionBar(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.NOW_SPECTATING_SPECTATE)).replaceAll("\\{user}",target.getName())));
                    player.teleport(target.getLocation());
                }
            }else if(args[0].equals("exit-here")){
                if(!player.hasPermission("staffspectate.leavebypass")){
                    player.sendMessage(plugin.getConfig().getString(Config.CMD_NOPERM));
                    return false;
                }
                VanishData vanishData = vanishedPlayers.get(player.getUniqueId());
                if (vanishData == null) {
                    a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.NOT_SPECTATING))));
                    return false;
                }
                Logger.log(player.getName()+" used the exit-here spectate command and was returned to original gamemode at current location ("+player.getLocation().getBlockX()+","+player.getLocation().getBlockY()+","+player.getLocation().getBlockZ()+")");
                player.setGameMode(vanishData.getGameMode());
                a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.SPECTATE_END))));
                vanishedPlayers.remove(player.getUniqueId());
            } else{
                a.sendMessage(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString(Config.NO_USER_SPECTATE))));
            }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();

        for(Player all: Bukkit.getOnlinePlayers()){
            if(!all.getName().equals(sender.getName())){
                arguments.add(all.getName());
            }
        }
        arguments.add("leave");
        if(sender.hasPermission("staffspectate.leavebypass")){
            arguments.add("exit-here");
        }
        return arguments;
    }
}
