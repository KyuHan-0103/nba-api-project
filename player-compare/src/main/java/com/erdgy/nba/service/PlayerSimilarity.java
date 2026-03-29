package com.erdgy.nba.service;
import com.erdgy.nba.model.Player;

public class PlayerSimilarity {
  
  private Player bballPlayer;
  private double score;

  public PlayerSimilarity(Player player, double similarityScore){
    this.bballPlayer = player;
    this.score = similarityScore;
  }

  public double getScore(){
    return this.score;
  }

  public Player getPlayer(){
    return this.bballPlayer;
  }
}
