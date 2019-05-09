package ch.epfl.javass;

import java.io.IOError;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

public class LocalMain extends Application {
    private static final Map<PlayerId, String> DEFAULT_PLAYERS=Collections.unmodifiableMap(new HashMap() {{
        put(PlayerId.PLAYER_1, "Aline");
        put(PlayerId.PLAYER_2, "Bastien");
        put(PlayerId.PLAYER_3, "Collette");
        put(PlayerId.PLAYER_4, "Davide");
    }});
    
    
    
    private final Map<PlayerId, Player> players=new HashMap<>();
    private final Map<PlayerId, String> playerNames=new HashMap<>();
    private final Map<PlayerId, Long> playersSeed = new HashMap<>();
    private long gameSeed;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> args= this.getParameters().getRaw();
        
        if(args.size()<4 || args.size()>5) {
            System.err.println("Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>]");
            System.err.println("où :");
            System.err.println("<jn> spécifie le joueur n, ainsi:");
            for(PlayersKind descr : PlayersKind.ALL) {
                System.err.println("    "+descr.getDescription());
            }
            System.err.println("<jn>");
            System.exit(1);
        }
        Random seedGenerator=null;
        try {
            seedGenerator= args.size()==5 ? new Random(Long.parseLong(args.get(4))) : new Random();
        }catch(NumberFormatException exception) {
            System.err.println("Erreur : la graine n'est pas un long valide : "+args.get(4));
        }
        gameSeed=seedGenerator.nextLong();
        
        for(PlayerId id : PlayerId.ALL) {
            playersSeed.put(id, seedGenerator.nextLong());
        }
        
        for(PlayerId id : PlayerId.ALL) {
            String[] message = args.get(id.ordinal()).split(":");
            switch(PlayersKind.valueOf(message[0])) {
            case h:
                human(message, id);
                break;
            case s:
                simulated(message, id);
                break;
            case r:
                distant(message, id);
                break;
            }
        }
        
        
        Thread gameThread = new Thread(()->{
            JassGame game = new JassGame(gameSeed, players, playerNames);
            
            while(!game.isGameOver()) {
                game.advanceToEndOfNextTrick();
                try {
                   Thread.sleep(1000);
                } catch (InterruptedException e) {
                  
                }
            }
            
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }
    
    private String[] resize(String[] strings, int n) {
        String[] out = new String[n];
        Arrays.fill(out, "");
        for(int i=0; i<n && i<strings.length; i++) {
            out[i]=strings[i];
        }
        return out;
    }
    
    private void human(String[] args, PlayerId id) {
        if(args.length>2 || args.length<1) {
            System.err.println("Erreur : nombre d'arguments invalide (1 ou 2 nécessaire) : "+ Arrays.toString(args));
            System.exit(1);
        }
        args=resize(args, 2);
        playerNames.put(id, !args[1].isEmpty() ? args[1] : DEFAULT_PLAYERS.get(id));
        players.put(id, new GraphicalPlayerAdapter());
    }
    
    private void simulated(String[] args, PlayerId id) {
        if(args.length>3 || args.length<1) {
            System.err.println("Erreur : nombre d'arguments invalide (1, 2 ou 3 nécessaire) : "+ Arrays.toString(args));
            System.exit(1);
        }
        args=resize(args, 3);
        
        playerNames.put(id, !args[1].isEmpty() ? args[1] : DEFAULT_PLAYERS.get(id));
        long seed=0;
        int iterations=0; 
        
  
        try {
            iterations= (!args[2].isEmpty() ? Integer.parseInt(args[2]) : 10000);
        }catch(NumberFormatException exception) {
            System.err.println("Erreur : le nombre d'iterations n'est pas un int valide : "+args[2]);  
            System.exit(1);
        }
        
        try {
            players.put(id, new PacedPlayer(new MctsPlayer(id,seed, iterations ),2));
        }catch(IllegalArgumentException exception) {
            System.err.println("Erreur : nombre d'iterations inferieur à 10 : "+iterations);  
            System.exit(1);
        }
        
    }
    
    private void distant(String[] args, PlayerId id) {
        if(args.length!=3) {
            System.err.println("Erreur : nombre d'arguments invalide (3 nécessaire) : "+ Arrays.toString(args));
            System.exit(1);
        }
        args=resize(args, 3);
        playerNames.put(id, !args[1].isEmpty() ? args[1] : DEFAULT_PLAYERS.get(id));
        
        try {
            players.put(id ,new RemotePlayerClient(args[2]));
        }catch(UncheckedIOException err) {
            System.err.println("Erreur : adresse ip invalide : "+args[2]);
            System.exit(1);
        }
    }

}
