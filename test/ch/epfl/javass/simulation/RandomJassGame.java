package ch.epfl.javass.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
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
         Player player=new RandomPlayer(2019);
         if(pId.team()==TeamId.TEAM_1)
            player = new WinningPlayer(new MctsPlayer(pId, 2019, 100),pId);
        /*if (pId == PlayerId.PLAYER_1)
        player = new PrintingPlayer(player);*/
        players.put(pId, player);
        playerNames.put(pId, pId.name());
      }
      
      List<Integer> lostSeed=new ArrayList();
      
      for(int i=2000; i<3000; i++) {
          JassGame g = new JassGame(i, players, playerNames);
          while (! g.isGameOver()) {
            g.advanceToEndOfNextTrick();
          }
          if(!((WinningPlayer)players.get(PlayerId.PLAYER_1)).hasWin()) {
             lostSeed.add(i);
          }
          System.out.println(i);
      }
      System.out.println("------------");
      for(Integer i : lostSeed)
          System.out.println(i);
      for (PlayerId pId: PlayerId.ALL) {
          Player player=new RandomPlayer(2019);
          if(pId.team()==TeamId.TEAM_1)
             player = new WinningPlayer(new MctsPlayer(pId, 2019, 10000),pId);
         /*if (pId == PlayerId.PLAYER_1)
         player = new PrintingPlayer(player);*/
         players.put(pId, player);
         playerNames.put(pId, pId.name());
       }
      List<Integer> lost= new ArrayList();
      for(Integer i : lostSeed) {
          JassGame g = new JassGame(i, players, playerNames);
          while (! g.isGameOver()) {
            g.advanceToEndOfNextTrick();
          }
          if(!((WinningPlayer)players.get(PlayerId.PLAYER_1)).hasWin()) {
              lost.add(i);
           }
          System.out.println(i);
      }
      
      System.out.println(lost.size());
      
    }
  }