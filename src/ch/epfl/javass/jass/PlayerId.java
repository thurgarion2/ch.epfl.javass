package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * contient les joueurs dans une énumération
 * 
 * @author Jean.Daniel Rouveyrol (301480)
 *
 */

public enum PlayerId {
	PLAYER_1(), PLAYER_2(), PLAYER_3(), PLAYER_4();
	
	/**
	 * liste de chaque joueur membre de l'énumeration - constante
	 */
	public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
	
	/**
	 * compte du nombre de joueur(s)
	 */
	public static final int COUNT = ALL.size();

    /**
     * 
     * @return le nom de l'équipe à laquelle appartient le joueur sur lequel la  méthode est appelée
     */
	public TeamId team() {
		if (this.equals(PLAYER_1) || this.equals(PLAYER_3))
			return TeamId.TEAM_1;
		return TeamId.TEAM_2;
	}
}
