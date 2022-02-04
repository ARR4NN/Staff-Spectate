package xyz.arr4nn.vulcanspectate.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.arr4nn.vulcanspectate.VulcanSpectate;
import xyz.arr4nn.vulcanspectate.commands.subcommands.spectatePlayer;
import xyz.arr4nn.vulcanspectate.commands.subcommands.stopSpectate;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {


  private ArrayList<SubCommand> subcommands = new ArrayList<>();
  public CommandManager(){
    subcommands.add(new spectatePlayer());
    subcommands.add(new stopSpectate());
  }
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(sender instanceof Player){
      String permissionString = VulcanSpectate.getInstance().getConfig().getString("permission");
      if(sender.hasPermission(permissionString)){
        Player p = (Player) sender;
        if(args.length > 0){

          for (int i = 0; i < getSubcommands().size(); i++){
            if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
              getSubcommands().get(i).perform(p,args);
            }
          }
        }else{
          sender.sendMessage(ChatColor.RED+"Unknown Sub-Command");
        }
      }else{
        sender.sendMessage(ChatColor.RED+"You don't have permission to use this command!");
      }
    }


    return true;
  }

  public ArrayList<SubCommand> getSubcommands(){
    return subcommands;
  }
}
