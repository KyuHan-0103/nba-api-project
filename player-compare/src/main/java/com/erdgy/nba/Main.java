package com.erdgy.nba;

//Spring Boot imports
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main 
{

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

/*
    public static void main( String[] args )
    {
        NBAApiClient client = new NBAApiClient();
        List<Player> players = client.getAllPlayers();

        Scanner scan = new Scanner(System.in);

        //Ask User for target player
        System.out.println("Enter name of target player");
        String target = scan.nextLine();

        Player tPlayer = PlayerService.findPlayerByName(players, target);
        if(tPlayer == null){
            System.out.println("Target player not found");
            scan.close();
            return;
        }
        
        System.out.println("Target Player: " + tPlayer.getFullName() + " found");

        //Ask User for weights
        System.out.println("Would you like to use preset weights? (Yes/No)");
        String yesNo = scan.nextLine();

        if(yesNo.toLowerCase().equals("no")){
            Map<String, Double> weights = new HashMap<>();

            System.out.println("Enter number of stats to be considered (integer): ");
            int numberOfStats = scan.nextInt();
            scan.nextLine();            
            for(int i = 0; i < numberOfStats; i++){

                if(i == 0){
                    System.out.println("Please choose from this list of stats:");
                    System.out.println(tPlayer.getStatList());
                }

                System.out.println("Enter stat name");
                String stat = scan.nextLine();

                if(!tPlayer.getStatList().contains(stat)){
                    System.out.println("Could not find requested statistic.");
                    scan.close();
                    return;
                }
                
                System.out.println("Enter weight for " + stat + " : (in double form)");
                Double weight = scan.nextDouble();
                scan.nextLine();
                weights.put(stat ,weight);
            }

            SimilarityService pSService = new SimilarityService(weights);

            List<PlayerSimilarity> closestPlayers = pSService.findSimilar(tPlayer, players);
            for(int i = 0; i < 5; i++){
                System.out.println((i + 1) + ". " + closestPlayers.get(i).getPlayer().getFullName());
            }

        } else if (yesNo.toLowerCase().equals("yes")){
            SimilarityService presetService = new SimilarityService();
            List<PlayerSimilarity> presetClosestPlayers = presetService.findSimilar(tPlayer, players);

            //Print out 5 closest players
            for(int i = 0; i < 5; i++){
                System.out.println((i + 1) + ". " + presetClosestPlayers.get(i).getPlayer().getFullName());
            }
        } else {
            System.out.println("Unrecgonized Response");
            scan.close();
            return;
        }
        scan.close();
    }

    */
