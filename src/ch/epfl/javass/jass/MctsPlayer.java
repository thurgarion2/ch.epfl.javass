package ch.epfl.javass.jass;

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
     * constructeur
     * 
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
        Node root = new Node(state, hand.packed(), null);
        root.addAllPossibleChildren();
        for (int i = 1; i < iterations; i++) {
            Node mostPromising = root.mostPromisingNode();
            //teste si toutes les possibilitées ont étées explorées
            Node next = mostPromising.nextChildren();
            next.genrerateTurnWithUpdateTree();
        
        }
        return root.bestCardFromChildren();
    }

    private class Node {
        // null si la racine
        private Node parent;
        private Node[] children;
        // nombre d'enfant directe
        private int size;

        private TurnState state;
        // représante la main du joueur ownId
        private long handPlayer;
        private long unPlayed;

        // id of next Player to play
        private PlayerId id;

        private int S;
        private int N;

        public Node(TurnState state, long handPlayer, Node parent) {
            this.parent = parent;
            this.state = state;
            this.handPlayer = handPlayer;
            this.id = state.nextPlayer();
            // en fonction de ownId, si c'est lui qui joue où un autre joueur
            long handCurrent = ownId == id ? handPlayer
                    : handOther(state, handPlayer);
            unPlayed = playable(state, handCurrent);
            children = new Node[PackedCardSet.size(unPlayed)];
            S = 0;
            N = 0;
            size = 0;
        }

        public Node nextChildren() {
            //test si toutes les cartes ont été jouées
            if(PackedCardSet.isEmpty(unPlayed)) {
                return this;
            }
            //prend la première carte non jouée
            int c = PackedCardSet.get(unPlayed, 0);
            unPlayed = PackedCardSet.remove(unPlayed, c);
            TurnState update = state
                    .withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            Node next = new Node(update, PackedCardSet.remove(handPlayer, c),
                    this);
            children[size] = next;
            size++;
            return next;
        }

        private double value() {
            return N == 0 ? Double.POSITIVE_INFINITY : S / (double) N;
        }
        
        public double score() {
            //teste si racine
            if(parent==null) {
                return Double.NEGATIVE_INFINITY;
            }
            if (N == 0) {
                return Double.POSITIVE_INFINITY;
            }
            return value() + C * Math.sqrt(2.0 * Math.log(parent.N) / (double) N);
        }

        private long playable(TurnState state, long hand) {
            int trick = state.packedTrick();
            return PackedTrick.playableCards(trick, hand);
        }

        private long handOther(TurnState state, long handPlayer) {
            long unplayedTurn = state.packedUnplayedCards();
            return PackedCardSet.difference(unplayedTurn, handPlayer);
        }
        
        public void addAllPossibleChildren() {
            while(!PackedCardSet.isEmpty(unPlayed)) {
                Node nextChild=this.nextChildren();
                nextChild.genrerateTurnWithUpdateTree();
            }
        }
        
        public Node mostPromisingNode() {
            Node best = this;
            for (int i = 0; i < size; i++) {
                Node child = children[i];
                best = best.score() < child.score() ? child : best;
            }
            //meilleur noeud
            if(best==this) {
                return this;
            }
            return best.mostPromisingNode();
        }

        public Card bestCardFromChildren() {
            int max = 0;
            System.out.println(children[0].value()+" "+children[0].N);
            for (int i = 1; i < size; i++) {
                System.out.println(children[i].value()+" "+children[i].N);
                if (children[i].value() > children[max].value()) {
                    max = i;
                }
            }
            long handChild = children[max].handPlayer;
            long play = PackedCardSet.difference(handPlayer, handChild);
            return Card.ofPacked(PackedCardSet.get(play, 0));
        }

        public void genrerateTurnWithUpdateTree() {
            //test si toutes les cartes ont été jouées
            if(PackedCardSet.isEmpty(unPlayed)) {
                update((int)this.value(), id.team());   
                return;
            }
            int score = randomGame();
        
            update(score, id.team());   
        }

        private void update(int points, TeamId team) {
            // teste si match 257 points
            S += team == id.team().other() ? points : Math.max(0, 157 - points);
            N++;
            if (parent != null) {
                parent.update(points, team);
            }
          
        }

        private int randomGame() {
            TurnState simulate = state;
            long hdPlayer = handPlayer;
            while (!simulate.isTerminal()) {
                long cards = ownId == simulate.nextPlayer() ? hdPlayer
                        : handOther(simulate, hdPlayer);
                long playable = playable(simulate, cards);
                int size = PackedCardSet.size(playable);
                int c = PackedCardSet.get(playable, rng.nextInt(size));
                // on peut sans problème supprimer une carte qui n'existe pas
                hdPlayer = PackedCardSet.remove(hdPlayer, c);
                simulate = simulate
                        .withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            }
            return simulate.score().turnPoints(id.team());
        }
        
        
    }
}
