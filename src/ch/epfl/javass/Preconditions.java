package ch.epfl.javass;

/**
 * vérifie les préconditions et lève des exceptions en focntion du résultat
 * 
 * @author erwan serandour (296100)
 *
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * lève une exception en fontion de b
     * 
     * @param b
     *            un booléen
     * 
     * @throws IllegalArgumentException
     *             si b est vrai
     */

    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * vérifie si index est dans l'intervalle
     * 
     * @param index
     *            l'index
     * @param size
     *            la taille de l'intervalle
     * @return l'index si dans l'intervalle
     * 
     * @throws IndexOutOfBoundsException
     *             si l'index est hors de l'intervalle
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }
}
