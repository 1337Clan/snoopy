package com.leetclan.snoopy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

public class PlayerLookup {
  private final Snoopy snoopy;
  
  public PlayerLookup(Snoopy snoopy) {
    this.snoopy = snoopy;
  }
  
  public Set<Player> matchPlayers(final String prefix) {
    final String regex = "^" + prefix.toLowerCase() + ".*";
    
    Collection<Player> matchedPlayers = Collections2.filter(Arrays.asList(snoopy.getServer().getOnlinePlayers()), new Predicate<Player>() {
      public boolean apply(Player player) {
        return player.getName().toLowerCase().matches(regex);
      }
    });
    
    return Sets.newHashSet(matchedPlayers);
  }
}
