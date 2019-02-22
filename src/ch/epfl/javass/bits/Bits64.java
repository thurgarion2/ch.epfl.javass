package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

public final class Bits64 {
    private Bits64() {
    }
    
    private static void checkSize(long bits, int size) {
        Preconditions.checkArgument(size > 0 && size < Long.SIZE);
        long mask = mask(size, Long.SIZE - size);
        Preconditions.checkArgument((bits & mask) == 0);
    }
    
    
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Long.SIZE);
        Preconditions.checkArgument(start + size <= Long.SIZE && size >= 0);
        if(size==Long.SIZE) {
            return (mask(0,Long.SIZE-1)<<1)|0b1;
        }
        long mask = ((long) 1 << size) - 1;
        return mask << start;
    }
    
    public static long extract(long bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Long.SIZE);
        Preconditions.checkArgument(start + size <= Long.SIZE && size >= 0);
        long fromStart =  bits >>> start;
        long fromSize = bits >>> (start + size);
        return  (fromStart ^ (fromSize << size));

    }
    
    public static long pack(long v1, int s1, long v2, int s2) {
        checkSize(v1, s1);
        checkSize(v2, s2);
        Preconditions.checkArgument(s1 + s2 <= Long.SIZE);
        return v1 | v2 << s1;
    }

}
