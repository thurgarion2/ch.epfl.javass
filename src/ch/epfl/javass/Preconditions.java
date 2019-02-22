package ch.epfl.javass;

/**
 * check preconditions and leave exception according to the result
 * 
 * @author erwan serandour (296100)
 *
 */
public final class Preconditions {
  
    private Preconditions() {
    }

    /**
     * leave an exception according to b
     * 
     * @param b
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
     * check wheter the index is in the range
     * 
     * @param index
     *            l'index dans l'intervalle
     * @param size
     *            la taille du tableau
     * @return l'index si compatible
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
