package ch.epfl.javass.jass;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;

/**
 * 
 * @author Jean-Daniel Rouveyrol (301480)
 *
 */

//faire la copie de tout ce qui est donné dehors
public final class Score {

	private long score;

	// à n'utiliser le constructeur que dans ofPAcked - gestion des exceptions
	private Score(long packed) {
		score = packed;
	}

	/**
	 * score initiale
	 */
	public static final Score INITIAL = ofPacked(PackedScore.INITIAL);

	/**
	 * seul moyen de créer un Score depuis l'extérieur
	 * @param packed
	 * @return un Score;
	 * @throws IllegalArgumentException
	 */
	public static Score ofPacked(long packed) throws IllegalArgumentException {
		Preconditions.checkArgument(PackedScore.isValid(packed));
		return new Score(packed);
	}
	
	/**
	 * 
	 * @return le score empacté
	 */
	public long packed() {
		return score;
	}

	/**
	 * 
	 * @param TeamId t
	 * @return le nombre de plis du tour courant au format eniter (int) de l'équipe passée en paramètre
	 */
	public int turnTricks(TeamId t) {
		return PackedScore.turnTricks(score, t);
	}
	
	/**
	 * 
	 * @param TeamId t
	 * @return le nombre de points du tour courant au format entier (int) de l'équipe passée en paramètre 
	 */
	public int turnPoints(TeamId t) {
		return PackedScore.turnPoints(score, t);
	}
	
	/**
	 * 
	 * @param TeamId t
	 * @return le nombre de points total de l'équipe passée en paramètre 
	 */
	public int gamePoints(TeamId t) {
		return PackedScore.gamePoints(score, t);
	}

	public int totalPoints(TeamId t) {
		return PackedScore.totalPoints(score, t);
	}

	/**
	 * met à jour les points en fonction de l'équipe gagnante et du dernier plis
	 * @param TeamId winningTeam
	 * @param int trickPoints
	 * @return un nouveau Score
	 * @throws IllegalArgumentException
	 */
	public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) throws IllegalArgumentException {
		if (trickPoints < 0)
			throw new IllegalArgumentException();
		return ofPacked(PackedScore.withAdditionalTrick(score, winningTeam, trickPoints));
	}

	/**
	 * met à jour les points de la prtie avec les points du dernier tour et remets à 0 le nombre de plis et les points du tour
	 * courant de chaque équipe
	 * @return un nouveau Score
	 */
	public Score nextTurn() {
		return ofPacked(PackedScore.nextTurn(score));
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() == this.getClass()) {
			return false;
		}
		if(this.score == ((Score) o).score) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		Long s = (Long) score;
		return s.hashCode();
	}

	@Override
	public String toString() {
		return PackedScore.toString(score);
	}

}
