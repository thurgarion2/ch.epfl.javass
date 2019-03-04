package ch.epfl.javass.jass;


import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CardSetTest {
    
    private static List<Card> allCards(){
        List<Card> all = new LinkedList<Card>();
        
        for(Card.Color c : Card.Color.ALL) {
            for(Card.Rank r : Card.Rank.ALL) {
               all.add(Card.of(c, r));
            }
        }
        
        return all;
    }
    
    @Test
    void emptyHasNoCards() {
        assertEquals(0,CardSet.EMPTY.size());
    } 
    
    @Test
    void ALL_CardsAreInRightOrder() {
       List <Card> all = allCards();
       int i=0;
       for(Card c : all) {
           assertEquals(c.toString(),CardSet.ALL_CARDS.get(i).toString());
           i++;
       }
    }
    
    @Test
    void ofWorksWithNoCard() {
        assertTrue(CardSet.EMPTY.equals(CardSet.of(new LinkedList<Card>()))); 
    }
    
    @Test
    void ofWorksWithAllCards() {
        assertTrue(CardSet.ALLL_CARDS.equals(CardSet.of(allCards()))); 
    }
    
    @Test
    void ofWorksForSomeCases() {
        List <Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            cards.add(c);
            CardSet set = CardSet.of(cards);
            int i=0;
            for(Card a : cards) {
                assertEquals(a.toString(),set.get(i).toString());
                i++;
            }
        }
    }
    
    @Test
    void ofPackedFailsForInvalidCases() {
        for(int i=0; i<2<<7; i++) {
            long packed= i<<9;
            if(!PackedCardSet.isValid(packed)) {
                assertThrows(IllegalArgumentException.class,()->{CardSet.ofPacked(packed);});
            }
        }
    }
    
    @Test
    void ofPackedWoksOnAllCards() {
        CardSet set = CardSet.ofPacked(PackedCardSet.ALL_CARDS);
        assertTrue(CardSet.ALL_CARDS.equals(set));
    }
    
    @Test
    void packedWorksOnSomeTestCase() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            assertTrue(CardSet.of(cards).equals(CardSet.ofPacked(CardSet.of(cards).packed())));
            cards.add(c);
            assertTrue(CardSet.of(cards).equals(CardSet.ofPacked(CardSet.of(cards).packed())));
        }
    }
    
    @Test 
    void isEmptyWorksOnEmptySet() {
        assertTrue(CardSet.ofPacked(0).isEmpty());
    }
    
    @Test
    void isEmptyWorksOnNoEmptySet() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            cards.add(c);
            assertFalse(CardSet.of(cards));
        }
    }
    
    @Test
    void sizeWorksOnSomeCases() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            assertEquals(cards.size(),CardSet.of(cards).size());
            cards.add(c);
            assertEquals(cards.size(),CardSet.of(cards).size());
        }
    }
    

}
