package com.erdgy.nba.model;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;

public class Player {
  
  //identity variables
  private String id;
  private String fullName;
  private String position;

  //stat variables
  private HashMap<String, Double> stats = new HashMap<>();

  //Constructor
  public Player(String idName, String fName, JsonNode statNode, int games){
    
    this.id = idName;
    this.fullName = fName;
    this.position = statNode.get("position").asText();
    stats.put("pts", statNode.get("points").asDouble()/games);
    stats.put("ast", statNode.get("assists").asDouble()/games);
    stats.put("3ptATM", statNode.get("threeAttempts").asDouble()/games);
    stats.put("3ptPCT", statNode.get("threePercent").asDouble());
    stats.put("reb", statNode.get("totalRb").asDouble()/games);
    stats.put("stl", statNode.get("steals").asDouble()/games);
    stats.put("blk", statNode.get("blocks").asDouble()/games);
    stats.put("fta", statNode.get("ftAttempts").asDouble()/games);
    stats.put("ftPCT", statNode.get("ftPercent").asDouble());
    stats.put("fga", statNode.get("fieldAttempts").asDouble()/games);
    stats.put("fgPCT", statNode.get("fieldPercent").asDouble());
    stats.put("to", statNode.get("turnovers").asDouble()/games);
    stats.put("min", statNode.get("minutesPg").asDouble()/games);
    stats.put("pf", statNode.get("personalFouls").asDouble()/games);
    stats.put("efgPCT", statNode.get("effectFgPercent").asDouble());

  }
  //Getter methods to get the stats
  public Double getStat(String key){
      return stats.get(key);
  }

  public String getId(){
    return this.id;
  }

  public String getFullName(){
    return this.fullName;
  }

  public String getPosition(){
    return this.position; 
  }

  public List<String> getStatList(){
    return new ArrayList<String>(stats.keySet());
  }
}
