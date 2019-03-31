package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * plusieurs méthodes utiles pour traiter avec des entier (int) sous forme
 * binaire
 * 
 * @author erwan serandour (296100)
 *
 */

public final class Bits32 {
    private Bits32() {
    }

    // vérifie si le nombre n'occcupe pas plus de valeur que sa taille (cf pack)
  
    //bits le nombre size la taille

    private static void checkSize(int bits, int size) {
        Preconditions.checkArgument(size > 0 && size < Integer.SIZE);
        int mask = mask(size, Integer.SIZE - size);
        Preconditions.checkArgument((bits & mask) == 0);
    }

    /**
     * retourne un entier dont les bits d'index allant de start (inclus) à start
     * + size (exclus) valent 1
     * 
     * @param start
     *            le départ (inclus) (doit positif et plus petit que 33)
     * @param size
     *            la taille (doit positif et start+size<=32)
     * @return un entier dont les bits d'index allant de start (inclus) à start
     *         + size (exclus) valent 1
     * @throws IllegalArgumentException
     *             si les conditions ne sont pas respectés cf start, size
     */

    public static int mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Integer.SIZE);
        Preconditions.checkArgument(start + size <= Integer.SIZE && size >= 0);
        long mask = (1L << size) - 1;
        return (int) mask << start;
    }

    /**
     * extrait de bits les bits allant de start (inclus) à start+size (exclus)
     * 
     * @param bits
     *            le nombre où il faut extraire les bits
     * @param start
     *            le début de la plage (inclus) (doit positif et plus petit que
     *            33)
     * @param size
     *            la taille de la plage (doit positif et start+size<=32)
     * @return une valeur dont les size bits de poids faible sont égaux à ceux
     *         de bits allant de l'index start (inclus) à l'index start + size
     *         (exclus)
     * @throws IllegalArgumentException
     *             si les conditions ne sont pas respectés cf start, size
     */
    public static int extract(int bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Integer.SIZE);
        Preconditions.checkArgument(start + size <= Integer.SIZE && size >= 0);
        return (bits & mask(start, size)) >>> start;
    }

    /**
     * retourne les valeurs v1 et v2 empaquetées dans un entier de type int
     * 
     * @param v1
     *            le nombre (int) qui occupe les s1 premier bits de poids faible
     * @param s1
     *            le nombre de bit occupé par v1 (doit être plus grand ou égale
     *            à la taille de v1)
     * @param v2
     *            le nombre (int) qui occupe les s2 bits de poids faible (à
     *            partire de s1)
     * @param s2
     *            le nombre de bit occupé par v2 (doit être plus grand ou égale
     *            à la taille de v2)
     * @return retourne les valeurs v1 et v2 empaquetées dans un entier de type
     *         int
     * @throws IllegalArgumentException
     *             si s1 ou s2 negatif ou s1+s2>32 ou ne respecte pas les
     *             conditions de tailles de v1 et v2 cf s1,s2
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        checkSize(v1, s1);
        checkSize(v2, s2);
        Preconditions.checkArgument(s1 + s2 <= Integer.SIZE);
        return v1 | v2 << s1;
    }

    /**
     * retourne les valeurs v1 et v2 et v3 empaquetées dans un entier de type
     * int
     * 
     * @param v1
     *            le nombre (int) qui occupe les s1 premier bits de poids faible
     * @param s1
     *            le nombre de bit occupé par v1 (doit être plus grand ou égale
     *            à la taille de v1)
     * @param v2
     *            le nombre (int) qui occupe les s2 bits de poids faible (à
     *            partire de s1)
     * @param s2
     *            le nombre de bit occupé par v2 (doit être plus grand ou égale
     *            à la taille de v2)
     * @param v3
     *            le nombre (int) qui occupe les s3 bits de poids faible (à
     *            partire de s1+s2)
     * @param s3
     *            le nombre de bit occupé par v3 (doit être plus grand ou égale
     *            à la taille de v3)
     * @return retourne les valeurs v1 et v2 et v3 empaquetées dans un entier de
     *         type int
     * 
     * @throws IllegalArgumentException
     *             si s1 ou s2 ou s3 negatif ou s1+s2+s3>32 ou ne respecte pas
     *             les conditions de tailles de v1 et v2 et v3 cf s1,s2,s3
     */

    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        Preconditions.checkArgument(s1 + s2 + s3 <= Integer.SIZE);
        int v1ToV2 = pack(v1, s1, v2, s2);
        return pack(v1ToV2, s1 + s2, v3, s3);
    }

    /**
     * retourne les valeurs v1 et v2 et v3 et v4 et v6 et v7 empaquetées dans un
     * entier de type int
     * 
     * @param v1
     *            le nombre (int) qui occupe les s1 premier bits de poids faible
     * @param s1
     *            le nombre de bit occupé par v1 (doit être plus grand ou égale
     *            à la taille de v1)
     * @param v2
     *            le nombre (int) qui occupe les s2 bits de poids faible (à
     *            partire de s1)
     * @param s2
     *            le nombre de bit occupé par v2 (doit être plus grand ou égale
     *            à la taille de v2)
     * @param v3
     *            le nombre (int) qui occupe les s3 bits de poids faible (à
     *            partire de s1+s2)
     * @param s3
     *            le nombre de bit occupé par v3 (doit être plus grand ou égale
     *            à la taille de v3)
     * @param v4
     *            le nombre (int) qui occupe les s4 bits de poids faible à
     *            partire de s1+s2+s3
     * @param s4
     *            le nombre de bit occupé par v4 (doit être plus grand ou égale
     *            à la taille de v4)
     * @param v5
     *            le nombre (int) qui occupe les s5 bits de poids faible à
     *            partire de s1+s2+s3+s4
     * @param s5
     *            le nombre de bit occupé par v5 (doit être plus grand ou égale
     *            à la taille de v5)
     * @param v6
     *            le nombre (int) qui occupe les s6 bits de poids faible à
     *            partire de s1+s2+s3+s4+s5
     * @param s6
     *            le nombre de bit occupé par v6 (doit être plus grand ou égale
     *            à la taille de v6)
     * @param v7
     *            le nombre (int) qui occupe les s7 bits de poids faible à
     *            partire de s1+s2+s3+s4+s5+s6
     * @param s7
     *            le nombre de bit occupé par v7 (doit être plus grand ou égale
     *            à la taille de v7)
     * 
     * @return retourne les valeurs v1 et v2 et v3 et v4 et v6 et v7 empaquetées
     *         dans un entier de type int
     * @throws IllegalArgumentException
     *             si les s1,...s7<0 ou s1+...+s7>32 ou ne respecte pas les
     *             conditions de tailles de v1,..v7 cf s1,...,s7
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        Preconditions.checkArgument(
                s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE);

        int v1ToV3 = pack(v1, s1, v2, s2, v3, s3);
        int v4ToV6 = pack(v4, s4, v5, s5, v6, s6);
        int v1ToV7 = pack(v1ToV3, s1 + s2 + s3, v4ToV6, s4 + s5 + s6, v7, s7);
        return v1ToV7;
    }

}
