package ch.epfl.javass.simulation;
import java.util.Map;

import ch.epfl.javass.jass.*;

public class WinningPlayer implements Player {
    private Player underlying;
    private boolean hasWin=false;
    private PlayerId ownId;
    private Score score;
    
    public WinningPlayer(Player underlying, PlayerId ownId) {
        this.underlying=underlying;
        this.ownId=ownId;
    }
    @Override
    public void setPlayers(PlayerId ownId,
            Map<PlayerId, String> playerNames) {
        hasWin=false;
    }
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        return underlying.cardToPlay(state, hand);
  
    }
    
    @Override
    public void  setWinningTeam(TeamId winningTeam) {
        if(ownId.team()==winningTeam)
            hasWin=true;
        System.out.println(score);
    }
    
    @Override
    public  void updateScore(Score score) {
        this.score=score;
    }

    
    public boolean hasWin() {
        return hasWin;
    }

}
