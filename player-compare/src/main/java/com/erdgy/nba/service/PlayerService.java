package com.erdgy.nba.service;

import java.util.List;

import com.erdgy.nba.model.Player;

public class PlayerService {
  public static Player findPlayerByName(List<Player> players, String target){
    Player tPlayer = null;

    for(Player p : players){
        if(p.getFullName().toLowerCase().contains(target.toLowerCase())){
            tPlayer = p;
            break;
        }
    }
      return tPlayer;
    }
}
