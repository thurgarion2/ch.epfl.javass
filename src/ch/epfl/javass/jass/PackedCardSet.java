package ch.epfl.javass.jass;

import java.util.StringJoiner;

/**
 * un ensemble de méthode permétant de gérer un ensemble de carte empaqueté dans un long 
 * 
 * @author erwan serandour (296100)
 *
 */

import ch.epfl.javass.bits.Bits64;

/**
 * plusieurs méthodes utiles pour empaqueté un ensemble de cartes dans un int
 * 
 * @author erwan serandour (296100)
 *
 */
// les 9 premiers bits représentent les 9 première cartes
// bits: 0 à 15 le pique
// bits: 16 à 31 le coeur
// bits: 32 à 47 le carreau
// bits: 48 à 61 le trèfle
public final class PackedCardSet {
    // pour chaque couleur l'ensemble des cartes de cette couleurs
    private final static long SUBSETCOLOROF[] = subsetOfColor();

    /**
     * l'ensemble de cartes vide
     */
    public static final long EMPTY = 0L;

    /**
     * l'ensemble des 36 cartes du jeu de Jass
     */
    public static final long ALL_CARDS = SUBSETCOLOROF[0] | SUBSETCOLOROF[1]
            | SUBSETCOLOROF[2] | SUBSETCOLOROF[3];

    // pour chaque carte toutes l'ensemble des cartes d'atout au dessus
    private static final long[][] TRUMPABOVE = trumpAbove();
    private static final int CARDPERCOLOR = 9;
    private static final int BITSPERCOLOR = 16;

    private static final int BEGINSPADE = 0;
    private static final int BEGINHEART = 16;
    private static final int BEGINDIAMOND = 32;
    private static final int BEGINCLUB = 48;

    private PackedCardSet() {
    }

    private static long[] subsetOfColor() {
        long[] out = { Bits64.mask(BEGINSPADE, CARDPERCOLOR),
                Bits64.mask(BEGINHEART, CARDPERCOLOR),
                Bits64.mask(BEGINDIAMOND, CARDPERCOLOR),
                Bits64.mask(BEGINCLUB, CARDPERCOLOR) };
        return out;
    }

    // génère pour chaque carte toutes l'ensemble des cartes d'atout au dessus
    private static long[][] trumpAbove() {
        long[][] trumpAbove = new long[Card.Color.COUNT][Card.Rank.COUNT];
        for (int card = 0; card < size(ALL_CARDS); card++) {
            long aboveRank = EMPTY;
            int pkCard = get(ALL_CARDS, card);
            for (int index = 0; index < size(ALL_CARDS); index++) {
                int pkCardCompare = get(ALL_CARDS, index);
                if (PackedCard.isBetter(PackedCard.color(pkCard), pkCardCompare, pkCard)) {
                    aboveRank = add(aboveRank, pkCardCompare);
                }
            }
            int color = PackedCard.color(pkCard).ordinal();
            int rank = PackedCard.rank(pkCard).ordinal();
            trumpAbove[color][rank] = aboveRank;

        }
        return trumpAbove;
    }

    /**
     * retourne vrai ssi la valeur donnée représente un ensemble de cartes
     * empaqueté valide
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @return vrai ssi la valeur donnée représente un ensemble de cartes
     *         empaqueté valide
     */
    public static boolean isValid(long pkCardSet) {
        // teste si tout les bits qui ne représente pas une carte sont à sero
        return (pkCardSet & (~ALL_CARDS)) == 0L;
    }

