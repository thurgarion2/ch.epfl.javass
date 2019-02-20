package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Card {
    private Color color;
    private Rank rank;

    private Card(Color c, Rank r){
        color=c;
        rank=r;
    };

    public enum Color{
        SPADE("\u2660"),
        HEART("\u2661"),
        DIAMOND("\u2662"),
        CLUB("\u2663");

        private String symbol;

        private Color(String symbol) {
            this.symbol =symbol;
        }

        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = ALL.size();

        @Override
        public String toString() {
            return this.symbol;
        }
    }

    public enum Rank{
        SIX("6",0),
        SEVEN("7",1),
        EIGHT("8",2),
        NINE("9",7),
        TEN("10",3),
        JACK("J",8),
        QUEEN("Q",4),
        KING("K",5),
        ACE("A",6);

        public final static List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        public final static int COUNT = ALL.size();

        private String symbol;
        private int trumpOrdinal;
        private Rank(String s, int trumpOrdinal) {
            this.trumpOrdinal=trumpOrdinal;
            this.symbol=s;
        }

        @Override
        public String toString(){return symbol;}

        public int trumpOrdinal(){return ALL.indexOf(this);}

    }

    public static Card of(Color c, Rank r){
       return new Card(c, r);
    }

    public static Card ofPacked(int packed){
        return new Card(PackedCard.color( packed),PackedCard.rank( packed));
    }

    public int packed(){
        return PackedCard.pack(color, rank);
    }

    public Color color(){
        return this.color;
    }

    public Rank rank(){ return this.rank;}

    public boolean isBetter(Color trump, Card that){
         return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    public int points(Color trump){ return PackedCard.points(trump, this.packed());}

    @Override
    public boolean equals(Object thatO){
        if(thatO.getClass()!=this.getClass())
            return false;
        Card other = (Card)thatO;
        return other.color==this.color & other.rank==rank;
    }

    @Override
    public int hashCode(){
        return this.packed();
    }

    @Override
    public String toString(){
        return PackedCard.toString(this.packed());
    }


}
