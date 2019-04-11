package ch.epfl.javass.net;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * plusieurs méthodes utiles pour serialiser et déserialiser des objets de
 * ch.epfl.javass.jass sous forme de chaine de caractères
 * 
 * @author erwan serandour (296100)
 *
 */
public final class Serializer {
    /**
     * sérialise un ensemble de cartes
     * 
     * @param set
     *            l'ensemble de cartes
     * @return une chaine de caractère représentant l'ensemble de cartes
     *         serialisé
     */
    public static String serializeCardSet(CardSet set) {
        return StringSerializer.serializeLong(set.packed());
    }

    /**
     * désérialise un ensemble de cartes
     * 
     * @param s
     *            l'ensemble de cartes sérialisé
     * @return l'ensemble de cartes désérialisé
     */
    public static CardSet deserializeCardSet(String s) {
        return CardSet.ofPacked(StringSerializer.deserializeLong(s));
    }

    /**
     * sérialise un état du tour
     * 
     * @param state
     *            l'état du tour
     * @return une chaine de caractère représentant l'état du tour serialisé
     */
    public static String serializeTurnState(TurnState state) {
        String score = serializeScore(state.score());
        String cards = serializeCardSet(state.unplayedCards());
        String trick = serializeTrick(state.trick());
        return StringSerializer.combineString(',', score, cards, trick);
    }

    /**
     * désérialise un état du tour
     * 
     * @param s
     *            l'état du tour sérialisé
     * @return l'état du tour désérialisé
     */
    public static TurnState deserializeTurnState(String s) {
        String[] comp = StringSerializer.splitString(s, ',');
        long score = StringSerializer.deserializeLong(comp[0]);
        long cards = StringSerializer.deserializeLong(comp[1]);
        int trick = StringSerializer.deserializeInt(comp[2]);
        return TurnState.ofPackedComponents(score, cards, trick);
    }

    /**
     * sérialise un pli
     * 
     * @param trick
     *            le pli
     * @return une chaine de caractère représentant le pli sérialisé
     */
    public static String serializeTrick(Trick trick) {
        return StringSerializer.serializeInt(trick.packed());
    }

    /**
     * désérialise un pli
     * 
     * @param trick
     *            le pli sérialisé
     * @return le pli désérialisé
     */
    public static Trick deserializeTrick(String trick) {
        return Trick.ofPacked(StringSerializer.deserializeInt(trick));
    }

    /**
     * sérialise une carte
     * 
     * @param c
     *            la carte
     * @return une chaine de caractère représentant la carte sérialisé
     */
    public static String serializeCard(Card c) {
        return StringSerializer.serializeInt(c.packed());
    }

    /**
     * désérialise une carte
     * 
     * @param card
     *            la carte sérialisée
     * @return la carte sérialisée
     */
    public static Card deserializeCard(String card) {
        return Card.ofPacked(StringSerializer.deserializeInt(card));
    }

    /**
     * sérialise un score
     * 
     * @param s
     *            le score
     * @return une chaine de caractère représentant le score sérialisé
     */
    public static String serializeScore(Score s) {
        return StringSerializer.serializeLong(s.packed());
    }

    /**
     * désérialise un score
     * 
     * @param score
     *            le score sérialisé
     * @return le score désérialisé
     */
    public static Score deserializeScore(String score) {
        return Score.ofPacked(StringSerializer.deserializeLong(score));
    }

    public static String serializeEnum(Enum e) {
        return StringSerializer.serializeInt(e.ordinal());
    }

}
