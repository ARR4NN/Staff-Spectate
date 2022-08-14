package com.arr4nn.staffspectate.commands;

import com.arr4nn.staffspectate.commands.subcommands.spectatePlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.arr4nn.staffspectate.StaffSpectate;
import com.arr4nn.staffspectate.commands.subcommands.stopSpectate;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

  StaffSpectate plugin;
  private ArrayList<SubCommand> subcommands = new ArrayList<>();
  public CommandManager(StaffSpectate plugin){
    this.plugin = plugin;
    subcommands.add(new spectatePlayer(plugin));
    subcommands.add(new stopSpectate(plugin));
  }
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(sender instanceof Player){
        Player p = (Player) sender;
        Audience a = plugin.adventure().player(p);
        MiniMessage mm = MiniMessage.miniMessage();
      String permissionString = StaffSpectate.getInstance().getConfig().getString("permissions.use");
      if(sender.hasPermission(permissionString)){
        if(args.length > 0){

          for (int i = 0; i < getSubcommands().size(); i++){
            if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
              getSubcommands().get(i).perform(p,args);
            }
          }
        }else{
          a.sendMessage(mm.deserialize(plugin.getConfig().getString("language.commands.unknown-command")));
        }
      }else{
        a.sendMessage(mm.deserialize(plugin.getConfig().getString("language.commands.no-permissions")));
      }
    }


    return true;
  }

  public ArrayList<SubCommand> getSubcommands(){
    return subcommands;
  }
}
