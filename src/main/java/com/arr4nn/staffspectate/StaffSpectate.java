package com.arr4nn.staffspectate;

import com.arr4nn.staffspectate.commands.spectateAdminCommand;
import com.arr4nn.staffspectate.commands.spectateCommand;
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
import java.util.Objects;
import java.util.UUID;

public final class StaffSpectate extends JavaPlugin implements Listener {

  private static StaffSpectate instance;
  private final String pluginVersion = getConfig().getString("plugin-version");
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

  void createConfig() {
    // This saves the config.yml included in the .jar file, but it will not
    // overwrite an existing config.yml
    this.saveDefaultConfig();
    reloadConfig();
    ConfigUpdater.updateConfig();
    setDefaultConfigValues();

  }

  @Override
  public void onEnable() {

    PluginManager pm = getServer().getPluginManager();
    Objects.requireNonNull(getCommand("ss")).setExecutor(new spectateCommand(this));
    Objects.requireNonNull(getCommand("ss")).setTabCompleter(new spectateCommand(this));

    Objects.requireNonNull(getCommand("ssadmin")).setExecutor(new spectateAdminCommand(this));
    Objects.requireNonNull(getCommand("ssadmin")).setTabCompleter(new spectateAdminCommand(this));

    pm.registerEvents(new Events(this),this);
    pm.registerEvents(this, this);
    adventure = BukkitAudiences.create(this);
    createConfig();
    new UpdateChecker(this, 99739).getVersion(version -> {
      if (pluginVersion.equals(version)) {
        getLogger().info("Plugin version is the latest");
        updateNeeded = false;
      } else {
        updateNeeded = true;
        getLogger().info("Plugin version is not latest. Please update this plugin.");
      }
    });

    getLogger().info(ChatColor.translateAlternateColorCodes('&',"&a[Staff-Spectate] Plugin Version: "+pluginVersion +" has been enabled!"));

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
    if (player.isOp() || player.hasPermission("staffspectate.notify")) {
      if(updateNeeded){
        if (getConfig().getBoolean(Config.NOTIFY_UPDATES)) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[&eStaff-Spectate&a]&7 A plugin update is available please update at: &ehttps://www.spigotmc.org/resources/staff-spectate.99739/"));
        }
      }
    }
  }
  private void setDefaultConfigValues() {
    getConfig().addDefault("return-to-location", true);
    getConfig().addDefault("notify_updates", true);
    getConfig().addDefault("language.commands.no-permissions", "<red>You don't have enough permissions to do this!");
    getConfig().addDefault("language.commands.spectate.self-spectate", "<red>You can't spectate yourself.");
    getConfig().addDefault("language.commands.spectate.spectating", "<green>You are now spectating {user}.");
    getConfig().addDefault("language.commands.spectate.no-user", "<red>Please provide a user to spectate! (/spectate <player>)");
    getConfig().addDefault("language.commands.stopSpectate.spectate-end", "<green>Returned you to your previous location.");
    getConfig().addDefault("language.commands.stopSpectate.not-spectating", "<red>You are not spectating anyone!");
    getConfig().addDefault("language.commands.spectate.already-spectating", "<red>You need to leave your spectating session to use this!");

  }
}

