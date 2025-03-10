package ch.epfl.javass.jass;

import java.util.Map;
import ch.epfl.javass.jass.Card.Color;

/**
 * force un joueur donné dans le constructeur à jouer les cartes dans un temps
 * minimum.
 * 
 * @author Jean-Daniel Rouveyrol(301480)
 *
 */

public final class PacedPlayer implements Player {

	private final Player underlyingPlayer;

	// en milli seconde
	private final double minTime;
	
	// le nombre par lequel multiplier pour convertir le temps en secondes en
	// milli secondes
	private static final int CONVERSION = 1000;

	/**
	 * @param underlyingPlayer le joeur sous-jassant
	 * 
	 * @param minTime   temps minimum que le joueur doit mettre avant de jouer (en seconde)
	 */
	public PacedPlayer(Player underlyingPlayer, double minTime) {
		this.underlyingPlayer = underlyingPlayer;
		this.minTime = minTime * CONVERSION;
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		double initTime = System.currentTimeMillis();
		Card result = this.underlyingPlayer.cardToPlay(state, hand);

		double deltaTime = System.currentTimeMillis() - initTime;
		if (deltaTime < minTime) {
			try {
				Thread.sleep((long) (minTime - deltaTime));
			} catch (InterruptedException e) {
			}
		}
		return result;
	}

	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		underlyingPlayer.setPlayers(ownId, playerNames);
	}

	@Override
	public void setTrump(Color trump) {
		underlyingPlayer.setTrump(trump);
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
		underlyingPlayer.setWinningTeam(winningTeam);
	}

}
