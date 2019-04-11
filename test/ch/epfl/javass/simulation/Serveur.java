package ch.epfl.javass.simulation;

import ch.epfl.javass.net.RemotePlayerServer;

public class Serveur {
    
    public static void main(String[] args) {
        RemotePlayerServer player=new RemotePlayerServer(new RandomPlayer(2019));
        player.run();
    }

}
