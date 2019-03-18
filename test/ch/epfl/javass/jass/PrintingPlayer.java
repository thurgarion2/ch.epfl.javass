package ch.epfl.javass.jass;

import java.util.Map;

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
      this.underlyingPlayer.setPlayers(ownId, playerNames);
  }
  
  @Override
  public void  updateHand(CardSet newHand) {
      this.underlyingPlayer.updateHand(newHand);
  }
  @Override
  public void setTrump(Color trump) {
      this.underlyingPlayer.setTrump(trump);
  }
  @Override
  public void updateTrick(Trick newTrick) {
      this.underlyingPlayer.updateTrick(newTrick);
  }
  @Override
  public void updateScore(Score score) {
      this.underlyingPlayer.updateScore(score);
  }
  @Override
  public void setWinningTeam(TeamId winningTeam) {
      this.underlyingPlayer.setWinningTeam(winningTeam);
  }
}