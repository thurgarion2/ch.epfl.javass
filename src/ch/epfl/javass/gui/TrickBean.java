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

/**
 * le bean du pli d'une partie de jass
 * 
 * @author erwan serandour (296100)
 *
 */
public final class TrickBean {
    private final ObjectProperty<Card.Color> trumpProperty;
    private final ObjectProperty<PlayerId> winningPlayerProperty;
    private final ObservableMap<PlayerId, Card> trickProperty;

    /**
     * 
     */
    public TrickBean() {
        trumpProperty = new SimpleObjectProperty<>();
        winningPlayerProperty = new SimpleObjectProperty<>();
        trickProperty = FXCollections.observableHashMap();
        for (PlayerId id : PlayerId.ALL) {
            trickProperty.put(id, null);
        }
    }

    /**
     * retourne la propriété de l'atout
     * 
     * @return la propriété de l'atout
     */
    public ReadOnlyObjectProperty<Card.Color> trumpProperty() {
        return trumpProperty;
    }

    /**
     * met à jour l'atout
     * 
     * @param trump
     *            l'atout mis à jour
     */
    public void setTrump(Card.Color trump) {
        trumpProperty.set(trump);
    }

    /**
     * retourne la propriété du joueur gagant le plis
     * 
     * @return la propriété du joueur gagant le plis
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayerProperty;
    }

    /**
     * retourne la propriété du plis
     * 
     * @return la propriété du plis
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trickProperty);
    }

    /**
     * met à jour la propriété du plis et du joueur gagant le plis
     * 
     * @param newTrick
     *            le plis mis à jour
     */
    public void setTrick(Trick newTrick) {
        PlayerId winner = newTrick.isEmpty() ? null : newTrick.winningPlayer();
        this.winningPlayerProperty.set(winner);
        for (int i = 0; i < PlayerId.COUNT; i++) {
            trickProperty.put(newTrick.player(i),
                    i < newTrick.size() ? newTrick.card(i) : null);
        }
    }

}
