package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;
/**
 * plusieurs méthodes utiles pour empaqueté un pli dans un int
 * 
 * @author erwan serandour (296100)
 *
 */
public final class RandomJassGame {
    public static void main(String[] args) {
      Map<PlayerId, Player> players = new HashMap<>();
      Map<PlayerId, String> playerNames = new HashMap<>();
      for (PlayerId pId: PlayerId.ALL) {
      Player player = new MctsPlayer(pId, 2019, 1000000);
        //Player player = new RandomPlayer(2019);
        if (pId == PlayerId.PLAYER_1)
      player = new PrintingPlayer(player);
        players.put(pId, player);
        playerNames.put(pId, pId.name());
      }

      JassGame g = new JassGame(2019, players, playerNames);
      while (! g.isGameOver()) {
        g.advanceToEndOfNextTrick();
        System.out.println("----");
      }
    }
  }