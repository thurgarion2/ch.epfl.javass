package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * un joueur qui utilse un arbre de montecarlo pour choisir la carte à jouer
 * 
 * @author erwan serandour (296100)
 *
 */
public final class MctsPlayer implements Player {
    private PlayerId ownId;
    private SplittableRandom rng;
    private int iterations;
    private static int C = 40;
    /**
     * @param ownId
     *            l'id du joueur
     * @param rngSeed
     *            la graine de départ
     * @param iterations
     *            le nombre d'iterations maximum (>=9)
     * @throws IllegalArgumentException
     *             si le nombre d'iterations inferieur à 9
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node root = new Node(state, hand.packed(),ownId.team().other(), ownId);
        for (int i = 0; i < iterations; i++) {
            List<Node> nodes = root.nextNode(ownId);
            //teste si toutes les possibilitées ont étées explorées
            Node ajoute=nodes.get(nodes.size()-1);
            int finTour=ajoute.randomGame(rng, ownId);
            for(Node n : nodes) {
                n.update(ajoute, finTour);
            }
        }
        return root.bestCardFromChildren();
    }

    private static class Node {
        // null si la racine
        private Node[] children;
        // nombre d'enfant directe
        private int size;

        private TurnState state;
        // représante la main du joueur ownId
        private long handPlayer;
        private long unPlayed;

        //id de la dernière équipe à avoir joué
        private TeamId id;

        private int S;
        private int N;

        public Node(TurnState state, long handPlayer, TeamId last, PlayerId ownId) {
            this.state = state;
            this.handPlayer = handPlayer;
            this.id=last;
            unPlayed = playable(state,handPlayer,state.nextPlayer()==ownId);
            children = new Node[PackedCardSet.size(unPlayed)];
            S = 0;
            N = 0;
            size = 0;
        }

        public List<Node> nextNode(PlayerId ownId) {
            List<Node> nodes=mostPromisingNode(new ArrayList());
            nodes.get(nodes.size()-1).addNextNode(ownId,nodes); 
            return nodes;
        }
        
        private void addNextNode(PlayerId ownId, List<Node> current) {
            if(PackedCardSet.isEmpty(unPlayed)) {
                return;
            }
            //prend la première carte non jouée
            int c = PackedCardSet.get(unPlayed, 0);
            unPlayed = PackedCardSet.remove(unPlayed, c);
            TurnState update = state.withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            Node next = new Node(update, PackedCardSet.remove(handPlayer, c),state.nextPlayer().team(), ownId);
            children[size] = next;
            size++;
            current.add(next);
        }
     
        private double score(int C, Node parent) {
            double s=S;
            double n=N;

            //teste si racine
            if (N == 0) {
                return Double.POSITIVE_INFINITY;
            }
            return s/n + C * Math.sqrt(2.0 * Math.log(parent.N) / n);
        }
        
        //toutes les cartes non jouées 
        private long playable(TurnState state, long handPlayer, boolean isOwnId) {
            long currentHand =isOwnId ? handPlayer:handOther(state,handPlayer );
            int trick = state.packedTrick();
            return PackedTrick.playableCards(trick,currentHand);
        }

        private long handOther(TurnState state, long handPlayer) {
            long unplayedTurn = state.packedUnplayedCards();
            return PackedCardSet.difference(unplayedTurn, handPlayer);
        }
        
        //null si pas d'enfant
        private Node bestChild(int C) {
            int max = 0;
            for (int i = 1; i < size; i++) {
                Node child=children[i];
                if (child.score(C,this) > children[max].score(C,this)) {
                    max = i;
                }
            }
            return children[max];
        }        
        private List<Node> mostPromisingNode(List<Node> current) {
            if(current.isEmpty()) {
                current.add(this);
                return !PackedCardSet.isEmpty(unPlayed) ? current : bestChild(C).mostPromisingNode(current);
            }
            double score=this.score(C,current.get(current.size()-1));
            current.add(this);
            Node bestChild=bestChild(C);
            //pas d'enfants
            if(bestChild==null) {return current;}
            //le noeud actuel est meilleur que ses enfants
            return score>=bestChild.score(C,this) ? current:bestChild.mostPromisingNode(current);
        }
        
        public Card bestCardFromChildren() {
            return bestChild(0).cardPlayed(this);
        }
        
        private Card cardPlayed(Node parent) {
            CardSet diff = parent.state.unplayedCards().difference(state.unplayedCards());
            return diff.get(0);
        }
             
        public void update(Node ajoute, int points) {
            //si un match
            S += ajoute.id == id ? points : Math.max(0, 157 - points);
            N++;
        }
        public int randomGame(SplittableRandom rng, PlayerId ownId) {
            //déjà simulé
            if(N!=0) {
                return (int) S/N;
            }
            TurnState simulate = state;
            long hdPlayer = handPlayer;
            while (!simulate.isTerminal()) {
                long playable = playable(simulate, hdPlayer,simulate.nextPlayer()==ownId);
                int size = PackedCardSet.size(playable);
                int c = PackedCardSet.get(playable, rng.nextInt(size));
                // on peut sans problème supprimer une carte qui n'existe pas
                hdPlayer = PackedCardSet.remove(hdPlayer, c);
                simulate = simulate
                        .withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            }
            return simulate.score().turnPoints(id);
        } 
    }
}
