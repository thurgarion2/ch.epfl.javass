package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * plusieurs méthodes utiles pour serialiser et déserialiser sous forme de
 * chaine de caractères
 * 
 * @author erwan serandour (296100)
 *
 */

public final class StringSerializer {
    private StringSerializer() {
    };

    private static Base64.Encoder ENCODER = Base64.getEncoder();
    private static Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * sérialise un int
     * 
     * @param i
     *            l'entier à sérialiser
     * @return l'entier sérialisé sous la forme de sa représentation textuelle
     *         en base 16
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, 16);
    }

    /**
     * désérialise un int
     * 
     * @param s
     *            l'entier sous la forme de sa représentation textuelle
     * @return l'entier désérialisé
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    /**
     * sérialise un int
     * 
     * @param i
     *            l'entier à sérialiser
     * @return l'entier sérialisé sous la forme de sa représentation textuelle
     *         en base 16
     */
    public static String serializeLong(Long i) {
        return Long.toUnsignedString(i, 16);
    }

    /**
     * désérialise un long
     * 
     * @param s
     *            l'entier sous la forme de sa représentation textuelle
     * @return l'entier désérialisé
     */
    public static Long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, 16);
    }

    /**
     * sérialise une chaine de caractère
     * 
     * @param s
     *            la chaine de caractère
     * @return la chaine sérialisée
     */
    public static String serializeString(String s) {
        return ENCODER.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * désérialise une chaine de caractère
     * 
     * @param s
     *            la chaine sérialisée
     * @return la chaine désérialisée
     */
    public static String deserializeString(String s) {
        return new String(DECODER.decode(s), StandardCharsets.UTF_8);
    }

    /**
     * retourne la chaîne composée des chaînes séparées par le séparateur
     * 
     * @param strings
     *            l'ensemble des chaines
     * @param combine
     *            le séparateur
     * @return la chaîne composée des chaînes séparées par le séparateur
     */
    public static String combineString(char combine, String... strings) {
        return String.join(combine + "", strings);
    }

    /**
     * retourne un tableau contenant la chaine de caractères partitionnée par le
     * séparateur
     * 
     * @param s
     *            la chaine de caractère
     * @param combine
     *            le séparateur
     * @return un tableau contenant la chaine de caractères partitionnée par le
     *         séparateur
     */
    public static String[] splitString(String s, char combine) {
        return s.split(combine + "");
    }

    /**
     * sérialise un ensemble de cartes
     * 
     * @param set
     *            l'ensemble de cartes
     * @return une chaine de caractère représentant l'ensemble de cartes
     *         serialisé
     */
    public static String serializeCardSet(CardSet set) {
        return serializeLong(set.packed());
    }

    /**
     * désérialise un ensemble de cartes
     * 
     * @param s
     *            l'ensemble de cartes sérialisé
     * @return l'ensemble de cartes désérialisé
     */
    public static CardSet deserializeCardSet(String s) {
        return CardSet.ofPacked(deserializeLong(s));
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
        return combineString(',', score, cards, trick);
    }

    /**
     * désérialise un état du tour
     * 
     * @param s
     *            l'état du tour sérialisé
     * @return l'état du tour désérialisé
     */
    public static TurnState deserializeTurnState(String s) {
        String[] comp = splitString(s, ',');
        long score = deserializeLong(comp[0]);
        long cards = deserializeLong(comp[1]);
        int trick = deserializeInt(comp[2]);
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
        return serializeInt(trick.packed());
    }

    /**
     * désérialise un pli
     * 
     * @param trick
     *            le pli sérialisé
     * @return le pli désérialisé
     */
    public static Trick deserializeTrick(String trick) {
        return Trick.ofPacked(deserializeInt(trick));
    }

    /**
     * sérialise une carte
     * 
     * @param c
     *            la carte
     * @return une chaine de caractère représentant la carte sérialisé
     */
    public static String serializeCard(Card c) {
        return serializeInt(c.packed());
    }

    /**
     * désérialise une carte
     * 
     * @param card
     *            la carte sérialisée
     * @return la carte sérialisée
     */
    public static Card deserialize(String card) {
        return Card.ofPacked(deserializeInt(card));
    }

    /**
     * sérialise un score
     * 
     * @param s
     *            le score
     * @return une chaine de caractère représentant le score sérialisé
     */
    public static String serializeScore(Score s) {
        return serializeLong(s.packed());
    }

    /**
     * désérialise un score
     * 
     * @param score
     *            le score sérialisé
     * @return le score désérialisé
     */
    public static Score deserializeScore(String score) {
        return Score.ofPacked(deserializeLong(score));
    }

    public static String serializeEnum(Enum e) {
        return serializeInt(e.ordinal());
    }

}
