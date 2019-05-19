package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import javafx.beans.property.SimpleObjectProperty;
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
 * génère l'interface permettant à un joueur de commuinquer avec le programme
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
    
    private final static int OWN_TEAM=1;
    private final static int OTHER_TEAM=0;
    private final static int SIZE_SCORE=5;
    private final static int TEAM_NAMES=0;
    private final static int TURN_POINTS=1;
    private final static int LAST_TRICK_POINTS=2;
    private final static int TOTAL =3;
    private final static int GAME_POINTS =4;
    
    private final static int BLUR=4;
    private static final float ENABLE = 1;
    private static final float DISABLE = 0.2f;
    
    private static final int NEXT_TEAM_PLAYER=2;
    private final Pane mainPane;
    private final String stageName;
           
    private static PlayerId fromOwn(PlayerId own, int index) {
        return PlayerId.ALL.get((own.ordinal()+index)%PlayerId.COUNT);
    }
    
    private static String teamNames(PlayerId own, TeamId t, Map<PlayerId, String> playersNames) {
        int start = own.team()==t ? 0 : 1;
        return playersNames.get(fromOwn(own, start))
                +" et "
                +playersNames.get(fromOwn(own, start+NEXT_TEAM_PLAYER));
    }
    
    private static Text bindText(ObservableValue<?> prop, HPos alignement) {
        Text out = new Text();
        GridPane.setHalignment(out, alignement);
        out.textProperty().bind(Bindings.convert(prop));
        return out;
    }
    
    private static Node[] scoreTeam(PlayerId own, TeamId t, ScoreBean score, Map<PlayerId, String> plNames) {
        Node[] line = new Node[SIZE_SCORE];
       
        Text names = new Text(teamNames(own, t, plNames)+" : ");
        GridPane.setHalignment(names, HPos.RIGHT);
       
        Text lasTrick = new Text();
        score.turnPointsProperty(t).addListener(
                (o,oV,nV)->{
                    int diff = nV.intValue()-oV.intValue();
                    lasTrick.setText(diff>0 ? " (+"+diff+")" : "" );
                });
        
        line[TEAM_NAMES]=names;
        line[TURN_POINTS]=bindText(score.turnPointsProperty(t), HPos.RIGHT);
        line[LAST_TRICK_POINTS]=(lasTrick);
        line[TOTAL]=new Text(" / Total : ");
        line[GAME_POINTS]=bindText(score.gamePointsProperty(t), HPos.RIGHT);
        
        return line;
    }
        
    private static Pane scorePane(PlayerId own, ScoreBean score,  Map<PlayerId, String> plNames) {
        GridPane pane = new GridPane();
       
        pane.addRow(OTHER_TEAM, scoreTeam(own, own.team().other(), score, plNames));
        pane.addRow(OWN_TEAM, scoreTeam(own, own.team(), score, plNames));
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
        rect.setEffect(new GaussianBlur(BLUR));
        rect.visibleProperty().bind(trick.winningPlayerProperty().isEqualTo(id));
        pane.getChildren().add(rect);
        return pane;
    }
    
    private static VBox cardPlayed(PlayerId own, PlayerId id, TrickBean trick, Map<PlayerId, String> playersNames) {
        VBox box = new VBox();
        Text name = new Text(playersNames.get(id));
        name.setStyle(CSS_TEXT_NAMES);
  
        box.getChildren().add(id==own? cardImage(id, trick) : name);
        box.getChildren().add(id==own? name: cardImage(id, trick));
        box.setAlignment(Pos.CENTER);
        
        return box;
    }
   
    private static Pane trickPane(PlayerId own, TrickBean trick, Map<PlayerId, String> playersNames) {
        List<Node> cards = new LinkedList<>();
        for(int i=0; i<PlayerId.COUNT; i++) {
            cards.add(cardPlayed(own,fromOwn(own,i),trick,playersNames));
        }
        Pane pane = JassComponent.cross(JassComponent.trumpImages(trick.trumpProperty()),
               cards); 
        pane.setStyle(CSS_TRICK_PANE);
        return pane;
    }
    

    private static Pane handPane(HandBean hand, ArrayBlockingQueue<Card> queu) {
        HBox box= new HBox();
        
        for(int i=0; i<hand.hand().size(); i++) {
            int index=i;
            
            ImageView card = 
                    JassComponent.cardsHandImages(Bindings.valueAt(hand.hand(), index));
            card.setOnMouseClicked(e -> {
                    try {
                        queu.put(hand.hand().get(index));
                    } catch (InterruptedException e1) {
                        throw new Error(e1.toString());
                    }
            });
            
            BooleanBinding isPlayable=Bindings.createBooleanBinding(
                ()->hand.playableCards().contains(hand.hand().get(index)),
                hand.hand(),
                hand.playableCards());
            
            card.opacityProperty().bind(Bindings.when(isPlayable)
                    .then(ENABLE)
                    .otherwise(DISABLE));
            
            card.disableProperty().bind(isPlayable.not());
            box.getChildren().add(card);
        }
        
        box.setStyle(CSS_HAND_CARD);
        return box;
    }
    
    private static Pane victoryPane(Map<PlayerId, String> plNames,
            ScoreBean score, PlayerId own, TeamId t) {
        BorderPane pane = new BorderPane();
        Text victory = new Text();
        victory.textProperty().bind(Bindings.format("%1$s ont gagné avec %2$d points contre %3$d.",
                teamNames(own,t, plNames),
                score.totalPointsProperty(t),
                score.totalPointsProperty(t.other())
                ));
        pane.setCenter(victory);
        pane.visibleProperty().bind(score.winningTeamProperty().isEqualTo(t));
        pane.setStyle(CSS_VICTORY_PANE);
        return pane;
    }
    
    /**
     * @param own
     *            l'id du joueur utilisant la fenètre
     * @param playersNames
     *            les noms des joueurs en fonctions de leur id
     * @param trick
     *            le bean du trick du jeu
     * @param score
     *            le bean du score jeu
     * @param hand
     *            le bean de la main du joueur
     * @param queu
     *            permet à la fénètre de communiqué la carte choisie par le
     *            joueur
     */
    public GraphicalPlayer(PlayerId own,
            Map<PlayerId, String> playersNames,
            TrickBean trick,
            ScoreBean score,
            HandBean hand,
            ArrayBlockingQueue<Card> queu
            ) {
          stageName="Javass-"+playersNames.get(own);
          StackPane pane = new StackPane();
        
          BorderPane gamePane = new BorderPane();
          gamePane.setTop(scorePane(own, score, playersNames));
          gamePane.setCenter(trickPane(own, trick, playersNames));
          gamePane.setBottom(handPane(hand, queu));
          pane.getChildren().add(gamePane);
          pane.setAlignment(Pos.CENTER);
          
          pane.getChildren().add(victoryPane(playersNames, score, own, own.team()));
          pane.getChildren().add(victoryPane(playersNames, score, own, own.team().other()));
  
          mainPane=pane;
    }
    
  
    /**
     * génére la fenètre permettant au joueur d'interagir avec le jeux
     * 
     * @return la fenètre permettant au joueur d'interagir avec le jeux
     */
    public Stage createStage() {
        Scene main = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(main);
        stage.setTitle(stageName);
        return stage;

    }
}
