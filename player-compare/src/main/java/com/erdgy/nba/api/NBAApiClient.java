package com.erdgy.nba.api;

import java.io.IOException;
//import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erdgy.nba.model.Player;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List; 


public class NBAApiClient {
  
  private final HttpClient client;

  public NBAApiClient(){

    this.client = HttpClient.newHttpClient();

  }

  //Get the list of all active NBA players
  public List<Player> getAllPlayers(){

    List<Player> allPlayers = new ArrayList<>();

    int currentPage = 1;
    int totalPage = 1;

    while(currentPage <= totalPage){
      try{
        String json = getPlayerPageJson(currentPage);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        //Updates total number of pages
        JsonNode pagination = rootNode.get("pagination");
        totalPage = pagination.get("pages").asInt();


        JsonNode dataArray = rootNode.get("data");

        if(dataArray.isArray()){
          for(JsonNode dataNode : dataArray){
            double games = dataNode.get("games").asInt();
            double points = dataNode.get("points").asInt()/games;
            double assists = dataNode.get("assists").asInt()/games;
            double threeAtmPerGame = dataNode.get("threeAttempts").asDouble()/games;
            allPlayers.add(new Player(
              dataNode.get("playerId").asText(),
              dataNode.get("playerName").asText(),
              points,
              assists,
              dataNode.get("threePercent").asDouble(),
              threeAtmPerGame
            ));
          }
        }
        currentPage++;
      } catch(IOException e) {
        throw new RuntimeException("Error", e);
      }
    }
    return allPlayers;
  }

  //Sends a request to the urlString
  private String fetchApiResponse(String urlString){
    try{
      HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(urlString))
      .header("Accept", "application/json")
      .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
      if(response.statusCode() != 200){
        System.out.println("Error: Could not connect to API");
        return null;
      }

      return response.body();

      /*Interrupted Exception catches if another thread interrupts our thread as we run our "send" method
        IOException catches if something goes wrong as we transfer data
        The stack trace is the full call history (it allows us to see where it went wrong)
      */
      } catch(IOException | InterruptedException e) {
        throw new RuntimeException("Failed to fetch API response", e);
      }
  }

  //Gets a page of player data
  public String getPlayerPageJson(int page) {
    return fetchApiResponse(
        "https://api.server.nbaapi.com/api/playertotals?season=2024&pageSize=100&page=" + page
    );
  }
  
  private List<Integer> extractPlayerIds(String activePlayerList){

    try {
      List<Integer> playerIds = new ArrayList<Integer>();

      ObjectMapper mapper = new ObjectMapper();
      //Deserialize JSON into Json node
      JsonNode root = mapper.readTree(activePlayerList);

      //Access the data
      JsonNode dataArray = root.get("data");

      //Checks to make sure "data" is a correct field and isn't missing from API
      if (dataArray == null || !dataArray.isArray()) {
          throw new RuntimeException("Invalid API response: missing 'data' array");
      }

      //Take every id and append them into an array to get all the ids of all the players
      for(JsonNode playerNode: dataArray){
        playerIds.add(playerNode.get("id").asInt());
      }

      return playerIds;

    } catch (IOException e) {
      throw new RuntimeException("Error");
    }
  }
}
