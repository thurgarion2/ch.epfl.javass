package ch.epfl.javass.jass;

public enum TeamId {
	TEAM_1(),
	TEAM_2();
	
	public static final  List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));

	public static final int COUNT = ALL.size();
}
