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

        Node root = new Node(state, hand.packed(),ownId.team().other(), ownId, null);
        for (int i = 0; i < iterations; i++) {
            Node mostPromising = root.mostPromisingNode(C);
            //teste si toutes les possibilitées ont étées explorées
            Node next = mostPromising.nextChildren( ownId);
            next.genrerateTurnWithUpdateTree(rng, ownId);

        }
        return root.bestCardFromChildren();
    }

    private static class Node {
        // null si la racine
        private Node parent;
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

        public Node(TurnState state, long handPlayer, TeamId last, PlayerId ownId, Node parent) {
            this.parent = parent;
            this.state = state;
            this.handPlayer = handPlayer;
            this.id=last;
            unPlayed = playable(state,handPlayer,state.nextPlayer()==ownId);

            children = new Node[PackedCardSet.size(unPlayed)];
            S = 0;
            N = 0;
            size = 0;
        }

        public Node nextChildren(PlayerId ownId) {
            //test si toutes les cartes ont été jouées
            if(PackedCardSet.isEmpty(unPlayed)) {
                return this;
            }
            //prend la première carte non jouée
            int c = PackedCardSet.get(unPlayed, 0);
            unPlayed = PackedCardSet.remove(unPlayed, c);
            TurnState update = state.withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            Node next = new Node(update, PackedCardSet.remove(handPlayer, c),state.nextPlayer().team(), ownId,this);
            children[size] = next;
            size++;
            return next;
        }
     
        private double score(int C) {
            double s=S;
            double n=N;

            //teste si racine
            if(parent==null) {
                //tant que tout les enfants de la racines n'ont pas été ajouté
                return PackedCardSet.isEmpty(unPlayed) ? s/n : Double.POSITIVE_INFINITY;
            }
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
        
        public Node mostPromisingNode(int C) {
            Node best = this;
            for (int i = 0; i < size; i++) {
                Node child = children[i];
                best = best.score(C) < child.score(C) ? child : best;
            }
            //le noeud actuell est le meilleur
            if(best==this) {
                return best;
            }
            //meilleur noeud
            return best.mostPromisingNode(C);
        }
        
        private Card playedCard() {
            if(parent==null) {
                return null;
            }
            long played=PackedCardSet.difference(parent.state.packedUnplayedCards(),state.packedUnplayedCards());
            return Card.ofPacked(PackedCardSet.get(played, 0));
        }

        public Card bestCardFromChildren() {
            int max = 0;
            for (int i = 1; i < size; i++) {
                if (children[i].score(0) > children[max].score(0)) {
                    max = i;
                }
            }
            return children[max].playedCard();
        }

        public void genrerateTurnWithUpdateTree(SplittableRandom rng, PlayerId ownId) {
            //test si ajout du noeud
            if(N==0) {
                int s=randomGame(rng, ownId);
                update(s, id);   
                return;
            }
            update((int)this.score(0), id);   
        }

        private void update(int points, TeamId team) {
            // teste si match 257 points
            S += team == id ? points : Math.max(0, 157 - points);
            N++;
            if (parent != null) {
                parent.update(points, team);
            }
          
        }

        private int randomGame(SplittableRandom rng, PlayerId ownId) {
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
