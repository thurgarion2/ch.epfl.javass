package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

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
     *             si le nombre d'iterations est inferieur à 9
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= 9);
        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node root = new Node(state, hand.packed(), ownId.team().other(), ownId);
        for (int i = 0; i < iterations; i++) {
            // le chemin de la racine au noeud ajouté
            List<Node> nodes = root.pathToNewNode(ownId);
            Node ajoute = nodes.get(nodes.size() - 1);
            int finTour = ajoute.randomGame(rng, ownId);
            for (Node n : nodes) {
                n.update(ajoute, finTour);
            }
        }
        return root.bestCardFromChildren();
    }

    private static class Node {
        private final static int NB_POINTS_PER_TURN = 157;
        
        

        private final TurnState state;
        private final Node[] children;
        // nombre d'enfant directe
        private int size;

       
        // représante la main du joueur ownId
        private long handPlayer;
        private long unPlayed;

        // id de la dernière équipe à avoir joué
        private TeamId id;

        private int S;
        private int N;
        
        

        // toutes les cartes non jouées de la main corresponsantes
        private static long playable(TurnState state, long handPlayer,
                PlayerId ownId) {
            long currentHand = state.nextPlayer() == ownId ? handPlayer
                    : handOther(state, handPlayer);
            int trick = state.packedTrick();
            return PackedTrick.playableCards(trick, currentHand);
        }

        // les cartes des joueurs sauf celui représenter par cette instance
        private static long handOther(TurnState state, long handPlayer) {
            long unplayedTurn = state.packedUnplayedCards();
            return PackedCardSet.difference(unplayedTurn, handPlayer);
        }

        private Node(TurnState state, long handPlayer, TeamId last, PlayerId ownId) {
            this.state = state;
            this.handPlayer = handPlayer;
            this.id = last;
            unPlayed = state.isTerminal() ? PackedCardSet.EMPTY
                    : playable(state, handPlayer, ownId);
            children = new Node[PackedCardSet.size(unPlayed)];
            S = 0;
            N = 0;
            size = 0;
        }
        
    
        private List<Node> pathToNewNode(PlayerId ownId) {
            List<Node> nodes = new ArrayList();
            // le noeud à partir du quel on l'appelle est considérer comme
            // racine
            nodes.add(this);
            this.pathToMostPromisingNode(nodes);
            // on ajoute un enfant au dernier noeud
            nodes.get(nodes.size() - 1).addNextNode(ownId, nodes);
            return nodes;
        }

        // le chemin de la racine au noeud le plus prométeur
        private List<Node> pathToMostPromisingNode(List<Node> current) {
            boolean allChidrenAdded=PackedCardSet.isEmpty(unPlayed);
            if (! allChidrenAdded || state.isTerminal()) {
                return current;
            }
            // le noeud actuel est plein
            Node next = bestChild(C);
            current.add(next);
            return next.pathToMostPromisingNode(current);
        }
        
        private double score(int C, Node parent) {
            double s = S;
            double n = N;
            if (N == 0) {
                return Double.POSITIVE_INFINITY;
            }
            double explore=Math.sqrt(2.0 * Math.log(parent.N) / n);
            return s / n + C * explore;
        }

        private Node bestChild(int C) {
            int max = 0;
            for (int i = 1; i < size; i++) {
                Node child = children[i];
                max = child.score(C, this) > children[max].score(C, this) ? i : max;
            }
            return children[max];
        }


        private void addNextNode(PlayerId ownId, List<Node> current) {
            // le noeud est terminal
            if (state.isTerminal()) {
                return;
            }
            // prend la première carte non jouée
            int c = PackedCardSet.get(unPlayed, 0);
            unPlayed = PackedCardSet.remove(unPlayed, c);
            TurnState update = state
                    .withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            Node next = new Node(update,
                    PackedCardSet.remove(handPlayer, c),
                    state.nextPlayer().team(), ownId);
            children[size] = next;
            size++;
            current.add(next);
        }
        
        
        private Card bestCardFromChildren() {
            return bestChild(0).cardPlayed(this);
        }

        private Card cardPlayed(Node parent) {
            CardSet diff = parent.state.unplayedCards()
                    .difference(state.unplayedCards());
            return diff.get(0);
        }

        private void update(Node ajoute, int points) {
            // si un match
            S += ajoute.id == id ? points
                    : Math.max(0, NB_POINTS_PER_TURN - points);
            N++;
        }
        //simule une fin de partie aléatoire
        private int randomGame(SplittableRandom rng, PlayerId ownId) {
            TurnState simulate = state;
            long hdPlayer = handPlayer;
            while (!simulate.isTerminal()) {
                long playable = playable(simulate, hdPlayer, ownId);
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
