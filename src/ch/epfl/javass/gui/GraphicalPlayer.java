package ch.epfl.javass.gui;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class GraphicalPlayer {
    
    private static final int FIT_WIDTH_CARD_TRICK = 120;
    private static final int FIT_HEIGHT_CARD_TRICK = 180;
    
    private static final int FIT_WIDTH_CARD_HAND = 120;
    private static final int FIT_HEIGHT_CARD_HAND = 180;
    
    private static final int FIT_WIDTH_TRUMP = 101;
    private static final int FIT_HEIGHT_TRUMP = 101;
    
    private static final String CSS_SCORE_PANE = "-fx-font: 16 Optima;"
            + "-fx-background-color: lightgray;" 
            + "-fx-padding: 5px;"
            + "-fx-alignment: center;";  
    private static final String CSS_TRICK_PANE = "-fx-background-color: whitesmoke;" + 
            "-fx-padding: 5px;" + 
            "-fx-border-width: 3px 0px;" + 
            "-fx-border-style: solid;" + 
            "-fx-border-color: gray;" + 
            "-fx-alignment: center;";
    private static final String CSS_HALO = "-fx-arc-width: 20;" + 
            "-fx-arc-height: 20;" + 
            "-fx-fill: transparent;" + 
            "-fx-stroke: lightpink;" + 
            "-fx-stroke-width: 5;" + 
            "-fx-opacity: 0.5;";
    private static final String CSS_TEXT_NAMES = "-fx-font: 14 Optima";
    private static final String CSS_HAND_CARD = "-fx-background-color: lightgray;" + 
            "-fx-spacing: 5px;" + 
            "-fx-padding: 5px;";
    
    private static final ObservableMap<Card, Image> CARDS_TRICK_IMAGE
            =FXCollections.unmodifiableObservableMap(allCardsImage(240));
    private static final ObservableMap<Card, Image> CARDS_HAND_IMAGE 
            =FXCollections.unmodifiableObservableMap(allCardsImage(160));
    private static final ObservableMap<Card.Color, Image> TRUMP_IMAGE 
            =FXCollections.unmodifiableObservableMap(allTrumpImage());
    

    private final Pane mainPane;
    
    private static ObservableMap<Card, Image> allCardsImage(int size) {
        ObservableMap<Card, Image> images = FXCollections.observableHashMap();
        for(int i=0; i<CardSet.ALL_CARDS.size(); i++) {
            Card card=CardSet.ALL_CARDS.get(i);
            images.put(card, new Image("/card_"+card.color().ordinal()+"_"+card.rank().ordinal()+"_"+size+".png"));
        }
        return images;
    }
    private static ObservableMap<Card.Color, Image> allTrumpImage() {
        ObservableMap<Card.Color, Image> images = FXCollections.observableHashMap();
        for(Card.Color c : Card.Color.ALL) {
            images.put(c, new Image("/trump_"+c.ordinal()+".png"));
        }
        return images;
    }
    
    private static PlayerId fromOwn(PlayerId own, int index) {
        return PlayerId.ALL.get((own.ordinal()+index)%PlayerId.COUNT);
    }
    
    private static Text bindText(ObservableValue<?> prop, TextAlignment al) {
        Text out = new Text();
        out.setTextAlignment(al);
        out.textProperty().bind(Bindings.convert(prop));
        return out;
    }
    
    private static Node[] scoreTeam(PlayerId own, TeamId t, ScoreBean score, Map<PlayerId, String> playersNames) {
        Node[] line = new Node[5];
        
        StringJoiner teamName = new StringJoiner(" et ",""," : ");
        for(int i=0; i<PlayerId.COUNT; i++) {
            if(fromOwn(own, i).team()==t) {
                teamName.add(fromOwn(own, i).team()==t ? playersNames.get(fromOwn(own, i)) : "");
            }
        }
        Text name = new Text(teamName.toString());
        name.setTextAlignment(TextAlignment.RIGHT);
        line[0]=name;
        line[1]=bindText(score.turnPointsProperty(t), TextAlignment.RIGHT);
        
        Text lasTrick = new Text();
        score.turnPointsProperty(t).addListener(
                (o,oV,nV)->{
                    int diff = nV.intValue()-oV.intValue();
                    lasTrick.setText(diff>0 ? " (+"+diff+")" : "" );
                });
        line[2]=(lasTrick);
        line[3]=new Text(" / Total : ");
        line[4]=bindText(score.gamePointsProperty(t), TextAlignment.LEFT);
        
        return line;
    }
    
    
    private static Pane scorePane(PlayerId own, ScoreBean score, Map<PlayerId, String> playersNames) {
        GridPane pane = new GridPane();
       
        pane.addRow(0, scoreTeam(own, own.team().other(), score, playersNames));
        pane.addRow(1, scoreTeam(own, own.team(), score, playersNames));
 
        pane.setStyle(CSS_SCORE_PANE);
        return pane;
        
    }
    
    private static <T> ImageView bindImage(ObservableMap<T, Image> images,  ObservableValue<? extends T> key) {
        ImageView image = new ImageView();
        image.imageProperty().bind(Bindings.valueAt(images, key));
        return image;
    }
    
   
    private static Pane cardImage(PlayerId id, TrickBean trick) {
        StackPane pane = new StackPane();
        
        ImageView image = bindImage(CARDS_TRICK_IMAGE,Bindings.valueAt(trick.trick(), id));
        image.setFitHeight(FIT_HEIGHT_CARD_TRICK);
        image.setFitWidth(FIT_WIDTH_CARD_TRICK);
        pane.getChildren().add(image);
        
        Rectangle rect = new Rectangle();
        rect.setWidth(FIT_WIDTH_CARD_TRICK);
        rect.setHeight(FIT_HEIGHT_CARD_TRICK);
        rect.setStyle(CSS_HALO);
        rect.setEffect(new GaussianBlur(4));
        rect.visibleProperty().bind(trick.winningPlayerProperty().isEqualTo(id));
        pane.getChildren().add(rect);
        return pane;
    }
    
    private static VBox ownCardPlayed(PlayerId id, TrickBean trick, Map<PlayerId, String> playersNames) {
        VBox box = new VBox();
        Text name = new Text(playersNames.get(id));
        name.setStyle(CSS_TEXT_NAMES);
  
        box.getChildren().add(cardImage(id, trick));
        box.getChildren().add(name);
        box.setAlignment(Pos.CENTER);
        
        return box;
    }
    private static VBox otherCardPlayed(PlayerId id, TrickBean trick, Map<PlayerId, String> playersNames) {
        VBox box = new VBox();
        Text name = new Text(playersNames.get(id));
        name.setStyle(CSS_TEXT_NAMES);
        
        box.getChildren().add(name);
        box.getChildren().add(cardImage(id, trick));
        box.setAlignment(Pos.CENTER);
        
        return box;
    }
   
    private static Pane trickPane(PlayerId own, TrickBean trick, Map<PlayerId, String> playersNames) {
        GridPane pane = new GridPane();
       
        pane.add(otherCardPlayed(fromOwn(own,3),trick,playersNames),0, 0, 1, 3);
        pane.add(otherCardPlayed(fromOwn(own,1),trick,playersNames),2, 0, 1, 3);
        pane.add(otherCardPlayed(fromOwn(own,2),trick,playersNames), 1, 0);
        pane.add(ownCardPlayed(fromOwn(own,0),trick,playersNames), 1, 2);
        
        ImageView trump=bindImage(TRUMP_IMAGE, trick.trumpProperty());
        trump.setFitHeight(FIT_HEIGHT_TRUMP);
        trump.setFitWidth(FIT_WIDTH_TRUMP);
        GridPane.setHalignment(trump,  HPos.CENTER);
        pane.add(trump, 1, 1);
        
        pane.setStyle(CSS_TRICK_PANE);
        return pane;
    }
    
    private static Pane handPane(HandBean hand, ArrayBlockingQueue<Card> queu) {
        HBox box= new HBox();
        
        for(int i=0; i<hand.hand().size(); i++) {
            int index=i;
            
            ImageView card = bindImage(CARDS_HAND_IMAGE,  Bindings.valueAt(hand.hand(), i));
            card.setFitHeight(FIT_HEIGHT_CARD_HAND);
            card.setFitWidth(FIT_WIDTH_CARD_HAND);
            card.setOnMouseClicked(e->{try {
                queu.offer(hand.hand().get(index), Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e1) {
            }});
            
            BooleanBinding isPlayable=Bindings.createBooleanBinding(()->{
                return hand.playableCards().contains(hand.hand().get(index)); },
                hand.hand(), hand.playableCards());
            card.opacityProperty().bind(Bindings.when(isPlayable).then(1).otherwise(0.2));
            card.disableProperty().bind(isPlayable.not());
            
            box.getChildren().add(card);
        }
        
        box.setStyle(CSS_HAND_CARD);
        return box;
    }
    
    public GraphicalPlayer(PlayerId own,
            Map<PlayerId, String> playersNames,
            TrickBean trick,
            ScoreBean score,
            HandBean hand,
            ArrayBlockingQueue<Card> queu
            ) {
          BorderPane pane = new BorderPane();
          pane.setTop(scorePane(own, score, playersNames));
          pane.setCenter(trickPane(own, trick, playersNames));
          pane.setBottom(handPane(hand, queu));
          
          mainPane=pane;
    }

    public Stage createStage() {
        Scene main = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(main);
        return stage;
    }
}
