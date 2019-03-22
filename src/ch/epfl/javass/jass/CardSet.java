package ch.epfl.javass.jass;

import java.util.List;

/**
 * modélise un ensmble de carte
 * 
 * @author Jean-Daniel Rouveyrol(301480)
 *
 */
public final class CardSet {
	
	private final long cardSet;
	
	private CardSet(long cardSet) {
		this.cardSet = cardSet;
	}
	
	/**
	 * constante : l'ensemble de cartes vide 
	 */
	public static final CardSet EMPTY =ofPacked(PackedCardSet.EMPTY);
	
	/**
	 * constante : l'ensemble de cartes représentant le jeu complet
	 */
	public static final CardSet ALL_CARDS = ofPacked(PackedCardSet.ALL_CARDS);
	
	/**
	 * créé un ensemble de carte à partir d'une liste de carte;
	 * 
	 * @param List<Card> cards
	 * 
	 * @return CardSet "new" cardsSet
	 */
	public static CardSet of(List<Card> cards) {
		long cardsSet = PackedCardSet.EMPTY;
		for(Card c : cards) {
		    cardsSet=PackedCardSet.add(cardsSet,c.packed());
		}
		return new CardSet(cardsSet);
	}
	
	/**
	 * créé un ensemble de carte à partir d'une version empaquetée
	 * 
	 * @param long packed
	 * 
	 * @return CardSet "new" cardsSet
	 * 
	 * @throws IllegalArgumentException si la version empaquetée passée en argument est invalide 
	 */
	public static CardSet ofPacked(long packed) throws IllegalArgumentException{
		if(!PackedCardSet.isValid(packed)) {
			throw new IllegalArgumentException();
		}
		return new CardSet(packed);
	}
	
	/**
	 * créé une version empaquetée de l'ensemble de carte
	 * 
	 * @return long pkCardSet
	 */
	public long packed() {
		return this.cardSet;
	}
	
	/**
	 * vérifie que l'ensemble de cartes est vide 
	 * 
	 * @return true si l'ensemble de cartes est vide 
	 * 		   faux si l'ensemble de cartes est plein
	 */
	public boolean isEmpty() {
		return PackedCardSet.isEmpty(cardSet);
	}
	
	/**
	 * donne le nombre de cartes dans l'ensemble de cartes ("la taille")
	 * 
	 * @return int size 
	 */
	public int size() {
		return PackedCardSet.size(cardSet);
	}
	
	/**
	 * retourne la version empaquetée de la carte d'index donné de l'ensemble de
     * cartes donné (comme si c'était un ensemble normal)
     * 
	 * @param index
	 * 
	 * @return int index
	 */
	public Card get(int index) {
		return Card.ofPacked(PackedCardSet.get(cardSet, index));
	}
	
	/**
     * retourne l'ensemble de cartes donné auquel la carte donnée a été ajoutée
     * 
     * @param Card card - la carte à ajouter
     * 
     * @return un nouvel ensemble basé sur l'ensemble de cartes donné auquel la carte donnée a été ajoutée
     */
	public CardSet add(Card card) {
		return new CardSet(PackedCardSet.add(cardSet, card.packed()));
	}
	
	/**
	 * enlève la carte donnée à l'ensemble de cartes donné
	 * 
	 * @param Card card - la carte a enlever
	 * 
	 * @return un nouvel ensemble basésur l'ensemble de cartes donné auquel la carte donnéee a été retirée
	 */
	public CardSet remove(Card card) {
		return new CardSet(PackedCardSet.remove(cardSet, card.packed()));
	}
	
	/**
	 * vérifie si une carte appartient à l'ensemble de cartes donné
	 * 
	 * @param Card card - la carte "témoin"
	 * 
	 * @return true si la carte passée en argument appartient à l'enemble 
	 * 		   false sinon
	 */
	public boolean contains(Card card) {
		return PackedCardSet.contains(cardSet, card.packed());
	}
	
	/**
	 * définit l'ensemble de cartes comlémentaire, l'ensemble des cartes qui n'appartiennent pas au premier ensemble
	 *  
	 * @return un nouvel ensemble - complémentaire au premier
	 */
	public CardSet complement() {
		return new CardSet(PackedCardSet.complement(cardSet));
	}
	
	/**
	 * définit l'ensemble de cartes contenant toutes les cartes de deux ensembles 
	 * 
	 * @param CardSet that le deuxième ensemble de cartes
	 * 
	 * @return un nouvel ensemble de cartes qui contient toutes les cartes appartenant aux deux ensembles
	 */
	public CardSet union(CardSet that){
		return new CardSet(PackedCardSet.union(cardSet, that.packed()));
	}
	
	/**
	 * définit l'ensemble de cartes contenant les cartes apparaissant dans deux ensembles 
	 * 
	 * @param CardSet that - le deuxième ensemble de cartes
	 * 
	 * @return un nouvel ensemble de cartes correspondant à l'intersection des deux ensembles 
	 */
	public CardSet intersection(CardSet that) {
		return new CardSet(PackedCardSet.intersection(cardSet, that.packed()));
	}
	
	/**
	 * suprime les éléments de l'ensemble de cartes qui appartiennent à l'enemble passé en argument 
	 * 
	 * @param CardSet that - le deuxième ensemble de cartes 
	 * 
	 * @return un nouvel ensemble de cartes qui est le premier sans les élément de celui passé en argument 
	 */
	public CardSet difference(CardSet that) {
		return new CardSet(PackedCardSet.difference(cardSet, that.packed()));
	}
	
	/**
	 * retourne le sous-ensemble de l'ensemble de cartes donné constitué uniquement des cartes de la couleur donnée
     * 
     * @param Card.Color color - la couleur
     * 
     * @return le sous-ensemble de l'ensemble de cartes donné constitué uniquement des cartes de la couleur donnée
	 */
	public CardSet subsetOfColor(Card.Color color) {
		return new CardSet(PackedCardSet.subsetOfColor(cardSet, color));
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(o.getClass() != this.getClass()) {
			return false;
		}
		CardSet other = (CardSet) o;
		return this.cardSet == other.cardSet;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(cardSet);
	}
	
	@Override
	public String toString() {
		return PackedCardSet.toString(cardSet);
	}
}
	
	
	
	
