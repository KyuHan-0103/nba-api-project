package com.erdgy.nba;

import java.util.List;

import org.springframework.stereotype.Component;

import com.erdgy.nba.api.NBAApiClient;
import com.erdgy.nba.model.Player;

@Component
public class DataCache {
  
  private final List<Player> players;

  public DataCache(NBAApiClient client){
    System.out.println("Loading players...");
    this.players = client.getAllPlayers();
    System.out.println("Loaded " + players.size() + " players.");
  }

  public List<Player> getPlayers(){
    return players;
  }

}
  

