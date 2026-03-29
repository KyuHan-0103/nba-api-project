package com.erdgy.nba.model;

import java.util.HashMap;

public class Player {
  
  //identity variables
  private String id;
  private String fullName;

  //stat variables
  private HashMap<String, Double> stats = new HashMap<>();

  //Constructor
  public Player(String idName, String fName, double points, double assists, double threePtPct, double threePtAtm){
    
    this.id = idName;
    this.fullName = fName;
    stats.put("pts", points);
    stats.put("ast", assists);
    stats.put("3ptPCT", threePtPct);
    stats.put("3ptATM", threePtAtm);

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

}
