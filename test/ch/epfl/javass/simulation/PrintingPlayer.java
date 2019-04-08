package ch.epfl.javass.simulation;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {
  private final Player underlyingPlayer;

  public PrintingPlayer(Player underlyingPlayer) {
    this.underlyingPlayer = underlyingPlayer;
  }

  @Override
  public Card cardToPlay(TurnState state, CardSet hand) {
    System.out.print("C'est à moi de jouer... Je joue : ");
    Card c = underlyingPlayer.cardToPlay(state, hand);
    System.out.println(c);
    return c;
  }
  
  @Override
  public void setPlayers(PlayerId ownId,
          Map<PlayerId, String> playerNames) {
      
      System.out.println("Les joueurs sont : ");
      for(String each : playerNames.values()) {
          System.out.println(each);
      }
      System.out.println("je suis : "+playerNames.get(ownId));
      this.underlyingPlayer.setPlayers(ownId, playerNames);
  }
  
  @Override
  public void  updateHand(CardSet newHand) {
      System.out.println("Ma nouvelle main : "+newHand);
      this.underlyingPlayer.updateHand(newHand);
  }
  @Override
  public void setTrump(Color trump) {
      System.out.println("Atout : "+trump);
      this.underlyingPlayer.setTrump(trump);
  }
  @Override
  public void updateTrick(Trick newTrick) {
      System.out.println(newTrick);
      this.underlyingPlayer.updateTrick(newTrick);
  }
  @Override
  public void updateScore(Score score) {
      System.out.println("Scores: "+score);
      this.underlyingPlayer.updateScore(score);
  }
  @Override
  public void setWinningTeam(TeamId winningTeam) {
      System.out.println("L'équipe gagnante est : "+winningTeam);
      this.underlyingPlayer.setWinningTeam(winningTeam);
  }
}