    /**
     * retourne l'ensemble des cartes strictement plus fortes que la carte
     * empaquetée donnée (en supposant que la carte fait partie de l'atout)
     * 
     * @param pkCard
     *            la carte empaquetée
     * @return l'ensemble des cartes strictement plus fortes que la carte
     *         empaquetée donnée (en supposant que la carte fait partie de
     *         l'atout)
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        int color = PackedCard.color(pkCard).ordinal();
        int rank = PackedCard.rank(pkCard).ordinal();
        return TRUMPABOVE[color][rank];
    }

    /**
     * retourne l'ensemble de cartes empaqueté contenant uniquement la carte
     * empaquetée donnée
     * 
     * @param pkCard
     *            la carte empaquetée
     * @return l'ensemble de cartes empaqueté contenant uniquement la carte
     *         empaquetée donnée
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return add(EMPTY, pkCard);
    }

    /**
     * retourne vrai ssi l'ensemble de cartes empaqueté donné est vide
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @return vrai ssi l'ensemble de cartes empaqueté donné est vide
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet == EMPTY;
    }

    /**
     * retourne la taille de l'ensemble de cartes empaqueté donné
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @return la taille de l'ensemble de cartes empaqueté donné (le nombre de
     *         carte qu'il contient)
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * retourne la version empaquetée de la carte d'index donné de l'ensemble de
     * cartes empaqueté donné (comme si c'était un set normale)
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @param index
     *            l'index de la carte dans le set (0 pour la première carte)
     * @return la version empaquetée de la carte d'index donné de l'ensemble de
     *         cartes empaqueté donné
     */
    public static int get(long pkCardSet, int index) {
        assert isValid(pkCardSet);
        assert index >= 0 && index < size(pkCardSet);
        // nettoye le long de toutes les cartes précédant la carte d'index
        for (int i = 0; i < index; i++) {
            pkCardSet ^= Long.lowestOneBit(pkCardSet);
        }
        // la couleur dépant du nombre de zéro (0-15 pique..)
        // le rang dépant du nombre de zéro après le début de la couleur
        int numberOfZero = Long.numberOfTrailingZeros(pkCardSet);
        int color = numberOfZero / BITSPERCOLOR;
        int rank = numberOfZero % BITSPERCOLOR;

        return PackedCard.pack(Card.Color.ALL.get(color),
                Card.Rank.ALL.get(rank));

    }

    // retourne l'index d'une carte dans le l'ensemble de carte
    private static int cardIndex(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return PackedCard.color(pkCard).ordinal() * BITSPERCOLOR
                + PackedCard.rank(pkCard).ordinal();
    }

    /**
     * retourne l'ensemble de cartes empaqueté donné auquel la carte empaquetée
     * donnée a été ajoutée
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @param pkCard
     *            la carte empaquetée à ajouter
     * @return l'ensemble de cartes empaqueté donné auquel la carte empaquetée
     *         donnée a été ajoutée
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return pkCardSet | (1L << cardIndex(pkCard));
    }

    /**
     * retourne l'ensemble de cartes empaqueté donné duquel la carte empaquetée
     * donnée a été supprimée
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @param pkCard
     *            la carte empaquetée à supprimer
     * @return l'ensemble de cartes empaqueté donné duquel la carte empaquetée
     *         donnée a été supprimée
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return pkCardSet & (~(1L << cardIndex(pkCard)));
    }

    /**
     * retourne vrai ssi l'ensemble de cartes empaqueté donné contient la carte
     * empaquetée donnée
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @param pkCard
     *            la carte empaquetée
     * @return vrai ssi l'ensemble de cartes empaqueté donné contient la carte
     *         empaquetée donnée
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return (pkCardSet & (1L << cardIndex(pkCard))) != EMPTY;
    }

    /**
     * retourne le complément de l'ensemble de cartes empaqueté donné
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @return le complément de l'ensemble de cartes empaqueté donné
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);
        return (~pkCardSet) & ALL_CARDS;
    }

    /**
     * retourne l'union des deux ensembles de cartes empaquetés donnés
     * 
     * @param pkCardSet1
     *            un ensemble de cartes empaquetées
     * @param pkCardSet2
     *            un ensemble de cartes empaquetées
     * @return retourne l'union des deux ensembles de cartes empaquetés donnés
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * retourne l'intersection des deux ensembles de cartes empaquetés donnés
     * 
     * @param pkCardSet1
     *            un ensemble de cartes empaquetées
     * @param pkCardSet2
     *            un ensemble de cartes empaquetées
     * @return retourne l'intersection des deux ensembles de cartes empaquetés
     *         donnés
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * retourne la différence entre le premier ensemble de cartes empaqueté
     * donné et le second
     * 
     * @param pkCardSet1
     *            le premier ensemble de cartes empaqueté
     * @param pkCardSet2
     *            le second ensemble de cartes empaqueté
     * @return la différence entre le premier ensemble de cartes empaqueté donné
     *         et le second
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return pkCardSet1 & ~(pkCardSet2 & pkCardSet1);
    }

    /**
     * retourne le sous-ensemble de l'ensemble de cartes empaqueté donné
     * constitué uniquement des cartes de la couleur donnée
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @param color
     *            la couleur
     * @return le sous-ensemble de l'ensemble de cartes empaqueté donné
     *         constitué uniquement des cartes de la couleur donnée
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);
        return pkCardSet & SUBSETCOLOROF[color.ordinal()];
    }

    /**
     * retourne la représentation textuelle de l'ensemble de cartes empaqueté
     * donné
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @return la représentation textuelle de l'ensemble de cartes empaqueté
     *         donné
     */
    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);
        StringJoiner out = new StringJoiner(",", "{", "}");
        int size = size(pkCardSet);
        for (int i = 0; i < size; i++) {
            out.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return out.toString();
    }

}
