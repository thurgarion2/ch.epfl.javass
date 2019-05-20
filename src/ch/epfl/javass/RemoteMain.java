package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * permet à un joueur humain de jouer à distance jeux de jass
 * 
 * @author erwan serandour (296100)
 *
 */
public class RemoteMain extends Application{
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Thread player = new Thread(
                ()->{
                    RemotePlayerServer p = 
                            new RemotePlayerServer(new GraphicalPlayerAdapter());
                    p.run();
                }
        );
        System.out.println("La partie commencera à la connexion du client…");
        player.setDaemon(true);
        player.start();

    }

}
