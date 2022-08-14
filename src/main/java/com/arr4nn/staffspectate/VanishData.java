package com.arr4nn.staffspectate;

import org.bukkit.GameMode;
import org.bukkit.Location;

public class VanishData {
  GameMode gameMode;
  Location location;

  public GameMode getGameMode() {
    return gameMode;
  }

  public Location getLocation() {
    return location;
  }

  public VanishData(GameMode gameMode, Location location) {
    this.gameMode = gameMode;
    this.location = location;
  }
}
