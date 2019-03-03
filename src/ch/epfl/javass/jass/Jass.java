package ch.epfl.javass.jass;

/**
 * interface Jass
 * 
 * @author erwan serandour (296100)
 *
 */
public interface Jass {
    /**
     * taille d'une main
     *
     */
    public static final int HAND_SIZE = 9;
    /**
     * pli par tour
     *
     */
    public static final int TRICKS_PER_TURN = 9;
    /**
     * nombre de points nécessaire pour gagner
     *
     */
    public static final int WINNING_POINTS = 1000;
    /**
     * nombre de points en plus lors d'un match
     *
     */
    public static final int MATCH_ADDITIONAL_POINTS = 100;
    /**
     * point de la dernière plie
     *
     */
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
