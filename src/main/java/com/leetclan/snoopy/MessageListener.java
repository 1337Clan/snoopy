package com.leetclan.snoopy;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.google.common.collect.Sets;

public class MessageListener implements Listener {
  public static final Set<String> triggeringCommands = Sets.newHashSet(
      "msg",
      "r", 
      "mail", 
      "m", 
      "t", 
      "emsg", 
      "tell", 
      "er", 
      "reply", 
      "ereply", 
      "email");

  private final Snoopy snoopy;
  
  public MessageListener(Snoopy snoopy) {
    this.snoopy = snoopy;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChannelChatEvent(ChannelChatEvent event) {
    Channel channel = event.getChannel();
    Collection<SnoopingPlayer> snoopers = snoopy.getSnoopersFor(channel);

    Chatter sender = event.getSender();
    Set<Chatter> members = channel.getMembers();
    
    if (snoopers.size() > 0) {
      for (SnoopingPlayer snooper : snoopers) {
        snooper.tellAbout(channel, sender.getPlayer(), event.getMessage());
      }
    } else {
      for (Chatter member : members) {
        if (member.equals(sender)) continue;
        
        snoopers = snoopy.getSnoopersFor(member.getPlayer());
        
        for (SnoopingPlayer snooper : snoopers) {
          snooper.tellAbout(member.getPlayer(), event.getMessage());
        }
      }
    }
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    String commandName = event.getMessage().toLowerCase(Locale.ENGLISH).split("\\s+")[0].substring(1);
    if (!triggeringCommands.contains(commandName)) return;
    
    for (SnoopingPlayer snooper : snoopy.getSnoopersFor(event.getPlayer())) {
      snooper.tellAbout(event.getPlayer(), event.getMessage());
    }
  }
}
