package com.leetclan.snoopy.commands;

import org.bukkit.command.CommandExecutor;

import com.leetclan.snoopy.Snoopy;

public abstract class BasicCommandExecutor implements CommandExecutor {
  private Snoopy snoopy;
  
  public BasicCommandExecutor(Snoopy snoopy) {
    this.snoopy = snoopy;
  }
  
  public Snoopy getSnoopy() {
    return snoopy;
  }
  
  public abstract String[] getTriggeringCommands();
}
