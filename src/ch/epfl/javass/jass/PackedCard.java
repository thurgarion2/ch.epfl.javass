package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * des méthodes utiles pour empaqueté une carte dans un int
 * 
 * @author Jean.Daniel Rouveyrol(301480)
 */

// représentation d'une carte empaquetée :
// les bits de 0 à 3 le rang
// de 4 à 5 la couleur
// le reste que des 0

public final class PackedCard {

	private PackedCard() {
	}

	/**
	 * une carte non valide
	 */
	public static final int INVALID = 0b111111;

	// tableau contenant les points la cartes (atout ou non)
	private static final int TRUMP_POINTS[] = { 0, 0, 0, 14, 10, 20, 3, 4, 11 };
	private static final int NORMAL_POINTS[] = { 0, 0, 0, 0, 10, 2, 3, 4, 11 };

	// quelques constantes
	private static final int START_OF_RANK = 0;
	private static final int NOMBRE_BITS_FOR_RANK = 4;

	private static final int START_OF_COLOUR = 4;
	private static final int NOMBRE_BITS_FOR_COLOUR = 2;

	/**
	 * vérifie que l'entier représentant la carte est correctement formé
	 * 
	 * @param pkCard la carte empaquetée
	 * 
	 * @return vrai, si le format entier de la carte est valide faux, si le format
	 *         entier de la carte est invalide
	 */
	public static boolean isValid(int pkCard) {
		// tout le reste des bits est à 0
		if (Bits32.extract(pkCard, 6, Integer.SIZE - 6) != 0) {
			return false;
		}
		int rank = Bits32.extract(pkCard, START_OF_RANK, NOMBRE_BITS_FOR_RANK);
		return !(rank < 0 || rank > 8);
	}

	/**
	 * empaquete une carte dans le format d'entier en partant du rang et de la
	 * couleur de la carte
	 * 
	 * @param r rang de la carte à empacter
	 * 
	 * @param c couleur de la carte à empacter
	 * 
	 * @return la carte carte empaqueteé
	 */
	public static int pack(Card.Color c, Card.Rank r) {
		int indexRank = r.ordinal();
		int indexColor = c.ordinal();
		int pkCard = Bits32.pack(indexRank, NOMBRE_BITS_FOR_RANK, indexColor, NOMBRE_BITS_FOR_COLOUR);
		assert isValid(pkCard);
		return pkCard;
	}

	// extrait de la forme empactée de la carte, l'entier représentant la
	// couleur de la carte
	private static int colorIndex(int pkCard) {
		assert isValid(pkCard);
		return Bits32.extract(pkCard, START_OF_COLOUR, NOMBRE_BITS_FOR_COLOUR);
	}

	/**
	 * donne la couleur d'une carte empaquetée
	 * 
	 * @param pkCard carte empaquetée
	 * 
	 * @return couleur de la carte
	 */
	public static Card.Color color(int pkCard) {
		assert isValid(pkCard);
		return Card.Color.ALL.get(colorIndex(pkCard));
	}

	// extrait, de la forme empactée de la carte, l'entier représentant le rang
	// de la carte
	private static int rankIndex(int pkCard) {
		assert isValid(pkCard);
		return Bits32.extract(pkCard, START_OF_RANK, NOMBRE_BITS_FOR_RANK);
	}

	/**
	 * donne le rang d'unu carte empaquetée
	 * 
	 * @param pkCard carte empaquetée
	 * 
	 * @return rang de la carte
	 */
	public static Card.Rank rank(int pkCard) {
		assert isValid(pkCard);
		return Card.Rank.ALL.get(rankIndex(pkCard));
	}

	// même chose que rankIndex(), en consideérant l'odre des rangs de la
	// couleur d'atout
	private static int rankIndexWithTrump(int pkCard, int trump) {
		assert isValid(pkCard);
		
		if (colorIndex(pkCard) == trump) {
			return Card.Rank.ALL.get(rankIndex(pkCard))
								.trumpOrdinal();
		} 
		
		return rankIndex(pkCard);
		
	}

	/**
	 * regarde si la première carte est meilleure que la seconde (format empaqueté
	 * pour les deux cartes)
	 * 
	 * @param trump   couleur de l'atout
	 * 
	 * @param pkCardL carte empaquetée comparée à l'autre
	 * 
	 * @param pkCardR carte empaquetée à laquelle est comparée la première
	 * 
	 * @return vrai si pkCradL est meilleure que pkCardR, faux sinon (y compris si
	 *         les cartes ne sont pas "comaprables directement")
	 */
	public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
		assert isValid(pkCardL);
		assert isValid(pkCardR);

		int indexColorL = colorIndex(pkCardL);
		int indexColorR = colorIndex(pkCardR);

		// rang en fonction de l'atout
		int indexRankL = rankIndexWithTrump(pkCardL, trump.ordinal());
		int indexRankR = rankIndexWithTrump(pkCardR, trump.ordinal());

		// même couleurs
		if (indexColorL == indexColorR) {
			return indexRankL > indexRankR;
		}

		// vérifie si le premier est l'atout autrement pas comparable
		return indexColorL == trump.ordinal();
		
	}

	/**
	 * retourne la valeur en points d'une carte
	 * 
	 * @param trump  couleur de l'atout
	 * 
	 * @param pkCard carte empaquetée
	 * 
	 * @return les points de la carte passée en argument
	 */
	public static int points(Card.Color trump, int pkCard) {
		assert isValid(pkCard);

		int indexColor = colorIndex(pkCard);
		int indexRank = rank(pkCard).ordinal();
	
		return indexColor == trump.ordinal() ? TRUMP_POINTS[indexRank] : NORMAL_POINTS[indexRank];
	}

	/**
	 * retourne une carte sous sa forme textuelle
	 * 
	 * @param pkCard carte empaquetée
	 * 
	 * @return le symbole de la couleur de la carte ainsi que son rang
	 */
	public static String toString(int pkCard) {
		int indexColor = Bits32.extract(pkCard, NOMBRE_BITS_FOR_RANK, 2);
		int indexRank = Bits32.extract(pkCard, 0, NOMBRE_BITS_FOR_RANK);
		return Card.Color.ALL.get(indexColor).toString() + Card.Rank.ALL.get(indexRank).toString();
	}

}
