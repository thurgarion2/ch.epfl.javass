package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * pkCard
 * 
 * @author Jean-Daniel Rouveyrol(301480)
 */

public final class PackedCard {
    private PackedCard(){}

    private static final int INVALID = 0b111111;

    /**
     * véfifie que l'entier rprésentant la carte est correctement formé
     * 
     * @param pkCard 
     * 	la carte au format entier 
     * @return boolean
     * 	true, si le format entier de la carte est valide
     * 	false, si le format entier de la carte est invalide
     */
    public static boolean isValid(int pkCard){
        if(Bits32.extract(pkCard, 6, Integer.SIZE-6)!=0)
            return false;
        
        int rank = Bits32.extract(pkCard, 0,4);
        if(rank < 0 || rank >8)
           return false;
       
        return true;
    }

    /**
     * empacte une carte dans le format d'entier en partant du rang 
     * et de la couleur
     * 
     * @param Card.Rank r
     * 	rang de la carte à empacter
     * 
     * @param Card.Color c
     * 	couleur de la carte à empacter
     * 
     * @return int pkCard
     * 	carte au format entier
     */
    public static int pack(Card.Color c, Card.Rank r){
            int indexRank = r.ordinal();
            int indexColor = c.ordinal();
            int pkCard = Bits32.pack(indexRank, 4, indexColor, 2);
            assert isValid(pkCard);
        return pkCard;
    }
    
    private static int colorIndex(int pkCard){
        assert isValid(pkCard);
        return  Bits32.extract(pkCard, 4,2);
    }

    /**
     * donne la couleur d'une carte format entier
     * 
     * @param int pkCard
     * 		carte format entier 
     * @return Card.Color
     * 		couleur de la carte 
     */
    public static Card.Color color(int pkCard){
        assert isValid(pkCard);
        return  Card.Color.ALL.get(colorIndex(pkCard));
    }
    
    private static int rankIndex(int pkCard){
        assert isValid(pkCard);
        return Bits32.extract(pkCard, 0,4);
    }

    /**
     * donne le rang d'un carte format entier
     * 
     * @param int pkCard
     * 
     * @return Card.Rank
     * 	rang de la carte
     */
    public static Card.Rank rank(int pkCard){
        assert isValid(pkCard);
        return Card.Rank.ALL.get(rankIndex(pkCard));
    }
     
    private static int rankIndexWithTrump(int pkCard, int trump) {
        assert isValid(pkCard);
        if(colorIndex(pkCard)==trump) {
            return Card.Rank.ALL.get(rankIndex(pkCard)).trumpOrdinal();
        }else {
            return rankIndex(pkCard);
        }
    }

    /**
     * regarde si la première carte est meilleure que la seconde 
     * les cartes sont au format entier
     * 
     * @param Card.Color trump
     * 	couleur de l'atout 
     * 
     * @param int pkCardL
     * 	carte format entier comparée à l'autre 
     * 
     * @param int pkCardR
     * 	carte format entier à laquelle est comparée la première
     * 
     * @return boolean
     * 	true si pkCradL est meilleure que pkCardR
     * 	false sinon (y compris si les cartes ne sont pas comaprables)
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR){
        assert isValid(pkCardL);
        assert isValid(pkCardR);
        
        //check if card are comparable
        if( !isValid(pkCardL) || !isValid(pkCardR) || pkCardL==pkCardR) {
            return false;
        }
        
        int indexColorL=colorIndex(pkCardL);
        int indexColorR=colorIndex(pkCardR);
        
        //get indexRankAccordingToTrump
        int indexRankL=rankIndexWithTrump (pkCardL, trump.ordinal());
        int indexRankR=rankIndexWithTrump (pkCardR, trump.ordinal());
        
        //check of same color
        if(indexColorL==indexColorR) {
            return indexRankL>indexRankR;
        }
        
        //check first is trump other not comparable
        if(indexColorL==trump.ordinal()) {
            return true;
        }
        
       
        return false;
    }

    /**
     * calcule les points d'une carte
     * 
     * @param Card.Color trump 
     * 	couleur de l'atout
     * 
     * @param int pkCard
     * 	carte format entier 
     * 
     * @return int
     * 	les points de la carte passée en argument
     */
    public static int points(Card.Color trump, int pkCard){
        assert isValid(pkCard);
        
        int indexColor=colorIndex(pkCard);     
        //get indexRankAccordingToTrump
        int indexRank=rankIndex(pkCard);
        
        //check if trump
        if(indexColor==trump.ordinal()) {
            return Card.Rank.ALL.get(indexRank).trumpPoints();
        }
       
        return Card.Rank.ALL.get(indexRank).normalPoints();
    }

    /**
     * surchage la méthode toString
     * 
     * @param int pkCard
     * 	carte format entier
     * 
     * @return String
     * 	le symbole de la couleur de la carte ainsi que son rang
     */
    public  static String toString(int pkCard){
        int indexColor = Bits32.extract(pkCard, 4, 2);
        int indexRank  = Bits32.extract(pkCard, 0, 4);
        return Card.Color.ALL.get(indexColor).toString() + Card.Rank.ALL.get(indexRank).toString();
    }

}

