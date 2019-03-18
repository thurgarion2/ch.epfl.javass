package ch.epfl.javass.jass;

import java.util.SplittableRandom;

public class MctsPlayer implements Player {
    
    private PlayerId ownId;
    private SplittableRandom rng;
    private int iterations;
    private static int C=40;
    
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        this.ownId=ownId;
        this.rng=new SplittableRandom(rngSeed);
        this.iterations=iterations;
        
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // TODO Auto-generated method stub
        return null;
    }

    
    private class Node{
        private Node parent;
        private TurnState state;
        
        private CardSet unPlayedOther;
        private CardSet unPlayedPlayer;
        
  
        private int nbChildren;
        private PlayerId id;
        
        int totPoints;
        
        public Node(TurnState state, CardSet unPlayedOther, CardSet unPlayedPlayer) {
            
        }
        
        public Node (TurnState state, CardSet unPlayedOther, CardSet unPlayedPlayer, PlayerId id, Node parent) {
            this.parent=parent;
            this.state=state;
            this.unPlayedOther=unPlayedOther;
            this.unPlayedPlayer=unPlayedPlayer;
            this.id=id;
            nbChildren=1;
            totPoints=scoreOfSimulateTurn();
        }
        
        public Node nextChildren() {
            CardSet playable = state.trick().playableCards(unPlayedPlayer);
        }
        
        public double scoreforRanking() {
            if(nbChildren==0) {
                return Double.MAX_VALUE;
            }
            double ratio=(double)totPoints/(double)nbChildren;
            double propagation=2*Math.log(nbChildren)/(double)nbChildren;
            return ratio + C*Math.sqrt(propagation);
        }
        
        public void update(int points, TeamId team) {
            if(id.team()==team) {
                totPoints+=points;
            }else {
                //dans le cas d'un match
                totPoints+=Math.max(0, 157-points);
            }
            nbChildren++;
        }
        
        private int scoreOfSimulateTurn() {
           TurnState simulate=state;
           CardSet player=this.unPlayedPlayer;
           CardSet other=this.unPlayedOther;
           
           while(!simulate.isTerminal()) {
               if(simulate.nextPlayer()==id) {
                   CardSet playable = simulate.trick().playableCards(player);
                   Card c=playable.get(rng.nextInt(playable.size()));
                   player=player.remove(c);
                   simulate=simulate.withNewCardPlayedAndTrickCollected(c);
               }else {
                   CardSet playable = simulate.trick().playableCards(other);
                   Card c=playable.get(rng.nextInt(playable.size()));
                   other=other.remove(c);
                   simulate=simulate.withNewCardPlayedAndTrickCollected(c);
               }
           }
           return simulate.score().turnPoints(id.team());
        }
    }
}
