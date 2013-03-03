package com.leetclan.snoopy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.leetclan.snoopy.commands.BasicCommandExecutor;
import com.leetclan.snoopy.commands.SnoopCommandExecutor;
import com.leetclan.snoopy.commands.SnoopCommandExecutor.OffCommand;
import com.leetclan.snoopy.commands.SnoopCommandExecutor.SnoopChannelsCommand;
import com.leetclan.snoopy.commands.SnoopCommandExecutor.SnoopPlayersCommand;
import com.leetclan.snoopy.util.Chat;
import com.leetclan.snoopy.util.ServerLogger;

public class Snoopy extends JavaPlugin {
  private final List<? extends BasicCommandExecutor> commands;
  
  private final PlayerLookup playerLookup;
  private final Map<String, SnoopingPlayer> snoopers;
  
  private interface SnoopDelegate {
    public void snoopAll(SnoopingPlayer snooper);
    public int addTarget(SnoopingPlayer snooper, String targetName);
    public int removeTarget(SnoopingPlayer snooper, String targetName);
  }
  
  private final SnoopDelegate snoopPlayerDelegate = new SnoopDelegate() {
    @Override
    public void snoopAll(SnoopingPlayer snooper) {
      snooper.setSnoopAllPlayers(true);
    }

    @Override
    public int addTarget(SnoopingPlayer snooper, String targetName) {
      Set<Player> players = getPlayerLookup().matchPlayers(targetName);
      
      switch (players.size()) {
        case 0:
          Chat.scold(snooper.getPlayer(), "'%s' did not match any players.", targetName);
          return 0;
          
        case 1:
          Player target = players.toArray(new Player[0])[0];
          
          if (target == snooper.getPlayer()) {
            return 0;
          }
            
          snooper.snoopOn(target);
          return 1;
        
        default:
          Chat.scold(snooper.getPlayer(), "Target name '%s' is ambiguous. Did you mean:", targetName);
          
          for (Player player : players) {
            Chat.scold(snooper.getPlayer(), "  - %s", player.getName());
          }
          
          return 0;
      }
    }

    @Override
    public int removeTarget(SnoopingPlayer snooper, String targetName) {
      Set<Player> players = getPlayerLookup().matchPlayers(targetName);
      int numTargetsRemoved = 0;
      
      for (Player player : players) {
        if (snooper.isSnoopingOn(player)) {
          numTargetsRemoved++;
          snooper.stopSnoopingOn(player);
        }
      }
      
      return numTargetsRemoved;
    }
  };
  
  private final SnoopDelegate snoopChannelDelegate = new SnoopDelegate() {
    @Override
    public void snoopAll(SnoopingPlayer snooper) {
      snooper.setSnoopAllChannels(true);
    }

    @Override
    public int addTarget(SnoopingPlayer snooper, String targetName) {
      Channel channel = Herochat.getChannelManager().getChannel(targetName);
      
      if (channel != null) {
        if (channel.isMember(Herochat.getChatterManager().getChatter(snooper.getPlayer()))) {
          Chat.tell(snooper.getPlayer(), "{yellow}- you are already in channel '%s'", targetName);
          return 0;
        }
        
        if (!snooper.isSnoopingOn(channel)) {
          snooper.snoopOn(channel);
          return 1;
        }
      } else {
        Chat.scold(snooper.getPlayer(), "- channel '%s' does not exist.", targetName);
      }
      
      return 0;
    }

    @Override
    public int removeTarget(SnoopingPlayer snooper, String targetName) {
      Channel channel = Herochat.getChannelManager().getChannel(targetName);
      
      if (channel != null) {
        if (snooper.isSnoopingOn(channel)) {
          snooper.stopSnoopingOn(channel);
          return 1;
        }
      } else {
        Chat.scold(snooper.getPlayer(), "- channel '%s' does not exist.", targetName);
      }

      return 0;
    }
  };
  
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
    
    new MessageListener(this);
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
  
  public void snoopPlayers(String snooperName, String...playerNames) {
    snoopTargets(snooperName, snoopPlayerDelegate, playerNames);
  }
  
  public void snoopChannels(String snooperName, String...channelNames) {
    snoopTargets(snooperName, snoopChannelDelegate, channelNames);
  }
  
  private void snoopTargets(String snooperName, SnoopDelegate delegate, String...targetNames) {
    SnoopingPlayer snooper = getSnooper(snooperName);
    
    if (targetNames.length == 0) {
      delegate.snoopAll(snooper);
      Chat.tell(snooper.getPlayer(), "{green}Active snooping enabled!");
      return;
    }
    
    int numNewTargets = 0;
    int numTargetsRemoved = 0;
    
    for (String targetName : targetNames) {
      boolean addTarget = true;
      
      if (targetName.startsWith("-")) {
        targetName = targetName.substring(1);
        addTarget = false;
      }

      if (addTarget) {
        numNewTargets += delegate.addTarget(snooper, targetName);
      } else {
        numTargetsRemoved += delegate.removeTarget(snooper, targetName);
      }
    }
    
    Chat.tell(snooper.getPlayer(), "Snooping targets updated: {green}+%d{/}, {red}-%d{/}", numNewTargets, numTargetsRemoved);
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
