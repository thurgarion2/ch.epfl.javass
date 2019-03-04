package ch.epfl.javass.jass;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        assertTrue(CardSet.ALL_CARDS.equals(CardSet.of(allCards()))); 
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
            assertFalse(CardSet.of(cards).isEmpty());
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
    
    @Test
    void getfailsWithOutOfBoundIndex() {
        
    }
    
    @Test
    void getWorksForSomeCases() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            cards.add(c);
            CardSet set = CardSet.of(cards);
            int index=0;
            
            for(Card each : cards) {
                assertEquals(each,set.get(index));
                index++;
            }
        }
    }
    
    @Test
    void addWorksOnSomeCases() {
        List<Card> all = allCards();
        CardSet set = CardSet.EMPTY;
        for(Card c : all) {
            set=set.add(c);
            assertEquals(c,set.get(set.size()-1));
        }
    }
    
    @Test
    void removeWorksOnSomeCases(){
        CardSet set = CardSet.ALL_CARDS;
        List<Card> all = allCards();
        for(Card c : all) {
            assertEquals(c,set.get(0));
            set=set.remove(c);
        }
    }
    
    @Test
    void containsWorksOnSomeCases() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            CardSet set =CardSet.of(cards);
            for(Card each : all) {
                if(cards.contains(each)) {
                    assertTrue(set.contains(each));
                }else {
                    assertFalse(set.contains(each));
                }
            }
        }
    }
    
    @Test
    void complementWorksOnSomeCases() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            CardSet set =CardSet.of(cards).complement();
            for(Card each : all) {
                if(!cards.contains(each)) {
                    assertTrue(set.contains(each));
                }else {
                    assertFalse(set.contains(each));
                }
            }
        }
    }
    
    @Test
    void equalsWorksOnSameSet() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            CardSet set1= CardSet.of(cards);
            CardSet set2= CardSet.of(cards);
            assertTrue(set1.equals(set2));
            cards.add(c);
        }
    }
    
    @Test
    void equalsWorksOnDifferantSet() {
        List<Card> all = allCards();
        List <Card> cards = new LinkedList<>();
        for(Card c : all) {
            CardSet set1= CardSet.of(cards);
            CardSet set2= CardSet.of(cards);
            set2.remove(c);
            assertFalse(set1.equals(set2));
            cards.add(c);
        }
    }
    

}
