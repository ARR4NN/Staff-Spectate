package xyz.arr4nn.vulcanspectate;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arr4nn.vulcanspectate.commands.CommandManager;
import xyz.arr4nn.vulcanspectate.events.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VulcanSpectate extends JavaPlugin {

  private static VulcanSpectate instance;

  public static VulcanSpectate getInstance() { return instance; }

  {
    instance = this;
  }
  public static Map<UUID, VanishData> vanishedPlayers = new HashMap<>();
  @Override
  public void onEnable() {
    getConfig().options().copyDefaults();
    saveDefaultConfig();
    getCommand("vs").setExecutor(new CommandManager());
    getServer().getPluginManager().registerEvents(new Events(),this);

    // Final Thing to do
    getLogger().info("has started");

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    getLogger().info("has stopped");

  }
}

