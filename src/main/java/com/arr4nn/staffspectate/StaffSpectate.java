package com.arr4nn.staffspectate;

import com.arr4nn.staffspectate.commands.spectateAdminCommand;
import com.arr4nn.staffspectate.commands.spectateCommand;
import com.arr4nn.staffspectate.commands.spectatePOVCommand;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.*;
import java.util.*;

import static com.arr4nn.staffspectate.Logger.plugin;

public final class StaffSpectate extends JavaPlugin {

  private static StaffSpectate instance;
  private final String pluginVersion = getConfig().getString("plugin-version");
  public static StaffSpectate getInstance() { return instance; }
  public final PluginDescriptionFile pdf = this.getDescription();
  public final String version = pdf.getVersion();
  public boolean updateNeeded;

  // spectator UUID -> target UUID
  public static final Map<UUID, UUID> povTargets = new HashMap<>();

  public static final Set<UUID> gamemodeBypass = new HashSet<>();

  public static final Set<UUID> exitHereBypass = new HashSet<>();


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

  private static FileConfiguration dataStorage;
  private static File dataFile;


  @Override
  public void onEnable() {
    PluginManager pm = getServer().getPluginManager();

    Objects.requireNonNull(getCommand("ss")).setExecutor(new spectateCommand(this));
    Objects.requireNonNull(getCommand("ss")).setTabCompleter(new spectateCommand(this));

    Objects.requireNonNull(getCommand("sspov")).setExecutor(new spectatePOVCommand(this));
    Objects.requireNonNull(getCommand("sspov")).setTabCompleter(new spectatePOVCommand(this));

    Objects.requireNonNull(getCommand("ssadmin")).setExecutor(new spectateAdminCommand(this));
    Objects.requireNonNull(getCommand("ssadmin")).setTabCompleter(new spectateAdminCommand(this));

    pm.registerEvents(new Events(this),this);

    adventure = BukkitAudiences.create(this);

    createConfig();


    final int SPIGOT_RESOURCE_ID = 99739;

    new UpdateChecker(this, UpdateCheckSource.SPIGET, "" + SPIGOT_RESOURCE_ID + "") // A link to a URL that contains the latest version as String
            .setDownloadLink("https://www.spigotmc.org/resources/staff-spectate.99739") // You can either use a custom URL or the Spigot Resource ID
            .setDonationLink("https://ko-fi.com/arr4nn")
            .setChangelogLink(SPIGOT_RESOURCE_ID) // Same as for the Download link: URL or Spigot Resource ID
            .setNotifyOpsOnJoin(getConfig().getBoolean("notify_updates")) // Notify OPs on Join when a new version is found (default)
            .setNotifyByPermissionOnJoin("staffspectate.notify") // Also notify people on join with this permission
            .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion().addServerVersion())
            .checkEveryXHours(12) // Check every 30 minutes
            .setColoredConsoleOutput(true)
            .setSupportLink("https://discord.gg/wWfPJKC2mF")
            .checkNow(); // And check right now

    try {
      createFiles();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    new Logger(this);

    restoreData();

    getLogger().info("[Staff-Spectate] Plugin Version: "+plugin.getDescription().getVersion() +" has been enabled!");
  }

  public static File getLogFile() {
    return logFile;
  }

  @Override
  public void onDisable() {
    if (this.adventure != null) {
      this.adventure.close();
      this.adventure = null;
    }
    if(!vanishedPlayers.isEmpty()){
      try {
        getLogger().info("Saving spectator data...");
        this.saveData();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    // Plugin shutdown logic
    getLogger().info("has shut down successfully.");

  }

  public void saveData() throws IOException {
    for(Map.Entry<UUID,VanishData> entry: vanishedPlayers.entrySet()){
      dataStorage.set("data."+entry.getKey()+".gamemode",entry.getValue().getGameMode().toString());
      dataStorage.set("data."+entry.getKey()+".location",entry.getValue().getLocation());
    }
    dataStorage.save(dataFile);
  }
  public void restoreData(){
    try{
    dataStorage.getConfigurationSection("data").getKeys(false).forEach(key ->{
      VanishData vd = new VanishData(  GameMode.valueOf(dataStorage.getString("data."+key+".gamemode") ), dataStorage.getLocation("data."+key+".location"));
      vanishedPlayers.put(UUID.fromString(key), vd);
    });
    }catch (NullPointerException e){ return;}
  }

  private void setDefaultConfigValues() {
    getConfig().addDefault("return-to-location", true);
    getConfig().addDefault("notify_updates", true);
    getConfig().addDefault("log-to-file", true);
    getConfig().addDefault("language.commands.no-permissions", "<red>You don't have enough permissions to do this!");
    getConfig().addDefault("language.commands.spectate.self-spectate", "<red>You can't spectate yourself.");
    getConfig().addDefault("language.commands.spectate.spectating", "<green>You are now spectating {user}.");
    getConfig().addDefault("language.commands.spectate.no-user", "<red>Please provide a user to spectate! (/spectate <player>)");
    getConfig().addDefault("language.commands.stopSpectate.spectate-end", "<green>Returned you to your previous location.");
    getConfig().addDefault("language.commands.stopSpectate.not-spectating", "<red>You are not spectating anyone!");
    getConfig().addDefault("language.commands.spectate.already-spectating", "<red>You need to leave your spectating session to use this!");

  }

  public static File logFile;


  private void createFiles() throws IOException {
    dataFile = new File(getDataFolder().getAbsolutePath(),"data.yml");

    if(!dataFile.exists()){
      try {
        dataFile.createNewFile();
        getLogger().info("Created a new data.yml file!");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    dataStorage = YamlConfiguration.loadConfiguration(dataFile);


    logFile = new File(getDataFolder().getAbsolutePath(),"logs.txt");
    if(!logFile.exists()){
      try {
        logFile.createNewFile();
        getLogger().info("Created a new logs.txt file!");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }
}

