package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * identifie les équipes à l'aide d'une énumaration
 * 
 * @author Jean-Daniel Rouveyrol(301480)
 *
 */
public enum TeamId {
	TEAM_1(), TEAM_2();

	public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));

	public static final int COUNT = ALL.size();

	/**
	 * @return l'identifiant de l'autre équipe
	 */
	public TeamId other() {
		if (this.equals(TEAM_1))
			return TEAM_2;
		return TEAM_1;
	}
}
