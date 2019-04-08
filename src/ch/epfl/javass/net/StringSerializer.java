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
 * plusieurs méthodes utiles pour serialiser et déserialiser des stirng et des ints sous forme de
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

    
}
