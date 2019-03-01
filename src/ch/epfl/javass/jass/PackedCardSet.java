package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;

public final class PackedCardSet {
    private static final int CARDPERCOLOR = 9;
    
    private static final long[] TRUMPABOVE = { 0b1_1111_1110, 0b1_1111_1100,
            0b1_1111_1000, 0b0_0010_0000, 0b1_1110_1000, 0, 0b1_1010_1000,
            0b1_0010_1000, 0b0_0010_1000 };

    private final static long SUBSETCOLOROF[] = { Bits64.mask(0, 9),
            Bits64.mask(16, 9), Bits64.mask(32, 9), Bits64.mask(48, 9) };

    public static final long EMPTY = 0L;
    public static final long ALL_CARDS = SUBSETCOLOROF[0] | SUBSETCOLOROF[1]
            | SUBSETCOLOROF[2] | SUBSETCOLOROF[3];

    private PackedCardSet() {
    }
    
    public static void main(String[] args) {
        long x=SUBSETCOLOROF[1]|SUBSETCOLOROF[3];
        System.out.println(toString(x));
       
    }
 
    
    public static boolean isValide(long pkCardSet) {
        return (pkCardSet&(~ALL_CARDS))==0L;
    }
    
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        int color=PackedCard.color(pkCard).ordinal();
        return TRUMPABOVE[PackedCard.rank(pkCard).ordinal()]<<(color*16);
    }
    
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return add(0L, pkCard);
    }
    
    public static int size(long pkCardSet) {
        assert isValide(pkCardSet);
        return Long.bitCount(pkCardSet);
    }
    
    
    public static int get(long pkCardSet, int index) {
        assert isValide(pkCardSet);
        assert index>=0 && index<size(pkCardSet);
                
        for(int i=0; i<index; i++) {
            pkCardSet^=Long.lowestOneBit(pkCardSet);
        }
        
        int numberOfZero=Long.numberOfTrailingZeros(pkCardSet);
        int color =numberOfZero/16;
        int rank =numberOfZero-color*16;
        
        return PackedCard.pack(Card.Color.ALL.get(color),Card.Rank.ALL.get(rank));
        
    }
    
    private static int cardIndex(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return PackedCard.color(pkCard).ordinal()*16+PackedCard.rank(pkCard).ordinal();
    }
    
    public static long add(long pkCardSet, int pkCard) {
        assert isValide(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return pkCardSet|(1L<<cardIndex(pkCard));
    }

    public static long remove(long pkCardSet, int pkCard) {
        assert isValide(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return  pkCardSet&(~(1L<<cardIndex(pkCard)));
    }
    
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValide(pkCardSet);
        assert PackedCard.isValid(pkCard);    
        return (pkCardSet&(1L<<cardIndex(pkCard)))==0;
    }
    
    public static long complement(long pkCardSet) {
        assert isValide(pkCardSet);
        return (~pkCardSet)&ALL_CARDS;
    }
    
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValide(pkCardSet1);
        assert isValide(pkCardSet2);
        return pkCardSet1|pkCardSet2;
    }
    
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValide(pkCardSet1);
        assert isValide(pkCardSet2);
        return pkCardSet1&pkCardSet2;
    }
    
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValide(pkCardSet1);
        assert isValide(pkCardSet2);
        return pkCardSet1&~(pkCardSet2&pkCardSet1);
    }
    
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValide(pkCardSet);
        return pkCardSet&SUBSETCOLOROF[color.ordinal()];
    }
    
    public static String toString(long pkCardSet) {
        assert isValide(pkCardSet);
        StringJoiner out = new StringJoiner(",", "{", "}");
        int size=size(pkCardSet);
        for(int i=0; i<size; i++) {
            out.add(PackedCard.toString(get(pkCardSet,i)));
        }
        return out.toString();
    }

}
