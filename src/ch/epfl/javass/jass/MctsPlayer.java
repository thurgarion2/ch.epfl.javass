package ch.epfl.javass.jass;

import java.util.SplittableRandom;

public class MctsPlayer implements Player {
    private PlayerId ownId;
    private SplittableRandom rng;
    private int iterations;
    private Node[] tree;
    
    private static int C=40;
    
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        this.ownId=ownId;
        this.rng=new SplittableRandom(rngSeed);
        this.iterations=iterations;
    }
    
    private Node maxScore(int size) {
        int max=0;
        for(int i=1; i<size; i++) {
            double f1=tree[max].scoreforRanking();
            double f2=tree[i].scoreforRanking();
            max = f1>=f2 ? max : i;
        }
        return tree[max];
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        tree=new Node[iterations+1];
        tree[0] = new Node(state,hand,null);
        for(int i=1; i<iterations; i++) {
          tree[i]=maxScore(i).nextChildren();
          tree[i].genrerateTurnWithUpdateTree();
        }
        return tree[0].maxChildren();
    } 
    
    private class Node{
        private Node parent;
        private TurnState state;

        private CardSet handPlayer;
        private CardSet unPlayed;
        
        private Node[] children;
        private PlayerId id;
        
        private int S;
        private int N;
        
        public Node (TurnState state, CardSet handPlayer, Node parent) {
            this.parent=parent;
            this.state=state;           
            this.handPlayer=handPlayer;
            this.unPlayed=handPlayer;
            this.id=PlayerId.ALL.get((state.nextPlayer().ordinal()+3)%4);
            children= new Node[handPlayer.size()];
            S=0;
            N=0;
        }
        
        public Node nextChildren() {
            CardSet playable = state.trick().playableCards(unPlayed);
            Card c=playable.get(rng.nextInt(playable.size()));
            unPlayed=unPlayed.remove(c);
            int index=handPlayer.size()-unPlayed.size();
            children[index]=new Node(state.withNewCardPlayedAndTrickCollected(c),handPlayer.remove(c),this);
            return children[index];
        }
        
        public Card maxChildren() {
            int size=handPlayer.size()-unPlayed.size();
            int max=0;
            for(int i=1; i<size; i++) {
                if(children[i].value()>children[max].value()) {
                    max=i;
                }
            }
            return handPlayer.difference(children[max].handPlayer).get(0);
        }
        
        public double value() {
            return N==0 ? Double.MAX_VALUE : S/(double)N;
        }
        private void genrerateTurnWithUpdateTree() {
            N++;
            S=randomGame();
            if(parent!=null) {
                update(S, id.team());
            }
        }
        
        public double scoreforRanking() {
            return value() + C*Math.sqrt(2*Math.log(N)/(double)N);
        }
        
        private void update(int points, TeamId team) {
            //teste si match 257 points
            S+= team==id.team() ? points : Math.max(0, 157-points);
            N++;
            if(parent!=null) {
                parent.update(points, team);
            }
        }
              
        private int randomGame() {
           TurnState simulate=state;
           CardSet player=this.handPlayer;
           while(!simulate.isTerminal()) {
               CardSet next = simulate.nextPlayer()==id ? player : state.unplayedCards().difference(player);
               CardSet playable= simulate.trick().playableCards(next);
               Card c=playable.get(rng.nextInt(playable.size()));
               player=player.remove(c);
               simulate=simulate.withNewCardPlayedAndTrickCollected(c);
           }
           return simulate.score().turnPoints(id.team());
        }
    }
}
