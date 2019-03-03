package ch.epfl.javass.jass;

import java.util.StringJoiner;

/**
 * un ensemble de méthode permétant de gérer un ensemble de carte empaqueté dans un long 
 * 
 * @author erwan serandour (296100)
 *
 */

import ch.epfl.javass.bits.Bits64;

public final class PackedCardSet {
    private static final int CARDPERCOLOR = 9;

    private static final long[] TRUMPABOVE = { 0b1_1111_1110, 0b1_1111_1100,
            0b1_1111_1000, 0b0_0010_0000, 0b1_1110_1000, 0, 0b1_1010_1000,
            0b1_0010_1000, 0b0_0010_1000 };

    private final static long SUBSETCOLOROF[] = { Bits64.mask(0, 9),
            Bits64.mask(16, 9), Bits64.mask(32, 9), Bits64.mask(48, 9) };

    /**
     * l'ensemble de cartes vide
     */
    public static final long EMPTY = 0L;

    /**
     * l'ensemble des 36 cartes du jeu de Jass
     */
    public static final long ALL_CARDS = SUBSETCOLOROF[0] | SUBSETCOLOROF[1]
            | SUBSETCOLOROF[2] | SUBSETCOLOROF[3];

    private PackedCardSet() {
    }

    /**
     * retourne vrai ssi la valeur donnée représente un ensemble de cartes
     * empaqueté valide
     * 
     * @param pkCardSet
     *            l'ensemble de carte empqueté
     * @return vrai ssi la valeur donnée représente un ensemble de cartes
     *         empaqueté valide, ( si aucun des 28 bits inutilisés ne vaut 1)
     */
    public static boolean isValide(long pkCardSet) {
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
        return TRUMPABOVE[PackedCard.rank(pkCard).ordinal()] << (color * 16);
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
        return add(0L, pkCard);
    }

    /**
     * etourne vrai ssi l'ensemble de cartes empaqueté donné est vide
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @return vrai ssi l'ensemble de cartes empaqueté donné est vide
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValide(pkCardSet);
        return pkCardSet == 0L;
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
        assert isValide(pkCardSet);
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
        assert isValide(pkCardSet);
        assert index >= 0 && index < size(pkCardSet);

        for (int i = 0; i < index; i++) {
            pkCardSet ^= Long.lowestOneBit(pkCardSet);
        }

        int numberOfZero = Long.numberOfTrailingZeros(pkCardSet);
        int color = numberOfZero / 16;
        int rank = numberOfZero - color * 16;

        return PackedCard.pack(Card.Color.ALL.get(color),
                Card.Rank.ALL.get(rank));

    }

    private static int cardIndex(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return PackedCard.color(pkCard).ordinal() * 16
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
        assert isValide(pkCardSet);
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
        assert isValide(pkCardSet);
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
        assert isValide(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return (pkCardSet & (1L << cardIndex(pkCard))) != 0;
    }

    /**
     * retourne le complément de l'ensemble de cartes empaqueté donné
     * 
     * @param pkCardSet
     *            l'ensemble des cartes empaquetées
     * @return le complément de l'ensemble de cartes empaqueté donné
     */
    public static long complement(long pkCardSet) {
        assert isValide(pkCardSet);
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
        assert isValide(pkCardSet1);
        assert isValide(pkCardSet2);
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
        assert isValide(pkCardSet1);
        assert isValide(pkCardSet2);
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
        assert isValide(pkCardSet1);
        assert isValide(pkCardSet2);
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
        assert isValide(pkCardSet);
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
        assert isValide(pkCardSet);
        StringJoiner out = new StringJoiner(",", "{", "}");
        int size = size(pkCardSet);
        for (int i = 0; i < size; i++) {
            out.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return out.toString();
    }

}
