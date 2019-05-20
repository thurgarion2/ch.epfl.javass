package ch.epfl.javass.gui;

import java.util.List;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * contient plusieurs composants réutilisables pour les interfaces graphiques
 * 
 * @author erwan serandour (296100)
 *
 */
/**
 * @author esera
 *
 */
public class JassComponent {

    /**
     * la largeur d'une carte utilisé dans le pli
     */
    public static final int FIT_WIDTH_CARD_TRICK = 120;
    /**
     * la hauteur d'une carte utilisé dans le pli
     */
    public static final int FIT_HEIGHT_CARD_TRICK = 180;

    /**
     * la largeur d'une carte utilisé dans la main
     */
    public static final int FIT_WIDTH_CARD_HAND = 80;
    /**
     * la hauteur d'une carte utilisé dans la main
     */
    public static final int FIT_HEIGHT_CARD_HAND = 120;

    /**
     * la largeur de l'image représantant l'atout
     */
    public static final int FIT_WIDTH_TRUMP = 101;
    /**
     * la hauteur de l'image représantant l'atout
     */
    public static final int FIT_HEIGHT_TRUMP = 101;

    private static final ObservableMap<Card, Image> CARDS_TRICK_IMAGE = FXCollections
            .unmodifiableObservableMap(allCardsImage(240));
    private static final ObservableMap<Card, Image> CARDS_HAND_IMAGE = FXCollections
            .unmodifiableObservableMap(allCardsImage(160));
    private static final ObservableMap<Card.Color, Image> TRUMP_IMAGE = FXCollections
            .unmodifiableObservableMap(allTrumpImage());
    
    private static final int SIZE_OF_BORDER =4;

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

    private static <T> ImageView bindImage(ObservableMap<T, Image> images,
            ObservableValue<? extends T> key) {
        ImageView image = new ImageView();
        image.imageProperty().bind(Bindings.valueAt(images, key));
        return image;
    }

    /**
     * retourne une image d'une carte (format pli)
     * 
     * @param key
     *            la valeur observable d'une carte du jeux du jass
     * @return une image d'une carte (format pli)
     */
    public static ImageView cardsTrickImages(
            ObservableValue<? extends Card> key) {
        ImageView image = bindImage(CARDS_TRICK_IMAGE, key);
        image.setFitHeight(FIT_HEIGHT_CARD_TRICK);
        image.setFitWidth(FIT_WIDTH_CARD_TRICK);
        return image;
    }

    /**
     * retourne une image d'une carte (format main)
     * 
     * @param key
     *            la valeur observable d'une carte du jeux du jass
     * @return une image d'une carte (format main)
     */
    public static ImageView cardsHandImages(
            ObservableValue<? extends Card> key) {
        ImageView image = bindImage(CARDS_HAND_IMAGE, key);
        image.setFitHeight(FIT_HEIGHT_CARD_HAND);
        image.setFitWidth(FIT_WIDTH_CARD_HAND);
        return image;
    }

    /**
     * retourne une image de l'atout
     * 
     * @param key
     *            la valeur observable d'une couleur
     * @return une image de l'atout
     */
    public static ImageView trumpImages(
            ObservableValue<? extends Card.Color> key) {
        ImageView image = bindImage(TRUMP_IMAGE, key);
        image.setFitHeight(FIT_WIDTH_TRUMP);
        image.setFitWidth(FIT_HEIGHT_TRUMP);
        return image;
    }

    /**
     * retourne une croix avec au centre le noeud center et au extremité partant
     * du bas les noeuds du bord
     * 
     * @param center
     *            le noeud du centre
     * @param border
     *            les noeuds de bords
     * @return une croix avec au centre le noeud
     */
    public static Pane cross(Node center, List<Node> border) {
        
        if(border.size()!=SIZE_OF_BORDER) {
            throw new IllegalArgumentException();
        }
        
        GridPane pane = new GridPane();
        
        GridPane.setHalignment(center,  HPos.CENTER);
        border.forEach((n)->GridPane.setHalignment(n,  HPos.CENTER));
        
        pane.add(center, 1, 1);
        pane.add(border.get(3),0, 0, 1, 3);
        pane.add(border.get(2), 1, 0);
        pane.add(border.get(1),2, 0, 1, 3);
        pane.add(border.get(0), 1, 2);
        
        return pane;
    }

}
