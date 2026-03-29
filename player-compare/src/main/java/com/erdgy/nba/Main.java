package com.erdgy.nba;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.erdgy.nba.model.Player;
import com.erdgy.nba.service.PlayerSimilarity;
import com.erdgy.nba.service.SimilarityService;
import com.erdgy.nba.api.NBAApiClient;

public class Main 
{
    public static void main( String[] args )
    {
        NBAApiClient client = new NBAApiClient();
        List<Player> players = client.getAllPlayers();
        Player tPlayer = players.get(0);

        Map<String, Double> weights = new HashMap<>();
        weights.put("pts", 3.5);
        weights.put("ast", 2.0);
        weights.put("3ptPCT", 4.0);
        weights.put("3ptATM", 4.0);

        SimilarityService pSService = new SimilarityService(weights);

        for(Player p : players){
            if(p.getFullName().equals("Stephen Curry")){
                tPlayer = p;
                break;
            }
        }
        List<PlayerSimilarity> closestPlayers = pSService.findSimilar(tPlayer, players);
        for(int i = 0; i < 5; i++){
            System.out.println((i + 1) + ". " + closestPlayers.get(i).getPlayer().getFullName());
        }

    }
}
