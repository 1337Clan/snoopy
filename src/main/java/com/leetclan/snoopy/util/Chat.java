/*
 * 1337Clan Hunger Games Chat System
 */
package com.leetclan.snoopy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

/**
 * 
 */
public final class Chat {
  public static final ChatColor DEFAULT_COLOR = ChatColor.WHITE;
  
  @SuppressWarnings("serial")
  private static final Map<String, ChatColor> colorMap = new HashMap<String, ChatColor>() {
    {
      for (ChatColor color : ChatColor.values()) {
        put(color.name().toLowerCase(), color);
      }
    }
  };
  
  private Chat() {}

  public static String format(String message, Object...args) {
    message = $i(message);
    return String.format(message, args);
  }
  
  public static String asLines(String...lines) {
    for (int i = 0; i < lines.length; ++i) {
      lines[i] = $i(lines[i]);
    }
    
    return Joiner.on('\n').join(lines);
  }
  
  /**
   * Sends a message to the player in red because they were naughty.
   * 
   * @param player the player to whom the message should be sent
   * @param message the message to send
   * @param args any format args
   */
  public static void scold(Player player, String message, Object...args) {
    String meanString = String.format("{red}%s", message);
        
    tell(player, meanString, args);
  }
  
  public static void tell(Player player, String message, Object...args) {
    player.sendMessage(format(message, args));
  }
  
  /**
   * Sends a message to all players on the server.
   * 
   * @param message the message to send
   */
  public static void broadcast(String message, Object...args) {
    message = String.format("%s %s", ServerLogger.TAG, message);
    Bukkit.broadcastMessage(format(message, args));
  }
  
  /**
   * Interpolates chat colors in a  string by removing all instances of the pattern
   * 
   *    \{[^\}]\}
   * 
   * where the contents of the curly-braces is the name of the {@link ChatColor}
   * class to use to color the message at that position.  The curly braces can be escaped
   * with a simple '\\' in the string to print out the text as-is.  Unclosed braces will
   * be appended to the output as-is.
   * 
   * Eg.  "{blue}this is blue{red}and this is red" becomes "§9this is blue§cand this is red"
   * 
   * @param message the message to color-interpolate
   * @return the message with the colors inserted
   */
  public static String $i(String message) {
    StringBuilder builder = new StringBuilder();
    Stack<ChatColor> colorStack = new Stack<>();
    colorStack.add(DEFAULT_COLOR);
    
    for (int i = 0; i < message.length(); ++i) {
      switch (message.charAt(i)) {
        case '{':
          int end = balanceInterpolation(message, i);
          String colorName = message.substring(i + 1, end);
          
          boolean isClosing = colorName.equals("/");
          
          if (isClosing) {
            colorStack.pop();
          } else {
            ChatColor color = colorMap.containsKey(colorName) ? colorMap.get(colorName) : DEFAULT_COLOR;
            colorStack.push(color);
          }
          
          builder.append(colorStack.peek());
          i = end;
          break;

        // In the case where we escape, and the default case
        // we always just append the current character.
        case '\\':
          // But we want to skip the escape character...
          ++i;
          
        default:
          builder.append(message.charAt(i));
      }
    }
    
    return builder.toString();
  }
  
  /**
   * Find the index of the last closing brace of the color interpolation
   * 
   * @param message the message containing the interpolation
   * @param startIndex the start index of the interpolation
   * @return the index of the last closing brace
   */
  private static int balanceInterpolation(String message, int startIndex) {
    for (int i = startIndex + 1; i < message.length(); ++i) {
      if (message.charAt(i) == '}') {
        return i;
      }
    }
    
    return message.length();
  }
  
}