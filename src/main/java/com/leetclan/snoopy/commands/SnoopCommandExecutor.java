package com.leetclan.snoopy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.leetclan.snoopy.SnoopingPlayer;
import com.leetclan.snoopy.Snoopy;
import com.leetclan.snoopy.util.Chat;

public class SnoopCommandExecutor extends CommandExecutorChain {
  public static class OffCommand extends BasicCommandExecutor {
    public OffCommand(Snoopy snoopy) {
      super(snoopy);
    }

    public boolean onCommand(CommandSender sender, Command command,
        String label, String[] args) {
      
      SnoopingPlayer snooper = getSnoopy().getSnooper(sender.getName());
      snooper.setSnoopAllChannels(false);
      snooper.setSnoopAllPlayers(false);
      
      getSnoopy().removeSnooper(snooper.getPlayer().getName());
      
      Chat.tell(getSnoopy().getServer().getPlayer(sender.getName()), "{red}Snooping disabled for all targets.");
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
  
  public static class SnoopPlayersCommand extends BasicCommandExecutor {
    public SnoopPlayersCommand(Snoopy snoopy) {
      super(snoopy);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      getSnoopy().snoopPlayers(sender.getName(), args);
      return true;
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
  
  public static class SnoopChannelsCommand extends BasicCommandExecutor {
    public SnoopChannelsCommand(Snoopy snoopy) {
      super(snoopy);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      getSnoopy().snoopChannels(sender.getName(), args);
      return true;
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
