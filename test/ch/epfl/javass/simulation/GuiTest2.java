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

public final class GuiTest2 extends Application {
    public static void main(String[] args) { 
        launch(args); 
     }

    @Override
    public void start(Stage primaryStage) throws Exception {
      Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
      PlayerId.ALL.forEach(p -> ns.put(p, p.name()));
      ScoreBean sB = new ScoreBean();
      TrickBean tB = new TrickBean();
      Trick t = Trick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_1);
      t=t.withAddedCard(Card.of(Card.Color.CLUB, Card.Rank.JACK));
      tB.setTrick(t);
      tB.setTrump(Card.Color.CLUB);
      System.out.println("aaaaaa");
      GraphicalPlayer g =
        new GraphicalPlayer(PlayerId.PLAYER_2, ns, tB, sB);
      System.out.println("aaaaaa");

      primaryStage.setTitle("Image viewer");
      primaryStage.setScene(g.createStage());
      primaryStage.show();
    }
}