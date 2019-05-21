package ch.epfl.javass.jass;

/**
 * contient quelques constantes du jeux de jass
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
     * nombre de pli par tour
     *
     */
    public static final int TRICKS_PER_TURN = 9;
    /**
     * nombre de points nécessaire pour gagner
     *
     */
    public static final int WINNING_POINTS = 150;
    /**
     * nombre de points en plus lors d'un match
     *
     */
    public static final int MATCH_ADDITIONAL_POINTS = 100;
    /**
     * nombre de point additionnel pour la dernière plie
     *
     */
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
