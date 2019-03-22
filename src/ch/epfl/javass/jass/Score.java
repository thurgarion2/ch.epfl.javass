package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;

/**
 * 
 * @author Jean-Daniel Rouveyrol (301480)
 *
 */


public final class Score {

    private long score;

    
    private Score(long packed) {
        score = packed;
    }

    /**
     * score initial
     */
    public static final Score INITIAL = ofPacked(PackedScore.INITIAL);

    /**
     * créer un Score à partir d'une version empaquetée
     * 
     * @param packed - score empaqueté
     * 
     * @return Score
     * 
     * @throws IllegalArgumentException si la version empauetée n'est pas valide
     */
    public static Score ofPacked(long packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    /**
  	 *
     * @return le score sous forme empaquetée
     */
    public long packed() {
        return score;
    }

    /**
     * @param TeamId t - identifiant d'une équipe
     * 
     * @return le nombre de plis du tour courant de l'équipe passée en argument
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(score, t);
    }

    /**
     * 
     * @param TeamId t - identifiant d'une équipe
     * 
     * @return le nombre de points du tour courant de l'équipe passée en argument
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(score, t);
    }

    /**
     * @param TeamId t - identifiant d'une équipe
     * 
     * @return le nombre de points total de l'équipe passée en argument
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(score, t);
    }

    /**
     * @param TeamId t - identifiant d'une équipe
     * 
     * @return le nombre de points total de l'équipe passée en argument
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(score, t);
    }

    /**
     * met à jour les points en fonction de l'équipe gagnante et du dernier plis
     * 
     * @param TeamId winningTeam - équipe ayant gagné le pli
     * 
     * @param int trickPoints - nobmre de point du pli
     * 
     * @return un nouveau Score
     * 
     * @throws IllegalArgumentException si le nombre de point du pli est invalide, c-t-d. qu'il est strictement plus petit que 0
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints)
            throws IllegalArgumentException {
        if (trickPoints < 0) {
            throw new IllegalArgumentException();
        }
        return ofPacked(PackedScore.withAdditionalTrick(score, winningTeam,
                trickPoints));
    }

    /**
     * met à jour les points de la prtie avec les points du dernier tour et
     * remets à 0 le nombre de plis et les points du tour courant de chaque
     * équipe
     * 
     * @return un nouveau Score
     */
    public Score nextTurn() {
        return ofPacked(PackedScore.nextTurn(score));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o.getClass() != this.getClass()) {
            return false;
        }

        return this.score == ((Score) o).score;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(score);
    }

    @Override
    public String toString() {
        return PackedScore.toString(score);
    }

}
