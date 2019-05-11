package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JassComponent {
    
    public static final int FIT_WIDTH_CARD_TRICK = 120;
    public static final int FIT_HEIGHT_CARD_TRICK = 180;
    
    public static final int FIT_WIDTH_CARD_HAND = 80;
    public static final int FIT_HEIGHT_CARD_HAND = 120;
    
    public static final int FIT_WIDTH_TRUMP = 101;
    public static final int FIT_HEIGHT_TRUMP = 101;
    
    private static final ObservableMap<Card, Image> CARDS_TRICK_IMAGE = FXCollections
            .unmodifiableObservableMap(allCardsImage(240));
    private static final ObservableMap<Card, Image> CARDS_HAND_IMAGE = FXCollections
            .unmodifiableObservableMap(allCardsImage(160));
    private static final ObservableMap<Card.Color, Image> TRUMP_IMAGE = FXCollections
            .unmodifiableObservableMap(allTrumpImage());

    private static ObservableMap<Card, Image> allCardsImage(int size) {
        ObservableMap<Card, Image> images = FXCollections.observableHashMap();
        for (int i = 0; i < CardSet.ALL_CARDS.size(); i++) {
            Card card = CardSet.ALL_CARDS.get(i);
            images.put(card, new Image("/card_" + card.color().ordinal() + "_"
                    + card.rank().ordinal() + "_" + size + ".png"));
        }
        return images;
    }

    private static ObservableMap<Card.Color, Image> allTrumpImage() {
        ObservableMap<Card.Color, Image> images = FXCollections
                .observableHashMap();
        for (Card.Color c : Card.Color.ALL) {
            images.put(c, new Image("/trump_" + c.ordinal() + ".png"));
        }
        return images;
    }
    
    private static <T> ImageView bindImage(ObservableMap<T, Image> images,  ObservableValue<? extends T> key) {
        ImageView image = new ImageView();
        image.imageProperty().bind(Bindings.valueAt(images, key));
        return image;
    }
    
    public static ImageView cardsTrickImages(ObservableValue<? extends Card> key) {
        ImageView image = bindImage(CARDS_TRICK_IMAGE, key);
        image.setFitHeight(FIT_HEIGHT_CARD_TRICK);
        image.setFitWidth(FIT_WIDTH_CARD_TRICK);
        return image;
    }
    
    public static ImageView cardsHandImages(ObservableValue<? extends Card> key) {
        ImageView image = bindImage(CARDS_HAND_IMAGE, key);
        image.setFitHeight(FIT_HEIGHT_CARD_HAND);
        image.setFitWidth(FIT_WIDTH_CARD_HAND);
        return image;
    }
    
    public static ImageView trumpImages(ObservableValue<? extends Card.Color> key) {
        ImageView image = bindImage(TRUMP_IMAGE, key);
        image.setFitHeight(FIT_WIDTH_TRUMP);
        image.setFitWidth(FIT_HEIGHT_TRUMP);
        return image;
    }

}
