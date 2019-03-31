package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * défini les joueurs du jass
 * 
 * @author Jean.Daniel Rouveyrol (301480)
 *
 */

public enum PlayerId {
    /**
     * le joueur 1
     */
    PLAYER_1(),
    /**
     * le joueur 2
     */
    PLAYER_2(),
    /**
     * le joueur 3
     */
    PLAYER_3(),
    /**
     * le joueur 4
     */
    PLAYER_4();

    /**
     * liste de chaque joueur membre de l'énumeration
     */
    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    /**
     * le nombre de joeurs
     */
    public static final int COUNT = ALL.size();

    /**
     * retourne le nom de l'équipe à la quelle appartient le joueur sur lequel
     * la méthode est appelée
     * 
     * @return le nom de l'équipe à la quelle appartient le joueur sur lequel la
     *         méthode est appelée
     */
    public TeamId team() {
        if (this.equals(PLAYER_1) || this.equals(PLAYER_3))
            return TeamId.TEAM_1;
        return TeamId.TEAM_2;
    }
}
