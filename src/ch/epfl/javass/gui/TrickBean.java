package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public final class TrickBean {
    private final ObjectProperty<Card.Color> trumpProperty;
    private final ObjectProperty<PlayerId> winningPlayerProperty;
    private final ObservableMap<PlayerId, Card> trickProperty;
    
    public TrickBean() {
        trumpProperty= new SimpleObjectProperty<>(null);
        winningPlayerProperty= new SimpleObjectProperty<>(null);
        trickProperty= FXCollections.observableHashMap();
        for(PlayerId id : PlayerId.ALL) {
            trickProperty.put(id, null);
        }
    }
    
    public ReadOnlyObjectProperty <Card.Color> trumpProperty(){
        return trumpProperty;
    }
    
    public void setTrump(Card.Color trump) {
        trumpProperty.set(trump);
    }
    
    public  ReadOnlyObjectProperty <PlayerId> winningPlayerProperty(){
        return winningPlayerProperty;
    }
    
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trickProperty);
    }
    
    public void setTrick(Trick newTrick) {
        PlayerId winner = newTrick.isEmpty() ? null : newTrick.winningPlayer();
        this.winningPlayerProperty.set(winner);
        for(int i=0; i<PlayerId.COUNT; i++) {
            trickProperty.put(newTrick.player(i), i<newTrick.size() ? newTrick.card(i) : null);
        }
    }
    
    
}
