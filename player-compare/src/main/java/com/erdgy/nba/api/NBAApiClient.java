package com.erdgy.nba.api;

import java.io.IOException;
//import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

public class NBAApiClient {
  
  private final HttpClient client;
  private final String apiKey;

  public NBAApiClient(String myApiKey){

    this.client = HttpClient.newHttpClient();
    this.apiKey = myApiKey;

  }

  //Get the list of all active NBA players
  public String getActivePlayersJson(){
    return fetchApiResponse("https://api.balldontlie.io/v1/players?active=true");
  }

  //Sends a request to the urlString
  private String fetchApiResponse(String urlString){
    try{
      HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(urlString))
      .header("Authorization", apiKey)
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
