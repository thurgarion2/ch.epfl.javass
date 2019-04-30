package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * l'état d'un tour de jeu
 * 
 * @author erwan serandour (296100)
 *
 */

public final class TurnState {

    private final long score;
    private final long unplayedCards;
    private final int trick;

    private TurnState(long score, long unplayedCards, int trick) {
        this.score = score;
        this.unplayedCards = unplayedCards;
        this.trick = trick;
    }

    /**
     * retourne l'état initial correspondant à un tour de jeu dont l'atout, le
     * score initial et le joueur initial sont ceux donnés
     * 
     * @param trump
     *            l'atout initiale
     * @param score
     *            le score initiale
     * @param firstPlayer
     *            le joueur initinale
     * @return l'état initial d'un tour de jeu
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        int pkTrick = PackedTrick.firstEmpty(trump, firstPlayer);
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, pkTrick);
    }

    /**
     * retourne l'état d'un tour dont les composantes (empaquetées) sont celles
     * données
     * 
     * @param pkScore
     *            le score courant empqueté
     * @param pkUnplayedCards
     *            l'ensemble des cartes non jouées
     * @param pkTrick
     *            le pli courant
     * @return l'état dont les composantes (empaquetées) sont celles données
     * @throws IllegalArgumentException
     *             si pkScore ou pkUnplayedCards ou pkTrick ne représentent pas
     *             score ou un set de cartes ou pli empaqeté valide
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore)
                && PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * retourne la version empaquetée du score courant
     * 
     * @return la version empaquetée du score courant
     */
    public long packedScore() {
        return score;
    }

    /**
     * retourne la version empaquetée des cartes non jouées
     * 
     * @return la version empaquetée des cartes non jouées
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }

    /**
     * retourne la version empaquetée du pli courant
     * 
     * @return la version empaquetée du pli courant
     */
    public int packedTrick() {
        return trick;
    }

    /**
     * retourne le score courant
     * 
     * @return le score courant
     */
    public Score score() {
        return Score.ofPacked(score);
    }

    /**
     * retourne l'ensemble des cartes pas encore jouées
     * 
     * @return l'ensemble des cartes pas encore jouées
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    /**
     * retourne le pli courant
     * 
     * @return le pli courant
     */
    public Trick trick() {
        return Trick.ofPacked(trick);
    }

    /**
     * retourne vrai ssi l'état est terminal, c-à-d si le dernier pli du tour a
     * été joué
     * 
     * @return vrai ssi l'état est terminal, c-à-d si le dernier pli du tour a
     *         été joué
     */
    public boolean isTerminal() {
        return PackedTrick.INVALID == trick;
    }

    /**
     * retourne l'identité du joueur devant jouer la prochaine carte
     * 
     * @return l'identité du joueur devant jouer la prochaine carte
     * @throws IllegalStateException
     *             si le pli courant est plein
     */
    public PlayerId nextPlayer() {
        if (PackedTrick.isFull(trick)) {
            throw new IllegalStateException();
        }
        return PackedTrick.player(trick, PackedTrick.size(trick));
    }

    /**
     * retourne l'état correspondant après que le prochain joueur aie joué la
     * carte donnée
     * 
     * @param card
     *            la carte jouée
     * @return l'état correspondant après que le prochain joueur ait joué la
     *         carte donnée
     * @throws IllegalStateException
     *             si le pli courant est plein
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(trick)) {
            throw new IllegalStateException();
        }
        long nextUnplayedCards = PackedCardSet.remove(unplayedCards,
                card.packed());
        return new TurnState(score, nextUnplayedCards,
                PackedTrick.withAddedCard(trick, card.packed()));
    }

    /**
     * retourne l'état correspondant après que le pli courant ait été ramassé
     * 
     * @return l'état correspondant après que le pli courant aie été ramassé
     * @throws IllegalStateException
     *             si le pli courant n'est pas plein
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(trick)) {
            throw new IllegalStateException();
        }
        TeamId winning = PackedTrick.winningPlayer(trick).team();
        long nextScore = PackedScore.withAdditionalTrick(score, winning,
                PackedTrick.points(trick));
        return new TurnState(nextScore, unplayedCards,
                PackedTrick.nextEmpty(trick));
    }

    /**
     * retourne l'état correspondant après que le prochain joueur ait joué la
     * carte donnée et ramassé si alors plein
     * 
     * @param card
     *            la carte jouée
     * @return l'état correspondant après que le prochain joueur ait joué la
     *         carte donnée et ramassé si alors plein
     * @throws IllegalStateException
     *             si le pli courant est plein
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        TurnState next = withNewCardPlayed(card);
        return PackedTrick.isFull(next.trick) ? next.withTrickCollected() : next;
    }

}
