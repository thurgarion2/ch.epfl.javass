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
    

    private static final int DEFAULT_ITERATIONS = 10000;
    private static final long WAITING_TIME_END_ROUD = 1000;
    private static final long WAITING_TO_PLAY = 2;
    
    private static final int PLAYERS_TYPES = 0;
    private static final int PLAYERS_NAME = 1;
    private static final int ADDITIONAL_ARGS = 2;
    
    private static final String HUMAN="h", DISTANT="r", SIMULATED="s";
    
    private Map<PlayerId, Player> players=new HashMap<>();
    private Map<PlayerId, String> playerNames=new HashMap<>();
    private long gameSeed;
    
    
    private static String[] resize(String[] strings, int n) {
        String[] out = Arrays.copyOf(strings, n);
        for(int i=0; i<n ; i++) {
            out[i]=out[i]==null ? "" : out[i];
        }
        return out;
    }
    
    private static void printErr(boolean b,String err) {
        if(!b) {
            System.err.println(err);
            System.exit(1); 
        }
    }
    
    private static void printErr(String err) {
        printErr(false, err);
    }
    
    private static int readInt(String number) {
        try {
            return Integer.parseInt(number);
        }catch(NumberFormatException exception) {
            printErr("Erreur : le nombre : "+number+" n'est pas int valide");  
        }
        return 0;
    }
    
    private static long readLong(String number) {
        try {
            return Long.parseLong(number);
        }catch(NumberFormatException exception) {
            printErr("Erreur : le nombre : "+number+" n'est pas Long valide");  
        }
        return 0;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> args= this.getParameters().getRaw();
        game(args);
        
        Thread gameThread = new Thread(()->{
            JassGame game = new JassGame(gameSeed, players, playerNames);
            
            while(!game.isGameOver()) {
                game.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(WAITING_TIME_END_ROUD);
                } catch (InterruptedException e) {
                    throw new Error(e.toString());
                }
            } 
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }
    
    private void game(List<String> args) {
        String desc="Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>] \n"
                + "où : \n"
                + "<jn> spécifie le joueur n, ainsi: \n"
                + "   h:[<nom>  un joueur humain nommé <nom>]\n"
                + "   s:[<nom>  un joueur simulé nommé <nom>], [<graine>], [<iterations>] \n"
                + "   r:[<nom> un joueur distant nommé <nom>], <ip> l'adrresse ip du joueur <ip>\n"
                + "<jn>";
        
        printErr(args.size()==4 || args.size()==5,desc);
        Random seedGenerator= args.size()==5 ? new Random(readLong(args.get(4))) : new Random();
                
        gameSeed=seedGenerator.nextLong();
  
        for(PlayerId id : PlayerId.ALL) {
            String[] message = args.get(id.ordinal()).split(":");
            long seed = seedGenerator.nextLong();
            switch(message[PLAYERS_TYPES]) {
            case HUMAN:
                human(message, id);
                break;
            case SIMULATED:
                simulated(message, id, seed);
                break;
            case DISTANT:
                distant(message, id);
                break;
            default :
                printErr("Erreur : spécification de joueur invalide : "+message[0]);
            }
        }
    }
    
    private void human(String[] args, PlayerId id) {
        printErr(args.length==2 || args.length==1,
                  "Erreur : nombre d'arguments invalide (1 ou 2 nécessaire) : "+ Arrays.toString(args));
        args=resize(args, 2);
        String name = args[PLAYERS_NAME];
        playerNames.put(id, !name.isEmpty() ? name : DEFAULT_PLAYERS.get(id));
        players.put(id, new GraphicalPlayerAdapter());
    }
    
    private void simulated(String[] args, PlayerId id, long seed) {
        printErr(args.length<=3 && args.length>=1,
                "Erreur : nombre d'arguments invalide (1, 2 ou 3 nécessaire) : "+ Arrays.toString(args));
        args=resize(args, 3);
        
        String name = args[PLAYERS_NAME];
        String itera = args[ADDITIONAL_ARGS];
        
        playerNames.put(id, !name.isEmpty() ? name : DEFAULT_PLAYERS.get(id));
        int iterations= (!itera.isEmpty() ? readInt(itera): DEFAULT_ITERATIONS);
      
        try {
            players.put(id, new PacedPlayer(new MctsPlayer(id,seed, iterations ),WAITING_TO_PLAY));
        }catch(IllegalArgumentException exception) {
            printErr("Erreur : nombre d'iterations inferieur à 10 : "+iterations);  
        }

    }
    
    private void distant(String[] args, PlayerId id) {
        printErr(args.length==3,
                "Erreur : nombre d'arguments invalide (3 nécessaire) : "+ Arrays.toString(args));
        args=resize(args, 3);
        
        String name = args[PLAYERS_NAME];
        String ip = args[ADDITIONAL_ARGS];
        
        playerNames.put(id, !name.isEmpty() ? name : DEFAULT_PLAYERS.get(id));
        
        try{
            players.put(id ,new RemotePlayerClient(ip));
        }catch(UncheckedIOException err) {
            printErr("Erreur : adresse ip invalide : "+ip);
        }
    }

}
