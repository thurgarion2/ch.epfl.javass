package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

public final class PackedTrick {
    public static int INVALID = (int)((2L<<32)-1);
    private static int FOURINVALIDCARD = Bits32.mask(0, 24);
    
    private PackedTrick() {}
    
    public static void main(String[] args) {
        int base=firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_3);
        long allCard = PackedCardSet.ALL_CARDS;
        for(int i=0; i<4; i++) {
            System.out.println(toString(base));
            base=withAddedCard(base, PackedCardSet.get(allCard, i));
        }
        System.out.println(toString(base));
        System.out.println(points(base));
    } 
    
    private static int cardsAt(int pkTrick, int index) {
        return Bits32.extract(pkTrick, index*6, 6);
    }
    
    
    public static boolean isValid(int pkTrick) {
        if(Bits32.extract(pkTrick, 24, 4)<9) {
            return false;
        }
        for(int i=0; i<3; i++) {
            int pkCard=Bits32.extract(pkTrick,i*6,6);
            int following=Bits32.extract(pkTrick,(i+1)*6,24-(i+1)*6);
            int invalide = Bits32.mask(0,24-(i+1)*6);
            if(!(pkCard==PackedCard.INVALID || (PackedCard.isValid(pkCard)&&following!=invalide)) ) {
                return false;
            }
        }
        return true;
    }
    
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        int color=trump.ordinal();
        int indexPlayer=firstPlayer.ordinal();
        int cardsAndIndex=Bits32.pack(FOURINVALIDCARD,24,0,2);
        return Bits32.pack(cardsAndIndex,28 ,indexPlayer, 2,color,2);
    }
    
    public static int nextEmpty(int pkTrick) {
        int index=index(pkTrick);
        int trickClear=Bits32.extract(pkTrick,28,4);
        return Bits32.pack(FOURINVALIDCARD, 28, index+1, 4, trickClear, 4);
    }
    
    public static boolean isLast(int pkTrick) {
        return index(pkTrick)==8;
    }
    
    public static boolean isEmpty(int pkTrick) {
        return Bits32.extract(pkTrick, 0, 24)==FOURINVALIDCARD;
    }
    
    public static boolean isFull(int pkTrick) {
        return size(pkTrick)==4;
    }
    
    public static int size(int pkTrick) {
        for(int i=0; i<4; i++) {
            if(cardsAt(pkTrick,i)==PackedCard.INVALID) {
                return i;
            }
        }
        return 4;
    }
    
    public static Color trump(int pkTrick) {
        int color=Bits32.extract(pkTrick, 30, 2);
        return Card.Color.ALL.get(color);
    }
    
    public static PlayerId player(int pkTrick, int index) {
        return PlayerId.ALL.get((Bits32.extract(pkTrick,28,2)+index)%4);
    }
    
    public static int index(int pkTrick) {
        return Bits32.extract(pkTrick, 24, 4);
    }
    
    public static int card(int pkTrick, int index) {
        return cardsAt(pkTrick,index);
    }
    
    public static int withAddedCard(int pkTrick, int pkCard) {
        int pkTrickClear=pkTrick&~(PackedCard.INVALID<<(6*size(pkTrick)));
        return pkTrickClear|(pkCard<<(6*size(pkTrick)));
    }
    
    public static Color baseColor(int pkTrick) {
        return PackedCard.color(card(pkTrick,0));
    }
    
    
    public static long playableCards(int pkTrick, long pkHand) {
        Color color=baseColor(pkTrick);
        Color trump=trump(pkTrick);
        int winningCard=card(pkTrick,indexOfwinningCard(pkTrick));
        //add all cards of baseColor
        long playableCards=PackedCardSet.subsetOfColor(pkHand, color);
        
        //all cards of trump not playable
        long under;
        if(PackedCard.color(winningCard)==trump) {
            under=PackedCardSet.intersection(PackedCardSet.trumpAbove(winningCard), pkHand);
            under=PackedCardSet.difference(PackedCardSet.subsetOfColor(pkHand, trump), under);
        }else {
           under=PackedCardSet.EMPTY;
        }
        
        if(PackedCardSet.isEmpty(playableCards)) {
            //all cards minus all cards of trump not playable
            return PackedCardSet.difference(pkHand, under);
        }
        
        int jack=PackedCard.pack(trump, Rank.JACK);
        if(PackedCardSet.size(playableCards)==1 && trump==color && PackedCardSet.get(playableCards,0)==jack) {
            return pkHand;
        }
        ////all cards of trump playable
        long above= PackedCardSet.difference(PackedCardSet.subsetOfColor(pkHand, trump), under);
        return PackedCardSet.union(playableCards, above);
    }
    
    public static int points(int pkTrick) {
        int points=0;
        for(int i=0; i<size(pkTrick); i++) {
            points+=PackedCard.points(trump(pkTrick), card(pkTrick,i));
        }
        points+= (isLast(pkTrick)) ? Jass.LAST_TRICK_ADDITIONAL_POINTS :0 ; 
        return points;
    }
    
    private static int indexOfwinningCard(int pkTrick) {
        int best=0;
        for(int i=1; i<size(pkTrick); i++) {
            if(PackedCard.isBetter(trump(pkTrick), card(pkTrick,i), card(pkTrick,best))) {
                best=i;
            }
        }
        return best;
    } 
    
    public static PlayerId winningPlayer(int pkTrick) {
        return player( pkTrick, indexOfwinningCard(pkTrick));
    }
    
    public static String toString(int pkTrick) {
        long set=PackedCardSet.EMPTY;
        for(int i=0; i<size(pkTrick); i++) {
            set=PackedCardSet.add(set, card(pkTrick,i));
        }
        String otherInfo="Atout : "+trump(pkTrick).toString();
        otherInfo+=" Player : "+player(pkTrick,0).toString();
        otherInfo+=" Index : "+index(pkTrick);
        return otherInfo+" "+PackedCardSet.toString(set);
    }

}
