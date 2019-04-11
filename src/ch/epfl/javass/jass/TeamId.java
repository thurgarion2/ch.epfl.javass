package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * définit les équipes du jeux de jass
 * 
 * @author Jean.Daniel Rouveyrol(301480)
 *
 */
public enum TeamId {

	/**
	 * l'équipe 1
	 */
	TEAM_1(),
	/**
	 * l'équipe 2
	 */
	TEAM_2();

	/**
	 * liste de chaque équipe membre de l'énumeration
	 */
	public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));

	/**
	 * nombre d'équipes
	 */
	public static final int COUNT = ALL.size();

	/**
	 * retourne l'identifiant de l'autre équipe
	 * 
	 * @return l'identifiant de l'autre équipe
	 */
	public TeamId other() {
		return this.equals(TEAM_1) ? TEAM_2 : TEAM_1;
	}
}
