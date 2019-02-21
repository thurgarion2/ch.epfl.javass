package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

public final class PackedCard {
    private PackedCard(){}

    private static final int INVALID = 0b111111;

    public static boolean isValid(int pkCard){
        if(Bits32.extract(pkCard, 6, Integer.SIZE-6)!=0)
            return false;
        
        int rank = Bits32.extract(pkCard, 0,4);
        if(rank < 0 || rank >8)
           return false;
       
        return true;
    }

    public static int pack(Card.Color c, Card.Rank r){
            int indexRank = Card.Rank.ALL.indexOf(r);
            int indexColor = Card.Color.ALL.indexOf(c);
            int pkCard = Bits32.pack(indexRank, 4, indexColor, 2);
            assert isValid(pkCard);
        return pkCard;
    }

    public static Card.Color color(int pkCard){
        assert isValid(pkCard);
        int colorInt = Bits32.extract(pkCard, 4,2);
        Card.Color color = Card.Color.ALL.get(colorInt);
        return color;
    }

    public static Card.Rank rank(int pkCard){
        assert isValid(pkCard);
        int rankInt = Bits32.extract(pkCard, 0,4);
        Card.Rank rank = Card.Rank.ALL.get(rankInt);
        return rank;
    }


    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR){
        assert isValid(pkCardL);
        assert isValid(pkCardR);
        Card.Color colorL = color(pkCardL);
        Card.Rank rankL = rank(pkCardL);
        Card.Color colorR = color(pkCardR);
        Card.Rank rankR = rank(pkCardR);
        //check if the two are trump
        if(colorL.equals(trump) && colorR.equals(trump)){
            if(rankL.trumpOrdinal() > rankR.trumpOrdinal())
                return true;
            return false;
        //check if one of them is trump
        }else if (colorL.equals(trump) || colorR.equals(trump)){
            if(colorL.equals(trump))
                return true;
            return false;
        //check if they are comparable
        }else if (colorL.equals(colorR)){
            if(rankL.ordinal() > rankR.ordinal())
                return true;
            return false;
        }
        //if the program reach this point, the cards are not comparable
        return false;
    }

    /**
     * @param //rank of a card r
     * @return true if rank is under 10 else false
     */
    private static boolean checkU10(Card.Rank r){
        if(Card.Rank.ALL.indexOf(r) < 5)
            return true;
        return false;
    }

    /**
     * @param //Card.Rank r
     * @return true if r is not 9 or J, false otherwise
     */
    private static boolean checkNot9OrJ(Card.Rank r){
        if(r.equals(Card.Rank.NINE) || r.equals(Card.Rank.JACK))
            return false;
        return true;
    }

    public static int points(Card.Color trump, int pkCard){
        assert isValid(pkCard);
        Card.Color color = color(pkCard);
        Card.Rank rank = rank(pkCard);
        int value = -1;
        if(checkNot9OrJ(rank)){
            if(checkU10(rank)){
                value = 0;
            }else{
                switch(rank){
                case TEN: value = 10; break;
                case QUEEN: value = 3; break;
                case KING: value = 4; break;
                case ACE: value = 11; break;
                }
            }
        }else{
            if(trump.equals(color)){
                switch (rank){
                case NINE: value = 14; break;
                case JACK: value = 20; break;
                }
            }else{
                switch (rank){
                case NINE: value = 0; break;
                case JACK: value = 2; break;
                }
            }
        }
        return value;
    }


    public  static String toString(int pkCard){
        int indexColor = Bits32.extract(pkCard, 4, 2);
        int indexRank  = Bits32.extract(pkCard, 0, 4);
        return Card.Color.ALL.get(indexColor).toString() + Card.Rank.ALL.get(indexRank).toString();
    }

}

