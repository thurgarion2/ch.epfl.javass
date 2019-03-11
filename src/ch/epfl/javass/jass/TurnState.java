package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

public final class TurnState {
    
    private long score;
    private long unplayedCards;
    private int trick;
    
    private TurnState(long score,long unplayedCards, int trick) {
        this.score=score;
        this.unplayedCards=unplayedCards;
        this.trick=trick;
    }
    
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        int pkTrick = PackedTrick.firstEmpty(trump, firstPlayer);
        return new TurnState(score.packed(),PackedCardSet.ALL_CARDS, pkTrick);
    }
    
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        if(!PackedScore.isValid(pkScore)||!PackedCardSet.isValid(pkUnplayedCards)||!PackedTrick.isValid(pkTrick)) {
            throw new IllegalArgumentException();
        }
        return new TurnState( pkScore, pkUnplayedCards,pkTrick);
    }
    
    
    public long packedScore() {
        return score;
    }

    public long packedUnplayedCards() {
        return unplayedCards;
    }

    public int packedTrick() {
        return trick;
    }
    
    public Score score() {
        return Score.ofPacked(score);
    }

    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    public Trick trick() {
        return Trick.ofPacked(trick);
    }
    
    public boolean isTerminal() {
        return PackedTrick.isLast(trick)&&PackedTrick.isFull(trick);
    }
    
    public PlayerId nextPlayer() {
        if(PackedTrick.isFull(trick)) {
            throw new IllegalStateException();
        }
        return PackedTrick.player(trick, PackedTrick.size(trick));
    }
   
}
