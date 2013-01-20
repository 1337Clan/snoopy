package com.leetclan.snoopy;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.google.common.collect.Sets;

public class MessageListener extends SnoopyListener {
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
  
  public MessageListener(Snoopy snoopy) {
    super(snoopy);
    
    snoopy.getServer().getPluginManager().registerEvents(this, snoopy);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChannelChatEvent(ChannelChatEvent event) {
    Channel channel = event.getChannel();
    Collection<SnoopingPlayer> snoopers = getSnoopy().getSnoopersFor(channel);
    
    Chatter sender = event.getSender();
    
    if (snoopers.size() > 0) {
      for (SnoopingPlayer snooper : snoopers) {
        snooper.tellAbout(channel, sender.getPlayer(), event.getMessage());
      }
    } else {
      snoopers = getSnoopy().getSnoopersFor(sender.getPlayer());
      
      for (SnoopingPlayer snooper : snoopers) {
        snooper.tellAbout(sender.getPlayer(), event.getMessage());
      }
    }
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    String commandName = event.getMessage().toLowerCase(Locale.ENGLISH).split("\\s+")[0].substring(1);
    if (!triggeringCommands.contains(commandName)) return;
    
    for (SnoopingPlayer snooper : getSnoopy().getSnoopersFor(event.getPlayer())) {
      snooper.tellAbout(event.getPlayer(), event.getMessage());
    }
  }
}
