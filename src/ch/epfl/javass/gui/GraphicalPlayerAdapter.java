package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javafx.application.*;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;

public final class GraphicalPlayerAdapter implements Player {
    
    private GraphicalPlayer graphicalPlayer;
    private final ArrayBlockingQueue<Card> queu;
    
    private final HandBean hand;
    private final ScoreBean score;
    private final TrickBean trick;
    
    public GraphicalPlayerAdapter() {
        this.queu= new ArrayBlockingQueue<>(1);
        this.hand= new HandBean();
        this.score= new ScoreBean();
        this.trick= new TrickBean();
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(()->{
            this.hand.setHand(hand);
            this.hand.setPlayableCards(state.trick().playableCards(hand));
        });
        try {
            return queu.poll(Long.MAX_VALUE,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        return null;
    }
    
    
    @Override
    public void setPlayers(PlayerId ownId,
                   Map<PlayerId, String> playerNames) {
      graphicalPlayer = new GraphicalPlayer(ownId, playerNames, trick, score, hand, queu);
      Platform.runLater(() -> { graphicalPlayer.createStage().show(); });
    }
    
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(()->{hand.setHand(newHand);});
    }
    
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(()->{trick.setTrump(trump);});
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(()->{trick.setTrick(newTrick);});
    }
    
    @Override
    public void updateScore(Score score) {
        Platform.runLater(()->{
            for(TeamId id : TeamId.ALL) {
                this.score.setTurnPoints(id, score.turnPoints(id));
                this.score.setTotalPoints(id, score.totalPoints(id));
                this.score.setGamePoints(id, score.gamePoints(id));
            } 
        });
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(()->{score.setWinningTeam(winningTeam);});
    }
    

}
