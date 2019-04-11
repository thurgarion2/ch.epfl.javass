package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public final class TrickBean {
    private ObjectProperty<Card.Color> trumpProperty;
    private ObjectProperty<PlayerId> winningPlayerProperty;
    private  ObservableMap<PlayerId, Card> trickProperty;
    
    public TrickBean() {
        trumpProperty= new SimpleObjectProperty<>(null);
        winningPlayerProperty= new SimpleObjectProperty<>(null);
        trickProperty= FXCollections.observableHashMap();
    }
    
    
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trickProperty);
    }
    
    
}
