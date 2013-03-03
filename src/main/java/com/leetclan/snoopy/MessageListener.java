package com.leetclan.snoopy;

import java.util.Collection;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.ConversationChannel;
import com.dthielke.herochat.Herochat;
import com.google.common.collect.Sets;

public class MessageListener extends SnoopyListener {
  public MessageListener(Snoopy snoopy) {
    super(snoopy);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChannelChatEvent(ChannelChatEvent event) {
    Channel channel = event.getChannel();
    Chatter sender = event.getSender();
    
    // Check to see if the channel is a private channel between two people (through messages)
    if (channel instanceof ConversationChannel) {
      onConversationChannelChatEvent(sender, event.getMessage(), (ConversationChannel) channel);
      return;
    }
    
    // Tell the snoopers about a chat in the channel.
    Collection<SnoopingPlayer> snoopers = getSnoopy().getSnoopersFor(channel);
    for (SnoopingPlayer snooper : snoopers) {
      if (channel.isMember(Herochat.getChatterManager().getChatter(snooper.getPlayer()))) continue;
      
      snooper.tellAbout(channel, sender.getPlayer(), event.getMessage());
    }
  }
  
  private void onConversationChannelChatEvent(Chatter sender, String message, ConversationChannel channel) {
    Set<Chatter> chatters = channel.getMembers();
    chatters.remove(sender);
    Chatter recipient = chatters.toArray(new Chatter[0])[0];
    
    Collection<SnoopingPlayer> senderSnoopers = getSnoopy().getSnoopersFor(sender.getPlayer());
    Collection<SnoopingPlayer> recipientSnoopers = getSnoopy().getSnoopersFor(recipient.getPlayer());
    
    Set<SnoopingPlayer> snoopers = Sets.newHashSet(senderSnoopers);
    snoopers.addAll(recipientSnoopers);
    
    for (SnoopingPlayer snooper : snoopers) {
      if (snooper.getPlayer() == sender.getPlayer() || snooper.getPlayer() == recipient.getPlayer()) continue;
      
      snooper.tellAbout(sender.getPlayer(), recipient.getPlayer(), message);
    }
  }
}
