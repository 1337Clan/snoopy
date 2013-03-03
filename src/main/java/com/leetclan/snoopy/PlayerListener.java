package com.leetclan.snoopy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener extends SnoopyListener {
  public PlayerListener(Snoopy snoopy) {
    super(snoopy);
  }

  @EventHandler()
  public void onPlayerLoginEvent(PlayerLoginEvent event) {
    Player player = event.getPlayer();
    if (player.hasPermission("snoopy.snoop")) {
      getSnoopy().addSnooper(player.getName());
      getSnoopy().snoopPlayers(player.getName());
    }
  }

  @EventHandler()
  public void onPlayerQuitEvent(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (getSnoopy().isSnooping(player.getName())) getSnoopy().removeSnooper(player.getName());
  }
}
