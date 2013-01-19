package com.leetclan.snoopy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dthielke.herochat.Channel;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.leetclan.snoopy.commands.BasicCommandExecutor;
import com.leetclan.snoopy.commands.SnoopCommandExecutor;
import com.leetclan.snoopy.commands.SnoopCommandExecutor.OffCommand;
import com.leetclan.snoopy.commands.SnoopCommandExecutor.SnoopChannelsCommand;
import com.leetclan.snoopy.commands.SnoopCommandExecutor.SnoopPlayersCommand;
import com.leetclan.snoopy.util.ServerLogger;

public class Snoopy extends JavaPlugin {
  private final List<? extends BasicCommandExecutor> commands;
  
  private final PlayerLookup playerLookup;
  private final Map<String, SnoopingPlayer> snoopers;
  
  public Snoopy() {
    commands = ImmutableList.of(
        new SnoopCommandExecutor(this,
            new OffCommand(this),
            new SnoopPlayersCommand(this),
            new SnoopChannelsCommand(this))
        );
    
    playerLookup = new PlayerLookup(this);
    snoopers = Maps.newHashMap();
  }
  
  @Override
  public void onEnable() {
    ServerLogger.getInstance().info("Starting Snoopy!");
    
    for (BasicCommandExecutor executor : commands) {
      getCommand(executor.getTriggeringCommands()[0]).setExecutor(executor);
    }
  }
  
  public Collection<SnoopingPlayer> getSnoopersFor(final Player target) {
    return Collections2.filter(snoopers.values(), new Predicate<SnoopingPlayer>() {
      public boolean apply(SnoopingPlayer snooper) {
        return snooper.isSnoopingOn(target);
      }
    });
  }
  
  public Collection<SnoopingPlayer> getSnoopersFor(final Channel target) {
    return Collections2.filter(snoopers.values(), new Predicate<SnoopingPlayer>() {
      public boolean apply(SnoopingPlayer snooper) {
        return snooper.isSnoopingOn(target);
      }
    });
  }
  
  public PlayerLookup getPlayerLookup() {
    return playerLookup;
  }
  
  public boolean isSnooping(String name) {
    return snoopers.containsKey(name);
  }
  
  public SnoopingPlayer getSnooper(String name) {
    return snoopers.get(name);
  }
  
  public void removeSnooper(String name) {
    snoopers.remove(name);
  }
  
  public void addSnooper(String name) {
    snoopers.put(name, new SnoopingPlayer(getServer().getPlayer(name)));
  }
}
