package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**represant any card of the game of jass
 * 
 * @author erwan serandour (296100)
 *
 */

public class Card {
    private int card;
    
    
    
    /**
     * @param packedCard
     */
    private Card(int packedCard){
        card=packedCard;
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
        SIX("6",0,0),
        SEVEN("7",1,0),
        EIGHT("8",2,0),
        NINE("9",7,14,0),
        TEN("10",3,10),
        JACK("J",8,20,2),
        QUEEN("Q",4,3),
        KING("K",5,4),
        ACE("A",6,11);

        public final static List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        public final static int COUNT = ALL.size();

        private String symbol;
        private int trumpOrdinal;
        private int normalPoints;
        private int trumpPoints;
        
     
        private Rank(String s, int trumpOrdinal, int trumpPoints, int normalPoints) {
            this.trumpOrdinal=trumpOrdinal;
            this.symbol=s;
            this.trumpPoints=trumpPoints;
            this.normalPoints=normalPoints;
        }
        
        private Rank(String s, int trumpOrdinal, int normalPoints) {
            this( s,  trumpOrdinal, normalPoints, normalPoints);
        }

        @Override
        public String toString(){return symbol;}

        public int trumpOrdinal(){return trumpOrdinal;}
        
        public int trumpPoints() {return this.trumpPoints;}
        
        public int normalPoints() {return this.normalPoints;}

    }

    public static Card of(Color c, Rank r){
       return new Card(PackedCard.pack(c, r));
    }

    public static Card ofPacked(int packed){
        return new Card(packed);
    }

    public int packed(){
        return card;
    }

    public Color color(){
        return PackedCard.color(card);
    }

    public Rank rank(){ return PackedCard.rank(card);}

    public boolean isBetter(Color trump, Card that){
         return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    public int points(Color trump){ return PackedCard.points(trump, this.packed());}

    @Override
    public boolean equals(Object thatO){
        if(thatO.getClass()!=this.getClass())
            return false;
        Card other = (Card)thatO;
        return this.card==other.card;
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
