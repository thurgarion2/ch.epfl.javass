package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * plusieurs méthodes utiles pour empaqueté un pli dans un int
 * 
 * @author erwan serandour (296100)
 *
 */
/*
 * un pli empaquté 24 premiers bits (6 bits par carte 111111 si vide) 4 prochain
 * bits le numero du pli 2 prochain le premier joueur 2 dernier l'atout
 * 
 * de 0 à 23 les cartes du pli de 24 à 27 l'index du pli de 28 à 29 l'index du
 * premier joueur de 30 à 31 l'atout
 */
public final class PackedTrick {
    /**
     * un pli empaqueté invalide
     */
    public static final int INVALID = Bits32.mask(0, 32);
    // représente les 4 cartes vides d'un pli
    private static final int FOUR_INVALID_CARD = Bits32.mask(0, 24);
    private static final int BITS_PER_CARD = 6;
    private static final int CARD_PER_TRICK = 4;

    private static final int START_OF_CARDS = 0;
    private static final int BIT_SIZE_OF_CARDS = 24;

    private static final int START_OF_TRICK_INDEX = 24;
    private static final int BIT_SIZE_OF_TRICK_INDEX = 4;

    private static final int START_OF_PLAYER_INDEX = 28;
    private static final int BIT_SIZE_OF_PLAYER_INDEX = 2;

    private static final int START_OF_TRUMP = 30;
    private static final int BIT_SIZE_OF_TRUMP = 2;

    private PackedTrick() {
    }

    private static int cardsAt(int pkTrick, int index) {
        return Bits32.extract(pkTrick, index * BITS_PER_CARD, BITS_PER_CARD);
    }

