package ch.epfl.javass.jass;

import java.util.List;

public class CardSet {
	
	private final long cardSet;
	
	private CardSet(long cardSet) {
		this.cardSet = cardSet;
	}
	
	public static final long EMPTY = 0L;
	public static final long ALL_CARDS = PackedCardSet.ALL_CARDS;
	
	public static CardSet of(List<Card> cards) {
		long cardsSet = EMPTY;
		for(int i =0; i < cards.size(); ++i) {
			PackedCardSet.add(cardsSet, cards.get(i).packed());
		}
		return new CardSet(cardsSet);
	}
	
	public static CardSet ofPacked(long packed) throws IllegalArgumentException{
		if(!PackedCardSet.isValid(packed)) {
			throw new IllegalArgumentException();
		}
		return new CardSet(packed);
	}
	
	public long packed() {
		return this.cardSet;
	}
	
	public boolean isEmpty() {
		return PackedCardSet.isEmpty(cardSet);
	}
	
	public int size() {
		return PackedCardSet.size(cardSet);
	}
	
	public Card get(int index) {
		return Card.ofPacked(PackedCardSet.get(cardSet, index));
	}
	
	public CardSet add(Card card) {
		return new CardSet(PackedCardSet.add(cardSet, card.packed()));
	}
	
	public CardSet remove(Card card) {
		return new CardSet(PackedCardSet.remove(cardSet, card.packed()));
	}
	
	public boolean contains(Card card) {
		return PackedCardSet.contains(cardSet, card.packed());
	}
	
	public CardSet complement() {
		return new CardSet(PackedCardSet.complement(cardSet));
	}
	
	public CardSet union(CardSet that){
		return new CardSet(PackedCardSet.union(cardSet, that.packed()));
	}
	
	public CardSet intersection(CardSet that) {
		return new CardSet(PackedCardSet.intersection(cardSet, that.packed()));
	}
	
	public CardSet difference(CardSet that) {
		return new CardSet(PackedCardSet.difference(cardSet, that.packed()));
	}
	
	public CardSet substeOfColor(Card.Color color) {
		return new CardSet(PackedCardSet.subsetOfColor(cardSet, color));
	}
}
	
	
	
	
