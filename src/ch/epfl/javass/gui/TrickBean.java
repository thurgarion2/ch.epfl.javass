package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ObjectProperty;

public final class TrickBean {
    private ObjectProperty<Card.Color> trumpProperty;
    private ObjectProperty<PlayerId> winningPlayerProperty;
}
