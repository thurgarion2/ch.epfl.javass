package ch.epfl.javass.simulation;



import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.EnumMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ch.epfl.javass.gui.*;
import ch.epfl.javass.jass.*;
import javafx.stage.Stage;

public final class GuiTest extends Application {
    public static void main(String[] args) { 
        launch(args); 
     }

    @Override
    public void start(Stage primaryStage) throws Exception {
      Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
      PlayerId.ALL.forEach(p -> ns.put(p, p.name()));
      ScoreBean sB = new ScoreBean();
      TrickBean tB = new TrickBean();
      GraphicalPlayer g =
        new GraphicalPlayer(PlayerId.PLAYER_2, ns, tB, sB);
      
      
      new AnimationTimer() {
        long now0 = 0;
        TurnState s = TurnState.initial(Card.Color.SPADE,
                        Score.INITIAL,
                        PlayerId.PLAYER_3);
        CardSet d = CardSet.ALL_CARDS;

        @Override
        public void handle(long now) {
      if (now - now0 < 1_000_000_000L || s.isTerminal())
        return;
      now0 = now;

      s = s.withNewCardPlayed(d.get(0));
      d = d.remove(d.get(0));
      tB.setTrump(s.trick().trump());
      tB.setTrick(s.trick());

      if (s.trick().isFull()) {
        s = s.withTrickCollected();
        for (TeamId t: TeamId.ALL)
          sB.setTurnPoints(t, s.score().turnPoints(t));
      }
        }
      }.start();
      primaryStage.setScene(g.createStage());
      primaryStage.show();
    }
}