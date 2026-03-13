package com.erdgy.nba;

import com.erdgy.nba.api.NBAApiClient;

public class Main 
{
    public static void main( String[] args )
    {
        NBAApiClient client = new NBAApiClient("f4c45d30-b9b6-4344-9816-d9fe54bc94c7");
        String json = client.getActivePlayersJson();
        System.out.println(json);

    }
}
