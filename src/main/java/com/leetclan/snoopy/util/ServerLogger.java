package com.leetclan.snoopy.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerLogger {
  public static final String TAG = "[Snoopy]";
  private static ServerLogger instance = new ServerLogger();
  
  private final Logger logger = Logger.getLogger("Minecraft");
  
  public static ServerLogger getInstance() {
    return instance;
  }
  
  public void log(Level level, String message, Object...formatArgs) {
    logger.log(level, message, formatArgs);
  }
  
  public void info(String message, Object...formatArgs) {
    logger.info(tag(message));
  }
  
  public void warning(String message, Object...formatArgs) {
    logger.warning(tag(message));
  }
  
  public void error(String message, Object...formatArgs) {
    logger.severe(tag(message));
  }
  
  public static String tag(String message, Object...formatArgs) {
    message = String.format(message, formatArgs);
    return String.format("%s %s", TAG, message);
  }
}
