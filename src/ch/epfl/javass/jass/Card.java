package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * represante n'importe qu'elle carte du jeu du jass
 * 
 * @author erwan serandour (296100)
 *
 */

public final class Card {
    private int card;

    private Card(int packedCard) {
        card = packedCard;
    };

    /**
     * enumeration des couleurs
     * 
     * @author erwan serandour (296100)
     *
     */
    public enum Color {
        /**
         * @attr SPADE pique
         *
         */
        SPADE("\u2660"),
        /**
         * @attr HEART coeur
         *
         */
        HEART("\u2661"),
        /**
         * @attr DIAMOND carreau
         *
         */
        DIAMOND("\u2662"),
        /**
         * @attr CLUB trèfle
         *
         */
        CLUB("\u2663");

        private String symbol;

        private Color(String symbol) {
            this.symbol = symbol;
        }

        /**
         * @attr ALL liste contenant toutes les couleurs
         *
         */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        /**
         * @attr COUNT nombre de couleurs
         *
         */
        public static final int COUNT = ALL.size();

        @Override
        public String toString() {
            return this.symbol;
        }
    }

    /**
     * enumaration des rangs
     * 
     * @author erwan serandour (296100)
     *
     */
    public enum Rank {
        /**
         * @attr SIX six
         */
        SIX("6", 0),
        /**
         * @attr SEVEN sept
         */
        SEVEN("7", 1),
        /**
         * @attr EIGHT huit
         */
        EIGHT("8", 2),
        /**
         * @attr NINE neuf
         */
        NINE("9", 7),
        /**
         * @attr TEN dix
         */
        TEN("10", 3),
        /**
         * @attr JACK valet
         */
        JACK("J", 8),
        /**
         * @attr QUEEN dame
         */
        QUEEN("Q", 4),
        /**
         * @attr KING roi
         */
        KING("K", 5),
        /**
         * @attr ACE ace
         */
        ACE("A", 6);

        /**
         * @attr ALL liste contenant tous les rangs
         *
         */
        public final static List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        /**
         * @attr COUNT nombre de rang
         *
         */
        public final static int COUNT = ALL.size();

        private String symbol;
        private int trumpOrdinal;
       

        private Rank(String s, int trumpOrdinal) {
            this.trumpOrdinal = trumpOrdinal;
            this.symbol = s;
            
        }

      
        @Override
        public String toString() {
            return symbol;
        }

        /**
         * @return retourne l'ordre dans l'atout
         */
        public int trumpOrdinal() {
            return trumpOrdinal;
        }


    }

    /**
     * retourne une carte de couleur et de rang donné
     * 
     * @param c
     *            couleur de la carte
     * @param r
     *            rang de la carte
     * @return retourne une carte de couleur et de rang donné
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * retourne la carte correspondante au int empaqueté
     * 
     * @param packed
     *            le nombre de la carte
     * @return retourne la carte correspondante au int empaqueté
     * 
     */
    public static Card ofPacked(int packed) {
        return new Card(packed);
    }

    /**
     * retourne la carte sous sa forme empaqueté
     * 
     * @return retourne la carte sous sa forme empaqueté(int)
     */
    public int packed() {
        return card;
    }

    /**
     * retourne la couleur de la carte
     * 
     * @return retourne la couleur de la carte
     */
    public Color color() {
        return PackedCard.color(card);
    }

    /**
     * retourne le rang de la carte
     * 
     * @return retourne le rang de la carte
     */

    public Rank rank() {
        return PackedCard.rank(card);
    }

    /**
     * retourne si l'autre carte est meilleur que notre carte
     * 
     * @param trump
     *            la couleur de l'atout
     * @param that
     *            l'autre carte
     * @return retourne si l'autre carte est meilleur que notre carte
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    /**
     * retourne la valeur (points) de la carte en fonction de l'atout
     * 
     * @param trump
     *            la couleur de l'atout
     * @return retourne la valeur (points) de la carte en fonction de l'atout
     */
    public int points(Color trump) {
        return PackedCard.points(trump, this.packed());
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO.getClass() != this.getClass()) {
            return false;
        }
        Card other = (Card) thatO;
        return this.card == other.card;
    }

    @Override
    public int hashCode() {
        return this.packed();
    }

    @Override
    public String toString() {
        return PackedCard.toString(this.packed());
    }

}
