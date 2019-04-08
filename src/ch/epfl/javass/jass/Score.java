package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;

/**
 * 
 * représante le score d'une partie de jass
 * 
 * @author Jean.Daniel Rouveyrol (301480)
 *
 */

public final class Score {

    private final long score;

    private Score(long packed) {
        score = packed;
    }

    /**
     * score initial
     */
    public static final Score INITIAL = ofPacked(PackedScore.INITIAL);

    /**
     * créer un score à partir d'une version empaquetée
     * 
     * @param packed
     *            score empaqueté
     * 
     * @return le score généré à partir de sa version empaquetée
     * 
     * @throws IllegalArgumentException
     *             si la version empauetée n'est pas valide
     */
    public static Score ofPacked(long packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    /**
     * retourne le score sous forme empaquetée
     *
     * @return le score sous forme empaquetée
     */
    public long packed() {
        return score;
    }

    /**
     * retourne le nombre de pli gagné par l'équipe donnée (au tour courant)
     * 
     * @param t
     *            l'identifiant de l'équipe
     * 
     * @return le nombre de pli gagné par l'équipe donnée (au tour courant)
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(score, t);
    }

    /**
     * retourne le nombre de points (durant le tour) gagné par l'équipe donnée
     * 
     * @param t
     *            l'identifiant de l'équipe
     * 
     * @return le nombre de points (durant le tour) gagné par l'équipe donnée
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(score, t);
    }

    /**
     * retourne le nombre de points (durant la partie) gagné par l'équipe donnée
     * 
     * @param t
     *            l'identifiant de l'équipe
     * 
     * @return le nombre de points (durant la partie) gagné par l'équipe donnée
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(score, t);
    }

    /**
     * retourne le nombre de points gagné au totale durant la partie par
     * l'équipe donnée (ceux du tour + partie)
     * 
     * @param t
     *            l'identifiant de l'équipe
     * 
     * @return le nombre de points gagné au totale durant la partie par l'équipe
     *         donnée (ceux du tour + partie)
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(score, t);
    }

    /**
     * met à jour les points en fonction de l'équipe gagnante et du dernier plis
     * 
     * @param winningTeam
     *            équipe ayant gagné le pli
     * 
     * @param trickPoints
     *            nobmre de point du pli
     * 
     * @return le score mis à jour
     * 
     * @throws IllegalArgumentException
     *             si le nombre de point du pli est invalide, c-t-d. qu'il est
     *             strictement plus petit que 0
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
     * met à jour les points de la partie avec les points du dernier tour et
     * remets à 0 le nombre de plis et les points du tour courant de chaque
     * équipe
     * 
     * @return le score mis à jour pour le début du prochain tour
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
