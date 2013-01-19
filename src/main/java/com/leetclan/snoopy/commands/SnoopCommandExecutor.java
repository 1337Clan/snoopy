package com.leetclan.snoopy.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.leetclan.snoopy.SnoopingPlayer;
import com.leetclan.snoopy.Snoopy;

public class SnoopCommandExecutor extends CommandExecutorChain {
  public static class OffCommand extends BasicCommandExecutor {
    public OffCommand(Snoopy snoopy) {
      super(snoopy);
    }

    public boolean onCommand(CommandSender sender, Command command,
        String label, String[] args) {
      
      getSnoopy().removeSnooper(sender.getName());
      return true;
    }

    @Override
    public String[] getTriggeringCommands() {
      return new String[] {
        "off",
        "stop"
      };
    }
  }
  
  public static abstract class SnoopTargetCommand extends BasicCommandExecutor {
    public SnoopTargetCommand(Snoopy snoopy) {
      super(snoopy);
    }
    
    public abstract String[] getDefaultTargetNames();
    public abstract void addTarget(SnoopingPlayer snooper, String targetName);
    public abstract void removeTarget(SnoopingPlayer snooper, String targetName);

    public boolean onCommand(CommandSender sender, Command command,
        String label, String[] targetNames) {
      
      if (targetNames.length == 0) targetNames = getDefaultTargetNames();
      
      SnoopingPlayer snooper = getSnoopy().getSnooper(sender.getName());
      for (String targetName : targetNames) {
        boolean addTarget = true;
        
        if (targetName.startsWith("-")) {
          targetName = targetName.substring(1);
          addTarget = false;
        }

        if (addTarget) {
          addTarget(snooper, targetName);
        } else {
          removeTarget(snooper, targetName);
        }
      }
      
      return true;
    }
  }
  
  public static class SnoopPlayersCommand extends SnoopTargetCommand {
    public SnoopPlayersCommand(Snoopy snoopy) {
      super(snoopy);
    }

    @Override
    public String[] getDefaultTargetNames() {
      return Collections2.transform(Arrays.asList(getSnoopy().getServer().getOnlinePlayers()), new Function<Player, String>() {
        public String apply(Player player) {
          return player.getName();
        }
      }).toArray(new String[0]);
    }

    @Override
    public void addTarget(SnoopingPlayer snooper, String targetName) {
      snooper.snoopOn(getSnoopy().getServer().getPlayer(targetName));
    }

    @Override
    public void removeTarget(SnoopingPlayer snooper, String targetName) {
      snooper.stopSnoopingOn(getSnoopy().getServer().getPlayer(targetName));
    }
    
    @Override
    public String[] getTriggeringCommands() {
      return new String[] {
          "player",
          "players",
          "people",
          "ppl",
          "p"
      };
    }
  }
  
  public static class SnoopChannelsCommand extends SnoopTargetCommand {
    public SnoopChannelsCommand(Snoopy snoopy) {
      super(snoopy);
    }

    @Override
    public String[] getDefaultTargetNames() {
      return Collections2.transform(Herochat.getChannelManager().getChannels(), new Function<Channel, String>() {
        public String apply(Channel channel) {
          return channel.getName();
        }
      }).toArray(new String[0]);
    }

    @Override
    public void addTarget(SnoopingPlayer snooper, String targetName) {
      snooper.snoopOn(Herochat.getChannelManager().getChannel(targetName));
    }

    @Override
    public void removeTarget(SnoopingPlayer snooper, String targetName) {
      snooper.stopSnoopingOn(Herochat.getChannelManager().getChannel(targetName));
    }
    
    @Override
    public String[] getTriggeringCommands() {
      return new String[] {
          "channel",
          "channels",
          "chans",
          "chan",
          "c"
      };
    }
  }

  public SnoopCommandExecutor(Snoopy snoopy, BasicCommandExecutor...subCommands) {
    super(snoopy, subCommands);
  }

  public String[] getTriggeringCommands() {
    return new String[] {
      "snoop"
    };
  }

  @Override
  protected boolean onCommand(CommandSender sender, String label, String[] args) {
    if (!getSnoopy().isSnooping(sender.getName())) {
      getSnoopy().addSnooper(sender.getName());
    }

    return true;
  }
}
