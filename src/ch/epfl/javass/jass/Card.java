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

    public Card(int packedCard) {
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
         * pique
         *
         */
        SPADE("\u2660"),
        /**
         * coeur
         *
         */
        HEART("\u2661"),
        /**
         * carreau
         *
         */
        DIAMOND("\u2662"),
        /**
         * trèfle
         *
         */
        CLUB("\u2663");

        private String symbol;

        private Color(String symbol) {
            this.symbol = symbol;
        }

        /**
         * ALL liste contenant toutes les couleurs
         *
         */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        /**
         * COUNT nombre de couleurs
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
         * le six
         */
        SIX("6", 0),
        /**
         * le sept
         */
        SEVEN("7", 1),
        /**
         * le huit
         */
        EIGHT("8", 2),
        /**
         * le neuf
         */
        NINE("9", 7),
        /**
         * le dix
         */
        TEN("10", 3),
        /**
         * le valet
         */
        JACK("J", 8),
        /**
         * la dame
         */
        QUEEN("Q", 4),
        /**
         * le roi
         */
        KING("K", 5),
        /**
         * l'ace
         */
        ACE("A", 6);

        /**
         * ALL liste contenant tous les rangs
         *
         */
        public final static List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        /**
         * le nombre de rang
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
        return  ofPacked(PackedCard.pack(c, r));
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
        if(!PackedCard.isValid(packed)) {
            throw new IllegalArgumentException();
        }
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
        if(thatO==null) {
            return false;
        }
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
