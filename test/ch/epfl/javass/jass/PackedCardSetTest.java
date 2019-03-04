package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class PackedCardSetTest {
	
	private static List<Long> allCardSets(){
		List<Integer> allCards = allCards();
		List<Long> all = new LinkedList<Long>();
		for(int i = 0; i < 36; ++i) {
			all.add(PackedCardSet.singleton(allCards.get(i)));
		}
		return all;
	}
	
	private static List<Integer> allCards(){
        List<Integer> all = new LinkedList<Integer>();
        for(Card.Color c : Card.Color.ALL) {
            for(Card.Rank r : Card.Rank.ALL) {
               all.add(PackedCard.pack(c, r));
            }
        }
        return all;
    }
	
	@Test
	void trumpAboveTest(){
		List<Integer> allCards = allCards();
		List<Long> allCardSets = allCardSets();
		
	}
	
	@Test
	void sizeTest() {
		
	}

}
