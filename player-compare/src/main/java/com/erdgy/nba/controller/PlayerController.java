package com.erdgy.nba.controller;

import java.util.List;

import com.erdgy.nba.DataCache;
import com.erdgy.nba.api.NBAApiClient;
import com.erdgy.nba.model.Player;

//Import Spring Boot tools
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
  
  private final DataCache cache;
  @GetMapping("/similar")
  public String getSimilarPlayers(@RequestParam String name){
    List<Player> players = cache.getPlayers();
    return "Loaded " + players.size() + " players. Looking for " + name;
  }

  public PlayerController(DataCache cache){
    this.cache = cache;
  }
}
