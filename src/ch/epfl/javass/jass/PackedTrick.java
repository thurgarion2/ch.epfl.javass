package ch.epfl.javass.jass;



import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * plusieurs méthodes utiles pour empaqueté un pli dans un int
 * 
 * @author  erwan serandour (296100)
 *
 */
public final class PackedTrick {
    /**
     * un pli empaqueté invalide
     */
    public static int INVALID = Bits32.mask(0, 32);
    // four invalideCrads
    private static int FOURINVALIDCARD = Bits32.mask(0, 24);
    
    private PackedTrick() {
    }

    private static int cardsAt(int pkTrick, int index) {
        return Bits32.extract(pkTrick, index * 6, 6);
    }

    /**
     * retourne vrai ssi l'entier donné représente un pli empaqueté valide
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return vrai ssi l'entier donné représente un pli empaqueté valide
     */
    public static boolean isValid(int pkTrick) {
        if (index(pkTrick) > 8) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            int pkCard = cardsAt(pkTrick, i);

            if (pkCard != PackedCard.INVALID && !PackedCard.isValid(pkCard)) {
                return false;
            }
            
            if(pkCard != PackedCard.INVALID && i!=0) {
                int last=Bits32.extract(pkTrick, i*6-6, 6);
                if(last==PackedCard.INVALID) {
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
        int cardsAndIndex = Bits32.pack(FOURINVALIDCARD, 24, index, 4);
        return Bits32.pack(cardsAndIndex, 28, indexPlayer, 2, color, 2);
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
     * retourne le pli empaqueté vide suivant celui donné (INVALID si dernier
     * pli)
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return le pli empaqueté vide suivant celui donné (INVALID si dernier
     *         pli)
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        int index = index(pkTrick);
        if (isLast(pkTrick)) {
            return INVALID;
        }
        return packNewTrick(index + 1, trump(pkTrick), winningPlayer(pkTrick));
    }

    /**
     * retourne vrai ssi le pli est le dernier du tour
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return vrai ssi le pli est le dernier du tour
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == 8;
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
        return Bits32.extract(pkTrick, 0, 24) == FOURINVALIDCARD;
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
        return size(pkTrick) == 4;
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
        for (int i = 0; i < 4; i++) {
            if (cardsAt(pkTrick, i) == PackedCard.INVALID) {
                return i;
            }
        }
        return 4;
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
        int color = Bits32.extract(pkTrick, 30, 2);
        return Card.Color.ALL.get(color);
    }

    private static int indexFirstPalyer(int pkTrick) {
        return Bits32.extract(pkTrick, 28, 2);
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
        assert index < 4 && index>=0;
        return PlayerId.ALL.get((indexFirstPalyer(pkTrick) + index) % 4);
    }

    /**
     * retourne l'index du pli
     * 
     * @param pkTrick
     *            le pli empaqueté
     * @return l'index du pli
     */
    public static int index(int pkTrick) {
        return Bits32.extract(pkTrick, 24, 4);
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
        assert size(pkTrick) < 4;
        //le pli avec que des 0 à la place de la prochaine carte
        int pkTrickClear = pkTrick & ~(Bits32.mask(6 * size(pkTrick), 6));
        return pkTrickClear | (pkCard << (6 * size(pkTrick)));
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
        return PackedCard.color(card(pkTrick, 0));
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
        
        if(isEmpty(pkTrick)) {
            return pkHand;
        }
       
        Color color = baseColor(pkTrick);
        Color trump = trump(pkTrick);
        int winningCard = card(pkTrick, indexOfwinningCard(pkTrick));
        // ajoute toutes les cartes de la couleur de base
        long playableCards = PackedCardSet.subsetOfColor(pkHand, color);

        //toutes les cartes de l'atout jouable
        long above;
        if (PackedCard.color(winningCard) == trump) {
            above = PackedCardSet.intersection(
                    PackedCardSet.trumpAbove(winningCard), pkHand);
        } else {
            above = PackedCardSet.subsetOfColor(pkHand, trump);
        }

        if (PackedCardSet.isEmpty(playableCards)) {
            // toute la main moins les cartes de l'atout non jouable
            long under = PackedCardSet.complement(above);
            under = PackedCardSet.subsetOfColor(under, trump);
            playableCards=PackedCardSet.difference(pkHand, under);
            
            //test si seulement sous couper est possible
            if(PackedCardSet.isEmpty(playableCards)) {
                return pkHand;
            }
            
            return playableCards;
        }
        //teste ssi le valet peut être jouer en atout
        int jack = PackedCard.pack(trump, Rank.JACK);
        if (PackedCardSet.size(playableCards) == 1 && trump == color
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

    private static int indexOfwinningCard(int pkTrick) {
        int best = 0;
        for (int i = 1; i < size(pkTrick); i++) {
            if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, i),
                    card(pkTrick, best))) {
                best = i;
            }
        }
        return best;
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
