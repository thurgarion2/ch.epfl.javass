package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * met en place les prorpiétés JavaFX pour la main du joueur
 * 
 * @author  Jean.Daniel Rouveyrol(301480)
 *
 */
public final class HandBean {
	
	private ObservableList<Card> handProperty;
	private ObservableSet<Card> playableCardsProperty;
	
	/**
	 * 
	 */
	public HandBean() {
		handProperty = FXCollections.observableArrayList();
		playableCardsProperty = FXCollections.observableSet();
		for(int i = 0; i<Jass.HAND_SIZE; ++i) {
		    handProperty.add(null);
        }
	}

    /**
     * retourne une vue non modifiable sur la propriété playableCards
     * 
     * @return une vue non modifiable sur la propriété hand
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(handProperty);
    }

    /**
     * retourne une vue non modifiable sur la propriété playableCards
     * 
     * @return une vue non modifiable sur la propriété playableCards
     */
	public ObservableSet<Card> playableCards(){
			return FXCollections.unmodifiableObservableSet(playableCardsProperty);
	}

    /**
     * met à jour hand, en fonction du CardSet passé en argument
     * 
     * @param newHand
     *            met à jour hand, en fonction du CardSet passé en argument
     */
	public void setHand(CardSet newHand) {
	    //handProperty se comporte comme un tableau
		for(int i=0; i<Jass.HAND_SIZE; i++) {
	        Card current=handProperty.get(i);
		    Card isIn =  newHand.contains(current) ? current : null;
		    handProperty.set(i, newHand.size()==Jass.HAND_SIZE ? newHand.get(i) : isIn);
		}
	}

    /**
     * met à jour playableCards, en fonction du CardSet passé en argument
     * 
     * @param newPlayableCards
     *            met à jour playableCards, en fonction du CardSet passé en
     *            argument
     */
	public void setPlayableCards(CardSet newPlayableCards) {
	    //l'ordre n'importe pas soit une carte est jouable soit non
	    playableCardsProperty.clear();
	    for(int i=0; i<newPlayableCards.size(); i++) {
	        playableCardsProperty.add(newPlayableCards.get(i));
	    }
	}
}
