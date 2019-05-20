package ch.epfl.javass.gui;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.net.JassCommand;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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

/**
 * génère le stage pour représenter un joueur
 * 
 * @author erwan serandour (296100)
 *
 */
public final class GraphicalPlayer {
    
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
    private static final String CSS_VICTORY_PANE = "-fx-font: 16 Optima;" +
    		"-fx-background-color: white;";
    private static final String CSS_TEXT_NAMES = "-fx-font: 14 Optima";
    private static final String CSS_HAND_CARD = "-fx-background-color: lightgray;" + 
            "-fx-spacing: 5px;" + 
            "-fx-padding: 5px;";
    
    private final Pane mainPane;
       
    private static PlayerId fromOwn(PlayerId own, int index) {
        return PlayerId.ALL.get((own.ordinal()+index)%PlayerId.COUNT);
    }
    
    private static Text bindText(ObservableValue<?> prop, TextAlignment al) {
        Text out = new Text();
        out.setTextAlignment(al);
        out.textProperty().bind(Bindings.convert(prop));
        return out;
    }
    
    private static Node[] scoreTeam(TeamId t, ScoreBean score, String names1, String names2) {
        Node[] line = new Node[5];
        
        Text names = new Text(names1+" et "+names2+" : ");
        names.setTextAlignment(TextAlignment.RIGHT);
        
        Text lasTrick = new Text();
        score.turnPointsProperty(t).addListener(
                (o,oV,nV)->{
                    int diff = nV.intValue()-oV.intValue();
                    lasTrick.setText(diff>0 ? " (+"+diff+")" : "" );
                });
        
        line[0]=names;
        line[1]=bindText(score.turnPointsProperty(t), TextAlignment.RIGHT);
        line[2]=(lasTrick);
        line[3]=new Text(" / Total : ");
        line[4]=bindText(score.gamePointsProperty(t), TextAlignment.LEFT);
        
        return line;
    }
        
    private static Pane scorePane(PlayerId own, ScoreBean score, Map<PlayerId, String> playersNames) {
        GridPane pane = new GridPane();
       
        pane.addRow(0, scoreTeam(own.team().other(),
                score, 
                playersNames.get(fromOwn(own,1)),
                playersNames.get(fromOwn(own,3))));
        pane.addRow(1, scoreTeam( own.team(),
                score, 
                playersNames.get(fromOwn(own,0)),
                playersNames.get(fromOwn(own,2))));
 
        pane.setStyle(CSS_SCORE_PANE);
        return pane;
        
    }
    
   
    private static Pane cardImage(PlayerId id, TrickBean trick) {
        StackPane pane = new StackPane();
        
        pane.getChildren().add(
                JassComponent.cardsTrickImages(Bindings.valueAt(trick.trick(), id)));
        
        Rectangle rect = new Rectangle();
        rect.setWidth(JassComponent.FIT_WIDTH_CARD_TRICK);
        rect.setHeight(JassComponent.FIT_HEIGHT_CARD_TRICK);
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
        
        ImageView trump=
                JassComponent.trumpImages(trick.trumpProperty());
        GridPane.setHalignment(trump,  HPos.CENTER);
        pane.add(trump, 1, 1);
        
        pane.setStyle(CSS_TRICK_PANE);
        return pane;
    }
    

    private static Pane handPane(HandBean hand, ArrayBlockingQueue<Card> queu) {
        HBox box= new HBox();
        
        for(int i=0; i<hand.hand().size(); i++) {
            int index=i;
            
            ImageView card = 
                    JassCommand.cardsHandImages(Bindings.valueAt(hand.hand(), index));
            card.setOnMouseClicked(e->{try {
                
                queu.put(hand.hand().get(index));
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
          StackPane pane = new StackPane();
        
          BorderPane gamePane = new BorderPane();
          gamePane.setTop(scorePane(own, score, playersNames));
          gamePane.setCenter(trickPane(own, trick, playersNames));
          gamePane.setBottom(handPane(hand, queu));
          pane.getChildren().add(gamePane);
          pane.setAlignment(Pos.CENTER);
          
          pane.getChildren().add(
                  victoryPane(playersNames.get(own),
                  playersNames.get(fromOwn(own, 2)),
                  score, own.team()));
          
          pane.getChildren().add(
                  victoryPane(playersNames.get(fromOwn(own, 1)),
                  playersNames.get(fromOwn(own, 3)),
                  score, own.team().other()));
          
          mainPane=pane;

    }
  
    private static Pane victoryPane(String name1, String name2, ScoreBean score, TeamId team) {
        BorderPane pane = new BorderPane();
        Text victory = new Text();
        victory.textProperty().bind(Bindings.format("%1$s et %2$s ont gagné avec %3$d points contre %4$d.",
                name1,
                name2,
                score.totalPointsProperty(team),
                score.totalPointsProperty(team.other())
                ));
        pane.setCenter(victory);
        pane.visibleProperty().bind(score.winningTeamProperty().isEqualTo(team));
        pane.setStyle(CSS_VICTORY_PANE);
        return pane;
    }
    
    public Stage createStage() {
        Scene main = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(main);
        return stage;

    }
}
