package com.leetclan.snoopy.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leetclan.snoopy.Snoopy;

public abstract class CommandExecutorChain extends BasicCommandExecutor {
  private final Map<String, List<BasicCommandExecutor>> subCommandMap;

  public CommandExecutorChain(Snoopy snoopy, BasicCommandExecutor... subCommands) {
    super(snoopy);
    
    subCommandMap = Maps.newHashMap();

    for (BasicCommandExecutor subCommand : subCommands) {
      for (String triggeringCommand : subCommand.getTriggeringCommands()) {
        if (!subCommandMap.containsKey(triggeringCommand)) {
          subCommandMap.put(triggeringCommand,
              Lists.<BasicCommandExecutor> newArrayList());
        }

        subCommandMap.get(triggeringCommand).add(subCommand);
      }
    }
  }

  protected abstract boolean onCommand(CommandSender sender, String label,
      String[] args);

  public boolean onCommand(CommandSender sender, Command command, String label,
      String[] args) {

    boolean validCommand = onCommand(sender, label, args);
    if (validCommand && args.length > 1) {
      List<BasicCommandExecutor> executors = subCommandMap.containsKey(args[1]) ? subCommandMap
          .get(args[1]) : Lists.<BasicCommandExecutor> newArrayList();
   
      args = Arrays.copyOfRange(args, 1, args.length);    
      
      for (BasicCommandExecutor executor : executors) {
        validCommand = validCommand && executor.onCommand(sender, command, label, args);
      }
    }

    return validCommand;
  }
}
