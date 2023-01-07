package com.arr4nn.staffspectate.commands;

import com.arr4nn.staffspectate.StaffSpectate;
import com.arr4nn.staffspectate.VanishData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.arr4nn.staffspectate.StaffSpectate.vanishedPlayers;

public class spectateAdminCommand implements CommandExecutor, TabCompleter {
   StaffSpectate plugin;

    public spectateAdminCommand(StaffSpectate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Audience a = plugin.adventure().player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        FileConfiguration config = plugin.getConfig();
        if (args[0].equals("info")) {

            player.sendMessage(ChatColor.translateAlternateColorCodes ('&',"&9[&aStaff Spectate&9] &aRunning version &e"+plugin.getDescription().getVersion()+
                    "\n&dSpigot page&7:&e https://www.spigotmc.org/resources/staff-spectate.99739/"+
                    (plugin.updateNeeded? "\n\n&c(&c&l!&c) This plugin version is outdated!\n\n":"")+
                    "\n&dAuthor&7:&a ARR4NN"+
                    "\n&dDiscord Support&7:&e https://discord.gg/wWfPJKC2mF"+
                    "\n\n"+
                    "&aThank you for using Staff Spectate by ARR4NN!"
            ));


        }else if(args[0].equals("reload")){
            plugin.reloadConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes ('&',"&aPlugin has been reloaded!"));

        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        arguments.add("info");
        arguments.add("reload");
        return arguments;
    }
}
