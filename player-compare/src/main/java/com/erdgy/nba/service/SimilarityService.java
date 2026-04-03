  package com.erdgy.nba.service;
  import java.util.Comparator;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Map;
  import com.erdgy.nba.model.Player;
  import com.erdgy.nba.service.PlayerSimilarity;
  import java.util.Collections;
  import java.util.HashMap;

  public class SimilarityService {

    private Map<String, Double> weights;
    private boolean usePositionWeights;

      //Use user inputted weights
      public SimilarityService(Map<String, Double> weights) {
          this.weights = weights;
          this.usePositionWeights = false;
      }

      //Use preset position based weights
      public SimilarityService(){
        this.usePositionWeights = true;
      }

      private Map<String, Double> getWeightsForPosition(String pos){
        Map<String, Double> weight = new HashMap<String, Double>();

        if(pos.equals("PG")){
          weight.put("pts", 5.0);
          weight.put("ast", 6.0);
          weight.put("3ptPCT", 3.5);
          weight.put("3ptATM", 3.5);
          weight.put("reb", 1.0);
          weight.put("stl", 2.0);
          weight.put("blk", 0.8);
          weight.put("fta", 1.5);
          weight.put("ftPCT", 2.0);
          weight.put("fga", 3.0);
          weight.put("fgPCT", 3.0);
          weight.put("to", 4.5);
          weight.put("min", 5.0);
          weight.put("pf", 1.0);
          weight.put("efgPCT", 5.0);

        } else if (pos.equals("SG")){
          weight.put("pts", 6.0);
          weight.put("ast", 4.0);
          weight.put("3ptPCT", 4.5);
          weight.put("3ptATM", 4.5);
          weight.put("reb", 2.0);
          weight.put("stl", 2.0);
          weight.put("blk", 1.2);
          weight.put("fta", 2.0);
          weight.put("ftPCT", 2.5);
          weight.put("fga", 3.5);
          weight.put("fgPCT", 3.5);
          weight.put("to", 2.0);
          weight.put("min", 5.0);
          weight.put("pf", 1.0);
          weight.put("efgPCT", 5.5);
        } else if (pos.equals("SF")){
          weight.put("pts", 4.0);
          weight.put("ast", 2.0);
          weight.put("3ptPCT", 3.0);
          weight.put("3ptATM", 3.0);
          weight.put("reb", 3.0);
          weight.put("stl", 3.0);
          weight.put("blk", 3.0);
          weight.put("fta", 1.5);
          weight.put("ftPCT", 2.0);
          weight.put("fga", 3.0);
          weight.put("fgPCT", 3.0);
          weight.put("to", 2.0);
          weight.put("min", 5.0);
          weight.put("pf", 1.0);
          weight.put("efgPCT", 4.0);
        } else if (pos.equals("PF")){
          weight.put("pts", 4.0);
          weight.put("ast", 1.5);
          weight.put("3ptPCT", 2.2);
          weight.put("3ptATM", 2.2);
          weight.put("reb", 4.0);
          weight.put("stl", 2.0);
          weight.put("blk", 3.5);
          weight.put("fta", 2.5);
          weight.put("ftPCT", 2.5);
          weight.put("fga", 3.0);
          weight.put("fgPCT", 3.5);
          weight.put("to", 2.0);
          weight.put("min", 5.0);
          weight.put("pf", 1.0);
          weight.put("efgPCT", 5.0);
        } else if (pos.equals("C")){
          weight.put("pts", 4.0);
          weight.put("ast", 1.0);
          weight.put("3ptPCT", 2.0);
          weight.put("3ptATM", 2.0);
          weight.put("reb", 4.5);
          weight.put("stl", 1.5);
          weight.put("blk", 4.0);
          weight.put("fta", 2.5);
          weight.put("ftPCT", 3.0);
          weight.put("fga", 3.0);
          weight.put("fgPCT", 4.0);
          weight.put("to", 2.0);
          weight.put("min", 5.0);
          weight.put("pf", 1.0);
          weight.put("efgPCT", 5.0);
        }

        return weight;
      }

      // Takes a target player and a list of all players
      // Returns a sorted list of PlayerSimilarity objects (closest first)
      public List<PlayerSimilarity> findSimilar(Player target, List<Player> allPlayers) {
        List<PlayerSimilarity> players = new ArrayList<>();

        Map<String, Double> activeWeights = usePositionWeights ? getWeightsForPosition(target.getPosition()) : this.weights;
          for(Player baller : allPlayers){
            if(baller.getId().equals(target.getId())) continue;
            PlayerSimilarity player = new PlayerSimilarity(baller, calculateScore(target, baller, activeWeights));
            players.add(player);
          }

          sortForLowestScore(players);

          return players;
      }

      // Calculates a single similarity score between two players
      // Lower score = more similar
      private double calculateScore(Player target, Player candidate, Map<String, Double> activeWeights) {
        double score = 0;
        for(String key : activeWeights.keySet()){
          score += Math.abs(target.getStat(key) - candidate.getStat(key)) * activeWeights.get(key);
        }

        return score;
      }

      //Sort the list with players with the lowest similarity score coming first
      private void sortForLowestScore(List<PlayerSimilarity> similarPlayers){
        similarPlayers.sort(Comparator.comparing(PlayerSimilarity::getScore));
      }
  }
