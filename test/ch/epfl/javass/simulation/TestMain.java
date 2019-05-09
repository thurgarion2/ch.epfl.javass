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

public final class TestMain extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
      Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
      ps.put(PlayerId.PLAYER_1, new GraphicalPlayerAdapter());
      ps.put(PlayerId.PLAYER_2, new MctsPlayer(PlayerId.PLAYER_2, 123, 10_000));
      ps.put(PlayerId.PLAYER_3, new MctsPlayer(PlayerId.PLAYER_3, 456, 10_000));
      ps.put(PlayerId.PLAYER_4, new PacedPlayer(new MctsPlayer(PlayerId.PLAYER_4, 789, 10_000), 3));

      Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
      PlayerId.ALL.forEach(i -> ns.put(i, i.name()));

      new Thread(() -> {
      JassGame g = new JassGame(0, ps, ns);
      while (! g.isGameOver()) {
        g.advanceToEndOfNextTrick();
        try { Thread.sleep(1000); } catch (Exception e) {}
      }
      }).start();
    }
  }