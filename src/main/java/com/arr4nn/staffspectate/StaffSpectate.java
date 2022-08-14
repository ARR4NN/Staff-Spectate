package com.arr4nn.staffspectate;

import com.arr4nn.staffspectate.commands.CommandManager;
import com.arr4nn.staffspectate.commands.TabCompleter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.arr4nn.staffspectate.events.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StaffSpectate extends JavaPlugin implements Listener {

  private static StaffSpectate instance;
  private final String configVersion = getConfig().getString("config-version");
  public static StaffSpectate getInstance() { return instance; }
  public final PluginDescriptionFile pdf = this.getDescription();
  public final String version = pdf.getVersion();
  public boolean updateNeeded;

  private BukkitAudiences adventure;
  public @NotNull BukkitAudiences adventure() {
    if (this.adventure == null) {
      throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
    }
    return this.adventure;
  }

  {
    instance = this;
  }
  public static Map<UUID, VanishData> vanishedPlayers = new HashMap<>();
  @Override
  public void onEnable() {
    PluginManager pm = getServer().getPluginManager();
    getCommand("ss").setExecutor(new CommandManager(this));
    getCommand("ss").setTabCompleter(new TabCompleter());

    pm.registerEvents(new Events(),this);
    pm.registerEvents(this, this);
    adventure = BukkitAudiences.create(this);
    if (configVersion == null || getConfig().getString("config-version").isEmpty()) {
      pm.disablePlugin(this);
      getLogger().info("Restart server for functionality.");
    }
    getConfig().options().copyDefaults();
    saveDefaultConfig();
    new UpdateChecker(this, 99739).getVersion(version -> {
      if (configVersion.equals(version)) {
        getLogger().info("Plugin version is the latest");
        updateNeeded = false;
      } else {
        updateNeeded = true;
        getLogger().info("Plugin version is not latest. Please update this plugin.");
      }
    });

//    int pluginId = 99739;
//    Metrics metrics = new Metrics(this, pluginId);

    getLogger().info(ChatColor.translateAlternateColorCodes('&',"&a[Staff-Spectate] Plugin Version: "+configVersion +" has been enabled!"));

  }

  @Override
  public void onDisable() {
    if (this.adventure != null) {
      this.adventure.close();
      this.adventure = null;
    }
    // Plugin shutdown logic
    getLogger().info("has stopped");

  }


  @EventHandler(priority = EventPriority.HIGH)
  private void onJoin(@NotNull PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (player.isOp()) {
      if(updateNeeded){
        if (this.getConfig().getBoolean("notify_op_updates") == true) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[&eStaff-Spectate&a]&7 A plugin update is available please update at: &ehttps://www.spigotmc.org/resources/staff-spectate.99739/"));
        }
      }
    }
  }

}

