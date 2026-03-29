package com.erdgy.nba.service;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.erdgy.nba.model.Player;
import com.erdgy.nba.service.PlayerSimilarity;
import java.util.Collections;

public class SimilarityService {
  private Map<String, Double> weights;

    public SimilarityService(Map<String, Double> weights) {
        this.weights = weights;
    }

    // Takes a target player and a list of all players
    // Returns a sorted list of PlayerSimilarity objects (closest first)
    public List<PlayerSimilarity> findSimilar(Player target, List<Player> allPlayers) {
      List<PlayerSimilarity> players = new ArrayList<>();

        for(Player baller : allPlayers){
          if(baller.getId().equals(target.getId())) continue;
          PlayerSimilarity player = new PlayerSimilarity(baller, calculateScore(target, baller));
          players.add(player);
        }

        sortForLowestScore(players);

        return players;
    }

    // Calculates a single similarity score between two players
    // Lower score = more similar
    private double calculateScore(Player target, Player candidate) {
      double score = 0;
      for(String key : weights.keySet()){
        score += Math.abs(target.getStat(key) - candidate.getStat(key)) * weights.get(key);
      }

      return score;
    }

    //Sort the list with players with the lowest similarity score coming first
    private void sortForLowestScore(List<PlayerSimilarity> similarPlayers){
      similarPlayers.sort(Comparator.comparing(PlayerSimilarity::getScore));
    }
}
