package ch.epfl.javass.jass;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import ch.epfl.javass.Preconditions;

/**
 * 
 * @author Jean-Daniel Rouveyrol (301480)
 *
 */

//faire la copie de tout ce qui est donné dehors
public final class Score {
	
	private long score;
	
	//à n'utiliser le constructeur que dans ofPAcked - gestion des exceptions
	private Score(long packed) {
		score = packed;
	}
	
	public static final Score INTIAL = ofPacked(PackedScore.INITIAL);
	
	public static Score ofPacked(long packed) throws IllegalArgumentException{
		Preconditions.checkArgument(PackedScore.isValid(packed));
		return new Score(packed);
	}
	
	public long packed() {
		return score;
	}
	
	public int turnTricks(TeamId t) {
		return PackedScore.turnTricks(score, t);
	}
	
	public

}
