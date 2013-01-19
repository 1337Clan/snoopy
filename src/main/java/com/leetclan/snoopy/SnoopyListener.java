package com.leetclan.snoopy;

import org.bukkit.event.Listener;

public abstract class SnoopyListener implements Listener {
  private Snoopy snoopy;
  
  public SnoopyListener(Snoopy snoopy) {
    this.snoopy = snoopy;
  }
  
  public Snoopy getSnoopy() {
    return snoopy;
  }
}
