package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class HandBean {
	
	private ObservableList<Card> handProperty;
	private ObservableSet<Card> playableCardsProperty;
	
	public HandBean() {
		handProperty = FXCollections.observableArrayList();
		playableCardsProperty = FXCollections.observableSet();
		for(int i = 0; i<Jass.HAND_SIZE; ++i) {
		    handProperty.add(i, null);
        }
	}
		
	public ObservableList<Card> hand(){
		return FXCollections.unmodifiableObservableList(handProperty);
	}

	public ObservableSet<Card> playableCards(){
			return FXCollections.unmodifiableObservableSet(playableCardsProperty);
	}
	
	public void setHand(CardSet newHand) {
	    //handProperty se comporte comme un tableau
		for(int i=0; i<Jass.HAND_SIZE; i++) {
	        Card current=handProperty.get(i);
		    Card isIn = current!=null ? (newHand.contains(current) ? current : null) : null;
		    handProperty.set(i, newHand.size()==Jass.HAND_SIZE ? newHand.get(i) : isIn);
		}
	}

	public void setPlayableCards(CardSet newPlayableCards) {
	    //l'ordre n'importe pas soit une carte est jouable soit non
	    playableCardsProperty.clear();
	    for(int i=0; i<newPlayableCards.size(); i++) {
	        playableCardsProperty.add(newPlayableCards.get(i));
	    }
	}
}
