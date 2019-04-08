package ch.epfl.javass.jass;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.simulation.RandomPlayer;

class DelayPlayer extends RandomPlayer{
    private double timeToPlay;
    public DelayPlayer(long rngSeed, double timeToPlay) {
        super(rngSeed);
        this.timeToPlay=timeToPlay;
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        try{
            Thread.sleep((long) (this.timeToPlay*1000));
        } catch (InterruptedException e) {}
        return super.cardToPlay(state, hand);
    }
    
}
public class PacedPlayerTest {
   private CardSet hand=CardSet.ALL_CARDS;
   private TurnState state=TurnState.initial(Card.Color.CLUB,Score.INITIAL,PlayerId.PLAYER_1);
   
   @Test
   void playNotToFast(){
       Player player= new PacedPlayer(new DelayPlayer(10, 0.1),1);
       double initTime = System.currentTimeMillis();
       player.cardToPlay(state, hand);
       assertTrue(System.currentTimeMillis()-initTime>=1000);
       player= new PacedPlayer(new DelayPlayer(10, 1.5),1);
       initTime = System.currentTimeMillis();
       player.cardToPlay(state, hand);
       assertTrue(System.currentTimeMillis()-initTime>=1000);
   }
   
   
}
