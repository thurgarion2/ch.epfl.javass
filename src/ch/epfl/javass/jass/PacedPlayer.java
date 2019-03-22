package ch.epfl.javass.jass;

import java.util.Map;
import ch.epfl.javass.jass.Card.Color;

/**
 * force un joueur donné dans le constructeur à jouer les cartes dans un temps minimum.
 * 
 * @author Jean-Daniel Rouveyrol(301480)
 *
 */

public final class PacedPlayer implements Player {
	
	private Player underlyingPlayer;
	//en milli seconde
	private double minTime;
	
	public PacedPlayer(Player underlyingPlayer, double minTime) {
		this.underlyingPlayer = underlyingPlayer;
		this.minTime = minTime*1000;
	}
	
	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		double initTime = System.currentTimeMillis();
		Card result = this.underlyingPlayer.cardToPlay(state, hand);
		
		double deltaTime =System.currentTimeMillis()-initTime;
		if(deltaTime < minTime) {
			try{
				Thread.sleep((long) (minTime-deltaTime));
			} catch (InterruptedException e) {}
		}
		return result;
	}
	
	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		underlyingPlayer.setPlayers(ownId, playerNames);
	}
	
	@Override
	public void setTrump(Color trump) {
		underlyingPlayer.setTrump(trump);;
	}
	
	@Override
	public void updateHand(CardSet newHand) {
		underlyingPlayer.updateHand(newHand);
	}

	@Override
	public void updateTrick(Trick newTrick) {
		underlyingPlayer.updateTrick(newTrick);
	}
	
	@Override
	public void updateScore(Score score) {
		underlyingPlayer.updateScore(score);
	}
	
	@Override
	public void setWinningTeam(TeamId winningTeam) {
		Player.super.setWinningTeam(winningTeam);
	}

}
