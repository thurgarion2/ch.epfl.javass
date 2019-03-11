package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import ch.epfl.javass.jass.*;

import org.junit.jupiter.api.Test;

public class TrickTest {
  
    private static List<Integer> tricks(){
        List<Integer> tricks = new ArrayList<>();
        for(Card.Color color : Card.Color.ALL) {
            for(PlayerId id : PlayerId.ALL) {
                int trick = PackedTrick.firstEmpty(color, id);
                int nb=36;
                while(nb>0) {
                    int sum=nb%3+1;
                    int nextTrick=trick;
                    for(int i=0; i<sum && nb>0; i++) {
                        nextTrick=PackedTrick.withAddedCard(nextTrick,CardSet.ALL_CARDS.get(nb-1).packed());
                        nb--;
                    }
                    tricks.add(nextTrick);
                }
               
                
            }
        }
        return tricks;
   
    }
    
    private static Trick full() {
        Trick t1= Trick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1);
        for(int i=0; i<4; i++) {
            t1=t1.withAddedCard(Card.ofPacked(0));
        }
        return t1;
    }
    
    private static Trick last() {
        int trick=0b00001000;
        trick<<=24;
        return Trick.ofPacked(trick);
    }
    
    private static Trick empty() {
        return Trick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1);
    }

    @Test
    void invalidIsGood() {
        assertEquals(PackedTrick.INVALID,Trick.INVALID.packed());
    }
    
    @Test
    void firstEmptyWorksOnAllPossibility() {
        for(Card.Color color : Card.Color.ALL) {
            for(PlayerId id : PlayerId.ALL) {
                Trick trick = Trick.firstEmpty(color, id);
                assertEquals(color,trick.trump());
                assertEquals(id, trick.player(0));
            }
        }
    }
    
    @Test
    void failsWithInvalidTrick() {
        assertThrows(IllegalArgumentException.class, ()->{Trick.ofPacked(PackedTrick.INVALID);});
    }
    
    @Test
    void ofPackedWorksOnSomeCases() {
        for(Integer i : tricks()) {
            Integer second=Trick.ofPacked(i).packed();
            assertEquals(i, second);
        }
    }
    
    @Test
    void nextEmptyFailsWithNotFullTrick() {
        for(Integer i : tricks()) {
            Integer second=Trick.ofPacked(i).packed();
            if(!PackedTrick.isFull(second)) {
                assertThrows(IllegalStateException.class,()->{Trick.ofPacked(second).nextEmpty();});
            }
        }
    }
    
    @Test
    void playerIdFailsOnInvalidCases() {
        assertThrows(IndexOutOfBoundsException.class,()->{Trick.INVALID.player(-1);});
        assertThrows(IndexOutOfBoundsException.class,()->{Trick.INVALID.player(4);});
    }
    
    @Test
    void cardFailsWithInvalidIndex() {
        Trick t1= Trick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1);
        Trick t2=t1;
        assertThrows(IndexOutOfBoundsException.class,()->{t2.card(-1);});
        assertThrows(IndexOutOfBoundsException.class,()->{t2.card(1);});
        t1=t1.withAddedCard(Card.ofPacked(0));
        Trick t3=t1;
        assertThrows(IndexOutOfBoundsException.class,()->{t3.card(2);});
        t1=t1.withAddedCard(Card.ofPacked(0));
        Trick t4=t1;
        assertThrows(IndexOutOfBoundsException.class,()->{t4.card(3);});
        t1=t1.withAddedCard(Card.ofPacked(0));
        Trick t5=t1;
        assertThrows(IndexOutOfBoundsException.class,()->{t5.card(4);});
    }
    
    @Test 
    void withAddedCardFailsOnFullTrick() {
        assertThrows(IllegalStateException.class,()->{full().withAddedCard(Card.ofPacked(0));});
    }
    
    @Test
    void baseColorFailsOnEmptyTrick() {
        assertThrows(IllegalStateException.class,()->{empty().baseColor();});
    }
    
    @Test
    void winningPlayerFailsOnEmptyTrick() {
        assertThrows(IllegalStateException.class,()->{empty().winningPlayer();});
    }
    
    @Test
    void playableCardsFailsOnFullTrick() {
        assertThrows(IllegalStateException.class,()->{full().playableCards(CardSet.ofPacked(0));});
    }
    
    @Test
    void nextEmptyWorksForLastTrick() {
        assertEquals(Trick.INVALID, last().nextEmpty());
    }
    
    @Test
    void playableCardsWorksWithOnlyJack() {
        List <Card> cards = new ArrayList<>();
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.HEART, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.DIAMOND, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        CardSet hand=CardSet.of(cards);
        
        Trick t=empty();
        t=t.withAddedCard(Card.of(Card.Color.CLUB, Card.Rank.EIGHT));
        assertEquals(hand, t.playableCards(hand));
    }
    
    @Test
    void playableCardsWorksOnTrumpAsBaseColor() {
        List <Card> cards = new ArrayList<>();
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.KING));
        cards.add(Card.of(Card.Color.DIAMOND, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        CardSet hand1=CardSet.of(cards);
        CardSet hand2=CardSet.of(cards.subList(0, 2));
        
        Trick t=empty();
        t=t.withAddedCard(Card.of(Card.Color.CLUB, Card.Rank.EIGHT));
        assertEquals(hand2, t.playableCards(hand1));
    }
    
    @Test
    void playableCardsWorksOnTrivial() {
        List <Card> cards = new ArrayList<>();
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.KING));
        cards.add(Card.of(Card.Color.SPADE, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.SPADE, Card.Rank.QUEEN));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.SIX));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.SEVEN));
        cards.add(Card.of(Card.Color.DIAMOND, Card.Rank.JACK));
        CardSet hand1=CardSet.of(cards);
        CardSet hand2=CardSet.of(cards.subList(0, 4));
        
        Trick t=empty();
        t=t.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.EIGHT));
        t=t.withAddedCard(Card.of(Card.Color.CLUB, Card.Rank.QUEEN));
        assertEquals(hand2, t.playableCards(hand1));
    }
    @Test
    void playableCardsWorksOnNoCardsOfRequestColor() {
        List <Card> cards = new ArrayList<>();
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.KING));
        cards.add(Card.of(Card.Color.DIAMOND, Card.Rank.JACK));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.SIX));
        cards.add(Card.of(Card.Color.CLUB, Card.Rank.SEVEN));
        CardSet hand1=CardSet.of(cards);
        CardSet hand2=CardSet.of(cards.subList(0, 3));
        
        Trick t=empty();
        t=t.withAddedCard(Card.of(Card.Color.SPADE, Card.Rank.EIGHT));
        t=t.withAddedCard(Card.of(Card.Color.CLUB, Card.Rank.QUEEN));
        assertEquals(hand2, t.playableCards(hand1));
    }
}
