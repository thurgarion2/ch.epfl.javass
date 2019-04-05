package ch.epfl.javass.net;

/**
 * définit les 7 types de messages échangés par le client et le serveur
 * 
 * @author erwan serandour (296100)
 * 
 */
public enum JassCommand {
    /**
     * représente la fonction setPlayers de l'interface Player
     */
    PLRS(),
    /**
     * représente la fonction setTrump de l'interface Player
     */
    TRMP(),
    /**
     * représente la fonction updateHand de l'interface Player
     */
    HAND(),
    /**
     * représente la fonction updateTrick de l'interface Player
     */
    TRCK(),
    /**
     * représente la fonction cardToPlay de l'interface Player
     */
    CARD(),
    /**
     * représente la fonction updateScore de l'interface Player
     */
    SCOR(),
    /**
     * représente la fonction setWinningTeam de l'interface Player
     */
    WINR();
    
}
