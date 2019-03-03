package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;


/**
 * plusieurs méthodes utiles pour traiter avec des entier(long) sous forme
 * binaire
 * 
 * @author erwan serandour (296100)
 *
 */

public final class Bits64 {
    private Bits64() {
    }
    
    
    private static void checkSize(long bits, int size) {
        Preconditions.checkArgument(size > 0 && size < Long.SIZE);
        long mask = mask(size, Long.SIZE - size);
        Preconditions.checkArgument((bits & mask) == 0);
    }
    
    
    
    /**
     * retourne un entier dont les bits d'index allant de start (inclus) à start
     * + size (exclus) valent 1
     * 
     * @param start
     *            le départ (inclus) (doit positif et plus petit que 65)
     * @param size
     *            la taille (doit positif et start+size<=64)
     * @return un entier dont les bits d'index allant de start (inclus) à start
     *         + size (exclus) valent 1
     * @throws IllegalArgumentException
     *             si les conditions ne sont pas respectés cf start, size
     */
    
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Long.SIZE);
        Preconditions.checkArgument(start + size <= Long.SIZE && size >= 0);
        if(size==Long.SIZE) {
            return (mask(0,Long.SIZE-1)<<1)|0b1;
        }
        long mask = ( 1L << size) - 1;
        return mask << start;
    }
    
    /**
     * extrait de bits les bits allant de start (inclus) à start+size (exclus)
     * 
     * @param bits
     *            le nombre où il faut extraire les bits
     * @param start
     *            le début de la plage (inclus) (doit positif et plus petit que
     *            65)
     * @param size
     *            la taille de la plage (doit positif et start+size<=64)
     * @return une valeur dont les size bits de poids faible sont égaux à ceux
     *         de bits allant de l'index start (inclus) à l'index start + size
     *         (exclus)
     * @throws IllegalArgumentException
     *             si les conditions ne sont pas respectés cf start, size
     */
    
    public static long extract(long bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Long.SIZE);
        Preconditions.checkArgument(start + size <= Long.SIZE && size >= 0);
        return  (bits&mask(start,size))>>>start;

    }
    
    /**
     * retourne les valeurs v1 et v2 empaquetées dans un entier de type long
     * 
     * @param v1
     *            le nombre (long) qui occupe les s1 premier bits de poids faible
     * @param s1
     *            le nombre de bit occupé par v1 (doit être plus grand ou égale
     *            à la taille de v1)
     * @param v2
     *            le nombre (long) qui occupe les s2 bits de poids faible (à
     *            partire de s1)
     * @param s2
     *            le nombre de bit occupé par v2 (doit être plus grand ou égale
     *            à la taille de v2)
     * @return retourne les valeurs v1 et v2 empaquetées dans un entier de type
     *         long
     * @throws IllegalArgumentException
     *             si s1 ou s2 negatif ou s1+s2>64 ou ne respecte pas les
     *             conditions de tailles de v1 et v2 cf s1,s2
     */
    
    public static long pack(long v1, int s1, long v2, int s2) {
        checkSize(v1, s1);
        checkSize(v2, s2);
        Preconditions.checkArgument(s1 + s2 <= Long.SIZE);
        return v1 | v2 << s1;
    }

}
