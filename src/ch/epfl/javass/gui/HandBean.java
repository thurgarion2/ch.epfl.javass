package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class HandBean {
	
	private final static int NUMBER_OF_CARDS_FULL_HAND = 9;
	
	private ObservableList<Card> handProperty;
	private ObservableSet<Card> playableCardsProperty;
	
	public HandBean() {
		handProperty = FXCollections.observableArrayList();
		playableCardsProperty = FXCollections.observableSet();
		filledWithNull(handProperty);
	}
	
	//remplis une main avec neufs éléments null
	private static void filledWithNull(ObservableList<Card> list) {
		for(int i = 0; i<NUMBER_OF_CARDS_FULL_HAND; ++i) {
			list.add(i, null);
		}
	}
		
	public ObservableList<Card> hand(){
		return FXCollections.unmodifiableObservableList(handProperty);
	}

	public ObservableSet<Card> playableCards(){
//		if(check)
			return FXCollections.unmodifiableObservableSet(playableCardsProperty);
//		return FXCollections.emptyObservableSet();
	}
	
	public void setHand(CardSet newHand) {
		if (newHand.size() != NUMBER_OF_CARDS_FULL_HAND) {
			for (int i = 0; i < NUMBER_OF_CARDS_FULL_HAND; ++i) {
				if((handProperty.get(i) != null) && (!newHand.contains(handProperty.get(i)))) {
					handProperty.set(i, null);
				}
			}
		} else {
			for (int i = 0; i < NUMBER_OF_CARDS_FULL_HAND; ++i) {
				handProperty.set(i, newHand.get(i));
			}
		}
	}

	public void setPlayableCards(CardSet newPlayableCards) {
		if (newPlayableCards.size() == NUMBER_OF_CARDS_FULL_HAND) {
			playableCardsProperty.clear();
			for(int i = 0 ; i < NUMBER_OF_CARDS_FULL_HAND; ++i) {
				playableCardsProperty.add(newPlayableCards.get(i));
			}
		}else {
			for(Card c : playableCardsProperty) {
				if(!newPlayableCards.contains(c)) {
					playableCardsProperty.remove(c);
				}
			}
		}
	}
}
