package com.leetclan.snoopy;

import java.util.Set;

import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.google.common.collect.Sets;
import com.leetclan.snoopy.util.Chat;

public class SnoopingPlayer {
  private final Player player;
  private final Set<Player> targetPlayers;
  private final Set<Channel> targetChannels;
  private boolean snoopAllPlayers;
  private boolean snoopAllChannels;

  public SnoopingPlayer(Player player) {
    this.player = player;

    targetPlayers = Sets.newHashSet();
    targetChannels = Sets.newHashSet();
  }

  public Player getPlayer() {
    return player;
  }

  public Player[] getTargetPlayers() {
    return targetPlayers.toArray(new Player[0]);
  }

  public boolean isSnoopingOn(Player target) {
    return targetPlayers.contains(target) || snoopAllPlayers;
  }

  public void stopSnoopingOn(Player target) {
    if (targetPlayers.contains(target)) targetPlayers.remove(target);
  }

  public void snoopOn(Player target) {
    targetPlayers.add(target);
  }

  public void tellAbout(Player sender, Player recipient, String message) {
    Chat.tell(player, "[snoop] {gray}%s -> %s: {green}%s", sender.getName(), recipient.getName(),
        message);
  }

  public Channel[] getTargetChannels() {
    return targetChannels.toArray(new Channel[0]);
  }

  public boolean isSnoopingOn(Channel target) {
    return targetChannels.contains(target) || snoopAllChannels;
  }

  public void stopSnoopingOn(Channel target) {
    if (targetChannels.contains(target)) targetChannels.remove(target);
  }

  public void snoopOn(Channel target) {
    targetChannels.add(target);
  }

  public void tellAbout(Channel target, Player sender, String message) {
    Chat.tell(player, "[snoop] {gray}[in %s] %s: {green}%s", target.getName(), sender.getName(),
        message);
  }

  public void setSnoopAllPlayers(boolean snoopAllPlayers) {
    this.snoopAllPlayers = snoopAllPlayers;
  }

  public void setSnoopAllChannels(boolean snoopAllChannels) {
    this.snoopAllChannels = snoopAllChannels;
  }
}
