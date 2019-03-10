package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

public final class Trick {
	public static final Trick INVALID =ofPacked(PackedTrick.INVALID);
	
	private final int pkTrick;
	
	private Trick(int pkTrick) {
		this.pkTrick = pkTrick;
	}
	
	public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
		return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
	}
	
	public static Trick ofPacked(int packed) throws IllegalArgumentException {
		if(!PackedTrick.isValid(packed)) {
			throw new IllegalArgumentException();
		}
		return new Trick(packed);
	}
	
	public int packed() {
		return this.packed();
	}
	
	public Trick nextEmpty() throws IllegalStateException {
		if(!this.isFull()) {
			throw new IllegalStateException();
		}
		return new Trick(PackedTrick.nextEmpty(this.pkTrick));
	}
	
	public boolean isEmpty() {
		return PackedTrick.isEmpty(this.pkTrick);
	}
	
	public boolean isFull() {
		return PackedTrick.isFull(this.pkTrick);
	}
	
	public boolean isLast() {
		return PackedTrick.isLast(this.pkTrick);
	}
	
	public int size() {
		return PackedTrick.size(this.pkTrick);
	}
	
	public Color trump() {
		return PackedTrick.trump(this.pkTrick);
	}
	
	public int index() {
		return PackedTrick.index(this.pkTrick);
	}
	
	public PlayerId player(int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= 4) {
			throw new IndexOutOfBoundsException();
		}
		return PackedTrick.player(this.pkTrick, index);
	}
	
	public Card card(int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
		return Card.ofPacked(PackedTrick.card(this.pkTrick, index));
	}
	
	public Trick withAddedCard(Card c) throws IllegalStateException{
		if(this.isFull()) {
			throw new IllegalStateException();
		}
		return new Trick(PackedTrick.withAddedCard(this.pkTrick, c.packed()));
	}
	
	public Color baseColor() throws IllegalStateException{
		if(this.isEmpty()) {
			throw new IllegalStateException();
		}
		return PackedTrick.baseColor(this.pkTrick);
	}
	
	public CardSet playableCards(CardSet hand) throws IllegalStateException{
		if(this.isFull()) {
			throw new IllegalStateException();
		}
		return CardSet.ofPacked(PackedTrick.playableCards(this.pkTrick, hand.packed()));
	}
	
	public int points() {
		return PackedTrick.points(this.pkTrick);
	}
	
	public PlayerId winningPlayer() throws IllegalStateException{
		if(this.isEmpty()) {
			throw new IllegalStateException();
		}
		return PackedTrick.winningPlayer(this.pkTrick);
	}
	
	@Override
    public boolean equals(Object thatO) {
        if(thatO==null) {
            return false;
        }
        if (thatO.getClass() != this.getClass()) {
            return false;
        }
        Trick other = (Trick) thatO;
        return this.pkTrick == other.pkTrick;
    }

    @Override
    public int hashCode() {
        return this.packed();
    }

    @Override
    public String toString() {
        return PackedCard.toString(this.packed());
    }
}
































