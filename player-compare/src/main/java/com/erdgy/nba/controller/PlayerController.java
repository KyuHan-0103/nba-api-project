package com.erdgy.nba.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.erdgy.nba.DataCache;
import com.erdgy.nba.model.Player;
import com.erdgy.nba.service.PlayerService;
import com.erdgy.nba.service.PlayerSimilarity;
import com.erdgy.nba.service.SimilarityService;

import org.springframework.web.bind.annotation.CrossOrigin;
//Import Spring Boot tools
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class PlayerController {
  
  private final DataCache cache;

  public PlayerController(DataCache cache){
    this.cache = cache;
  }

  @GetMapping("/similar")
  public List<Player> getSimilarPlayers(
    @RequestParam String name,
    @RequestParam(required = false) Double pts,
    @RequestParam(required = false) Double ast,
    @RequestParam(required = false) Double reb,
    @RequestParam(required = false) Double stl,
    @RequestParam(required = false) Double blk,
    @RequestParam(required = false) Double threePtPCT,
    @RequestParam(required = false) Double threePtATM,
    @RequestParam(required = false) Double fgPCT,
    @RequestParam(required = false) Double fga,
    @RequestParam(required = false) Double ftPCT,
    @RequestParam(required = false) Double fta,
    @RequestParam(required = false) Double efgPCT,
    @RequestParam(required = false) Double to,
    @RequestParam(required = false) Double min,
    @RequestParam(required = false) Double pf){
    
    HashMap<String, Double> weights = new HashMap<String, Double>();
    if(pts != null) weights.put("pts", pts);
    if(ast != null) weights.put("ast", ast);
    if(reb != null) weights.put("reb", reb);
    if(stl != null) weights.put("stl", stl);
    if(blk != null) weights.put("blk", blk);
    if(threePtPCT != null) weights.put("3ptPCT", threePtPCT);
    if(threePtATM != null) weights.put("3ptATM", threePtATM);
    if(fgPCT != null) weights.put("fgPCT", fgPCT);
    if(fga != null) weights.put("fga", fga);
    if(ftPCT != null) weights.put("ftPCT", ftPCT);
    if(fta != null) weights.put("fta", fta);
    if(efgPCT != null) weights.put("efgPCT", efgPCT);
    if(to != null) weights.put("to", to);
    if(min != null) weights.put("min", min);
    if(pf != null) weights.put("pf", pf);
    
    List<Player> players = cache.getPlayers();
    
    Player targetPlayer = PlayerService.findPlayerByName(players, name);

    if(targetPlayer == null){
      return List.of();
    }

    SimilarityService service = weights.isEmpty()
      ? new SimilarityService()
      : new SimilarityService(weights);

    List<PlayerSimilarity> similarPlayers = service.findSimilar(targetPlayer, players);
    List<Player> topFivePlayers = new ArrayList<Player>();

      for(int i = 0; i < 5; i++){
        topFivePlayers.add(similarPlayers.get(i).getPlayer());
      }

    return topFivePlayers;
  }

  @GetMapping("/player")
  public Player getPlayer(@RequestParam String name) {
    List<Player> players = cache.getPlayers();
    return PlayerService.findPlayerByName(players, name);
  }
}
