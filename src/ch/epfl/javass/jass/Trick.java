package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * 
 * @author Jean-Daniel Rouveyrol (301480)
 *
 */
public final class Trick {
    /**
     * plis invalide - constante
     */
    public static final Trick INVALID = new Trick(PackedTrick.INVALID);

    private final int pkTrick;

    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    /**
     * créer un pli vide, initaliser avec l'atout et le premier joueur
     * 
     * @param trump
     * 
     * @param firstPlayer
     * 
     * @return un nouveau plis vide sachant l'atout et le premier joueur de type
     *         Trick
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * créer un Trick à partir d'un pli empaqueté
     * 
     * @param packed - pli empaqueté
     * 
     * @return un nouveau Trick 
     * 
     * @throws IllegalArgumentException si le plis empaqueté en paramètre est invalide
     */
    public static Trick ofPacked(int packed) throws IllegalArgumentException {
        if (!PackedTrick.isValid(packed)) {
            throw new IllegalArgumentException();
        }
        return new Trick(packed);
    }

    /**
     * @return la version empaquetée d'un Trick
     */
    public int packed() {
        return this.pkTrick;
    }

    /**
     * @return le pli vide suivant celui-ci sachant l'atout et le vainqueur du
     *         pli (le premier joueur du prochain pli)
     *         
     * @throws IllegalStateException si le pli donné n'est pas plein
     */
    public Trick nextEmpty() throws IllegalStateException {
        if (!this.isFull()) {
            throw new IllegalStateException();
        }
        return new Trick(PackedTrick.nextEmpty(this.pkTrick));
    }

    /**
     * @return vrai si le pli est vide, 
     * 		   faux sinon
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(this.pkTrick);
    }

    /**
     * @return vrai si le pli est plein, 
     * 		   faux sinon
     */
    public boolean isFull() {
        return PackedTrick.isFull(this.pkTrick);
    }

    /**
     * @return vrai si le pli est le dernier pli du tour courant, 
     * 		   faux sinon
     */
    public boolean isLast() {
        return PackedTrick.isLast(this.pkTrick);
    }

    /**
     * @return le nombre de carte que le plis contient (la taille du pli)
     */
    public int size() {
        return PackedTrick.size(this.pkTrick);
    }

    /**
     * @return la couleur (Card.Color) de l'atout du pli
     */
    public Card.Color trump() {
        return PackedTrick.trump(this.pkTrick);
    }

    /**
     * @return l'index du pli
     */
    public int index() {
        return PackedTrick.index(this.pkTrick);
    }

    /**
     * donne l'identifiant du joueur à l'index donné
     * 
     * @param index - "position" du joueur dans le pli
     * 
     * @return le joueur d'index donné 
     * 
     * @throws IndexOutOfBoundsException si l'index est invalide (index vlaide -> 0 <= index < 4)
     */
    public PlayerId player(int index) throws IndexOutOfBoundsException {
        Preconditions.checkIndex(index,4);
        return PackedTrick.player(this.pkTrick, index);
    }

    /**
     * donne la carte de l'index donné
     * 
     * @param index - position de la carte donné dans le pli
     * 
     * @return la version empaquetée de la carte du pli à l'index donné (supposée avoir été posée)
     *         
     * @throws IndexOutOfBoundsException si l index est invalide (index valide -> 0 <= index < taille du pli)
     */
    public Card card(int index) throws IndexOutOfBoundsException {
        Preconditions.checkIndex(index,this.size());
        return Card.ofPacked(PackedTrick.card(this.pkTrick, index));
    }

    /**
     * ajoute une carte à un pli
     * 
     * @param Card c - carte à ajouter 
     * 
     * @return retourne un pli identique à celui donné, mais auquel la carte donnée a été ajouté
     *         
     * @throws IllegalStateException si le pli est plein
     */
    public Trick withAddedCard(Card c) throws IllegalStateException {
        if (this.isFull()) {
            throw new IllegalStateException();
        }
        return new Trick(PackedTrick.withAddedCard(this.pkTrick, c.packed()));
    }

    /**
     * donne la couleur demandée dans le pli
     * 
     * @return la couleur de base du pli, c-à-d la couleur de sa première carte
     * 
     * @throws IllegalStateException si le pli est vide
     */
    public Card.Color baseColor() throws IllegalStateException {
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.baseColor(this.pkTrick);
    }

    /**
     * détermine l'enemble de carte jouable par un joueur étant donné sa main
     * 
     * @param CardSet hand - main d'un joueur;
     * 
     * @return le sous-ensemble des cartes de la main hand qui peuvent être jouées comme prochaine carte du pli
     * 
     * @throws IllegalStateException si le pli est plein
     */
    public CardSet playableCards(CardSet hand) throws IllegalStateException {
        if (this.isFull()) {
            throw new IllegalStateException();
        }
        return CardSet.ofPacked(
                PackedTrick.playableCards(this.pkTrick, hand.packed()));
    }

    /**
     * @return le nombre de points du pli, en tenant compte des « 5 de der »
     */
    public int points() {
        return PackedTrick.points(this.pkTrick);
    }

    /**
     * détermine le joueur étant entrain de gagner  
     * 
     * @return l'identifiant du joueur menant le pli
     * 
     * @throws IllegalStateException si le pli est vide
     */
    public PlayerId winningPlayer() throws IllegalStateException {
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.winningPlayer(this.pkTrick);
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO == null) {
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
        return PackedTrick.toString(this.packed());
    }
}
