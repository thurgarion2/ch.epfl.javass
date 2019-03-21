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
        Node root = new Node (state, hand.packed(),null);
        for(int i=1; i<iterations; i++) {
          Node mostPromising=root.mostPromisingNode();
          if(mostPromising.score()<0) {
              return root.bestCardFromChildren();
          }
          Node next=mostPromising.nextChildren();
          next.genrerateTurnWithUpdateTree();
        }
        return root.bestCardFromChildren();
    } 
    
    private class Node{
        //null si la racine
        private Node parent;
        private Node[] children;
        //nombre d'enfant directe
        private int size;
        
        private TurnState state;
        //représante la main du joueur ownId
        private long handPlayer;
        private long unPlayed;
        
        //id of next Player to play
        private PlayerId id;
        
        private int S;
        private int N;
        
        public Node (TurnState state, long handPlayer, Node parent) {
            this.parent=parent;
            this.state=state;    
            this.handPlayer=handPlayer;
            this.id=state.nextPlayer();
            //en fonction de c'est si ownId qui joue où un autre joueur
            long handCurrent= ownId==id ? handPlayer :handOther(state,handPlayer);
            unPlayed=playable(state, handCurrent);
            children= new Node[PackedCardSet.size(unPlayed)];
            S=0;
            N=0;
            size=0;
        }
        
        public Node nextChildren() {
            int c=PackedCardSet.get(unPlayed,0);
            unPlayed=PackedCardSet.remove(unPlayed,c);
            TurnState update=state.withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            Node next=new Node(update,PackedCardSet.remove(handPlayer,c),this);
            children[size]=next;
            size++;
            return next;
        }
        
        private double value() {
            return N==0 ? Double.MAX_VALUE : S/(double)N;
        }
        
        private long playable(TurnState state, long hand) {
            int trick=state.packedTrick();
            return PackedTrick.playableCards(trick, hand);
        }
        
        private long handOther(TurnState state, long handPlayer) {
            long unplayedTurn = state.packedUnplayedCards();
            return PackedCardSet.difference(unplayedTurn,handPlayer);
        }
        
        public double score() {
            if(PackedCardSet.size(unPlayed)<=0) {
                return -Double.MAX_VALUE;
            }
            if(N==0 || parent==null) {
                return Double.MAX_VALUE;
            }
            return value() + C*Math.sqrt(2*Math.log(parent.N)/(double)N);
        }
               
        public Node mostPromisingNode() {
            Node best=this;
            for(int i=0; i<size; i++) {
                Node child=children[i].mostPromisingNode();
                best = best.score()<child.score() ? child : best;
            }
            return best;
        }
        
        public Card bestCardFromChildren() {
            int max=0;
            for(int i=1; i<size; i++) {
                if(children[i].value()>children[max].value()) {
                    max=i;
                }
            }
            long handChild=children[max].handPlayer;
            long play=PackedCardSet.difference(handPlayer, handChild);
            return Card.ofPacked(PackedCardSet.get(play, 0));
        }
        
        public void genrerateTurnWithUpdateTree() {
            N++;
            S=randomGame();
            if(parent!=null) {
                update(S, id.team());
            }
        }

        private void update(int points, TeamId team) {
            //teste si match 257 points
            S+= team==id.team().other() ? points : Math.max(0, 157-points);
            N++;
            if(parent!=null) {
                parent.update(points, team);
            }
        }
              
        private int randomGame() {
           TurnState simulate=state;
           long hdPlayer=handPlayer;
           while(!simulate.isTerminal()) {
               long cards= ownId==simulate.nextPlayer() ? hdPlayer :handOther(simulate,hdPlayer);
               long playable= playable(simulate, cards);
               int size=PackedCardSet.size(playable);
               int c=PackedCardSet.get(playable, rng.nextInt(size));
               //on peut sans problème supprimer une carte qui n'existe pas
               hdPlayer=PackedCardSet.remove(hdPlayer, c);
               simulate=simulate.withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
           }
           return simulate.score().turnPoints(id.team());
        }
    }
}