    /**
     * retourne vrai ssi l'entier donné représente un pli empaqueté valide
     * +=
     * @param pkTrick
     *            le pli empaqueté
     * @return vrai ssi l'entier donné représente un pli empaqueté valide
     */
    public static boolean isValid(int pkTrick) {
        // index du pli plus petit que 9
        // 9 pli par tout --> de 0 à 8
        if (index(pkTrick) >= Jass.TRICKS_PER_TURN) {
            return false;
        }
        // teste si aucune carte ne se retrouve à une place invalide
        for (int i = 0; i < CARD_PER_TRICK; i++) {
            int pkCard = cardsAt(pkTrick, i);
            // emplacement soit vide soit une carte valide
            if (pkCard != PackedCard.INVALID && !PackedCard.isValid(pkCard)) {
                return false;
            }
            // teste si le précédant emplacement n'est pas vide alors que
            // l'actuel est plein
            if (pkCard != PackedCard.INVALID && i != 0) {
                int last = cardsAt(pkTrick, i - 1);
                if (last == PackedCard.INVALID) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int packNewTrick(int index, Color trump,
            PlayerId firstPlayer) {
        int color = trump.ordinal();
        int indexPlayer = firstPlayer.ordinal();
        int cardsAndIndex = Bits32.pack(FOUR_INVALID_CARD, BIT_SIZE_OF_CARDS,
                index, BIT_SIZE_OF_TRICK_INDEX);
        int size = BIT_SIZE_OF_CARDS + BIT_SIZE_OF_TRICK_INDEX;
        return Bits32.pack(cardsAndIndex, size,
                indexPlayer, BIT_SIZE_OF_PLAYER_INDEX, 
                color, BIT_SIZE_OF_TRUMP);
    }

    /**
     * retourne le pli empaqueté vide (sans aucune carte—d'index 0) avec l'atout
     * et le premier joueur donnés
     * 
     * @param trump
     *            l'atout
     * @param firstPlayer
     *            le premier joueur
     * @return le pli empaqueté vide (sans aucune carte—d'index 0) avec l'atout
     *         et le premier joueur donnés
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return packNewTrick(0, trump, firstPlayer);
    }

    /**
     * retourne le pli empaqueté vide suivant celui donné
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return le pli empaqueté vide suivant celui donné (INVALID si dernier pli
     *         du tour)
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        int index = index(pkTrick);
        return isLast(pkTrick) ? INVALID : 
            packNewTrick(index + 1, trump(pkTrick), winningPlayer(pkTrick));
    }

    /**
     * retourne vrai ssi c'est le dernier pli du tour
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return vrai ssi le pli est le dernier du tour
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) + 1 == Jass.TRICKS_PER_TURN;
    }

    /**
     * retourne vrai ssi le pli est vide (il n'a aucune carte)
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return vrai ssi le pli est vide (il n'a aucune carte)
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, START_OF_CARDS,BIT_SIZE_OF_CARDS) 
                == FOUR_INVALID_CARD;
    }

    /**
     * retourne vrai ssi le pli est plein
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return vrai ssi le pli est plein
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == CARD_PER_TRICK;
    }

    /**
     * retourne la taille du pli (son nombre de cartes)
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return la taille du pli (son nombre de cartes)
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);
        for (int i = 0; i < CARD_PER_TRICK; i++) {
            if (cardsAt(pkTrick, i) == PackedCard.INVALID) {
                return i;
            }
        }
        return CARD_PER_TRICK;
    }

    /**
     * retourne l'atout du pli
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return l'atout du pli
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        int color = Bits32.extract(pkTrick, 
                START_OF_TRUMP, BIT_SIZE_OF_TRUMP);
        return Card.Color.ALL.get(color);
    }

    private static int indexFirstPalyer(int pkTrick) {
        return Bits32.extract(pkTrick, 
                START_OF_PLAYER_INDEX, BIT_SIZE_OF_PLAYER_INDEX);
    }

    /**
     * retourne le joueur d'index donné dans le pli (de 0 à 3)
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @param index
     *            l'index du joueur entre 0 et 3
     * @return le joueur d'index donné dans le pli (de 0 à 3)
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert index < 4 && index >= 0;
        int next=(indexFirstPalyer(pkTrick) + index) % PlayerId.COUNT;
        return PlayerId.ALL.get(next);
    }

    /**
     * retourne l'index du pli
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return l'index du pli
     */
    public static int index(int pkTrick) {
        return Bits32.extract(pkTrick, 
                START_OF_TRICK_INDEX, BIT_SIZE_OF_TRICK_INDEX);
    }

    /**
     * retourne la version empaquetée de la carte du pli à l'index donné
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @param index
     *            l'index de la carte entre 0 et 3
     * @return la version empaquetée de la carte du pli à l'index donné
     */
    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert index < size(pkTrick);
        return cardsAt(pkTrick, index);
    }

    /**
     * retourne le pli avec la carte ajoutée
     * 
     * @param pkTrick
     *            le pli empaqueté (non plein)
     * @param pkCard
     *            la carte
     * @return le pli avec la carte ajoutée
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert PackedCard.isValid(pkCard);
        assert size(pkTrick) < CARD_PER_TRICK;
        // le pli avec que des 0 à la place de la prochaine carte
        int decalage=BITS_PER_CARD * size(pkTrick);
        int pkTrickClear = pkTrick & ~(PackedCard.INVALID<<(decalage));
        return pkTrickClear | (pkCard <<  decalage);
    }

    /**
     * retourne la couleur de base du pli
     * 
     * @param pkTrick
     *            le pli empaqueté (non vide)
     * @return la couleur de base du pli
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        assert !isEmpty(pkTrick);
        // la première carte jouée définit la couleur
        return PackedCard.color(card(pkTrick, 0));
    }
    
    private static int indexOfwinningCard(int pkTrick) {
        int best = 0;
        // incémentale teste si la prochaine carte est plus forte que la carte
        // courante
        for (int i = 1; i < size(pkTrick); i++) {
            if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, i), card(pkTrick, best))) {
                best = i;
            }
        }
        return best;
    }

    /**
     * retourne le sous-ensemble (empaqueté) des cartes de la main pkHand qui
     * peuvent être jouées comme prochaine carte du pli pkTrick
     * 
     * @param pkTrick
     *            le pli empaqueté (non plein)
     * @param pkHand
     *            la main du joueur
     * @return le sous-ensemble (empaqueté) des cartes de la main pkHand qui
     *         peuvent être jouées comme prochaine carte du pli pkTrick
     * 
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick);
        assert PackedCardSet.isValid(pkHand);

        if (isEmpty(pkTrick)) {
            return pkHand;
        }

        Color color = baseColor(pkTrick);
        Color trump = trump(pkTrick);
        int winningCard = card(pkTrick, indexOfwinningCard(pkTrick));
        int jack = PackedCard.pack(trump, Rank.JACK);
        
        // ajoute toutes les cartes de la couleur de base
        long playableCards = PackedCardSet.subsetOfColor(pkHand, color);

        // toutes les cartes de l'atout jouable
        long above= PackedCardSet.subsetOfColor(pkHand, trump);
        if (PackedCard.color(winningCard) == trump) {
            above = PackedCardSet.intersection(PackedCardSet.trumpAbove(winningCard), above);
        } 
        //le joeur n'a pas de cartes de la couleur de bases
        if (PackedCardSet.isEmpty(playableCards)) {
            // toute la main moins les cartes de l'atout non jouable
            long under = PackedCardSet.subsetOfColor(PackedCardSet.complement(above), 
                    trump);
            playableCards = PackedCardSet.difference(pkHand, under);
            
            // teste ssi le valet peut être jouer en atout
            if (PackedCardSet.size(playableCards) == 1 
                    && PackedCardSet.get(playableCards, 0) == jack) {
                return pkHand;
            }

            // test si seulement sous couper est possible
            return !PackedCardSet.isEmpty(playableCards) ? playableCards : pkHand;
        }
        // teste ssi le valet peut être jouer en atout
        if (PackedCardSet.size(playableCards) == 1 
                && PackedCardSet.get(playableCards, 0) == jack) {
            return pkHand;
        }

        return PackedCardSet.union(playableCards, above);
    }

    /**
     * retourne la valeur du pli
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return la valeur du pli
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        int points = 0;
        for (int i = 0; i < size(pkTrick); i++) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }
        points += (isLast(pkTrick)) ? Jass.LAST_TRICK_ADDITIONAL_POINTS : 0;
        return points;
    }


    /**
     * retourne l'identité du joueur menant le pli
     * 
     * @param pkTrick
     *            le pli empaqueté (non vide)
     * @return l'identité du joueur menant le pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        assert !isEmpty(pkTrick);
        return player(pkTrick, indexOfwinningCard(pkTrick));
    }

    /**
     * retourne une représentation textuelle du pli
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return une représentation textuelle du pli
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);
        long set = PackedCardSet.EMPTY;
        for (int i = 0; i < size(pkTrick); i++) {
            set = PackedCardSet.add(set, card(pkTrick, i));
        }
        String otherInfo = "Atout : " + trump(pkTrick).toString();
        otherInfo += "  Player : " + player(pkTrick, 0).toString();
        otherInfo += "  Index : " + index(pkTrick);
        return (otherInfo + " " + PackedCardSet.toString(set));
    }

}
