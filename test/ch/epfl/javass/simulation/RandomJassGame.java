package ch.epfl.javass.simulation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.net.RemotePlayerClient;
/**
 * plusieurs méthodes utiles pour empaqueté un pli dans un int
 * 
 * @author erwan serandour (296100)
 *
 */
public final class RandomJassGame {
    
    public static void main(String[] args) throws Exception  {
      Map<PlayerId, Player> players = new HashMap<>();
      Map<PlayerId, String> playerNames = new HashMap<>();
      for (PlayerId pId: PlayerId.ALL) {
        Player player=new RandomPlayer(2019);
        players.put(pId, player);
        playerNames.put(pId, pId.name());
      }
      
      try(RemotePlayerClient player= new RemotePlayerClient("localhost")){
          players.put(PlayerId.PLAYER_1, new PrintingPlayer(player));
          JassGame g = new JassGame(2019, players, playerNames);
          while (! g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
          }
          
      }catch(IOException e){
          throw new UncheckedIOException(e);
      }
      
  }
    
}