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
	//si methode statcic --> passer attribut score en static .. que faire ??
	public int turnTricks(TeamId t) {
		return PackedScore.turnTricks(score, t);
	}
	
	public int turnPoints(TeamId t) {
		return PackedScore.turnPoints(score, t);
	}
	
	public int gamePoints(TeamId t) {
		return PackedScore.gamePoints(score, t);
	}
	
	public int totalPoints(TeamId t) {
		return PackedScore.totalPoints(score, t);
	}
	
	
	//le type de retour me parait chelou...
	public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) throws IllegalArgumentException{
		if(trickPoints < 0) 
			throw new IllegalArgumentException();
		//esz-ce juste ?? ou qqch du type : score = .....
		return ofPacked(PackedScore.withAdditionalTrick(score, winningTeam, trickPoints));
	}
	
	//return this ??
	public Score nextTurn() {
		score = PackedScore.nextTurn(score);
		return this;
	}
	
	
	public boolean equals() {
		long s1= Bits64.extract(score, 13 ,11);
		long s2 = Bits64.extract(score, 32+13, 11);
		if(s1==s2)
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		Long s = (Long)score;
		return s.hashCode();
	}
	
	@Override
	public String toString() {
		return PackedScore.toString(score);
	}

}
