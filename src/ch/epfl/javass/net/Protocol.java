package ch.epfl.javass.net;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * contient la plupart des constantes necessaires à la communication du client
 * avec le joueur distant
 * 
 * @author erwan serandour (296100)
 *
 */
// necessaire de connaitre le protocol pour pouvoir interpreter la plupart des
// messages
public interface Protocol {
   
    /**
     * le port sur lequel le client et le serveur communique
     */
    public static final int PORT = 5108;
    
    /**
     * la base dans la quelle sont sérialisé les entiers
     */
    public static final int BASE = 16;
    
    /**
     * le jeu de caractère utilisé pour la communication
     */
    public static final Charset CHARSET = StandardCharsets.US_ASCII;
    /**
     * l'index de la commande dans un message
     */
    public static final int COMMAND_INDEX = 0;
    /**
     * l'index de l'id du joueur dans un message
     */
    public static final int ID_INDEX = 1;
    /**
     * l'index des noms des joueurs dans un message
     */
    public static final int NAMES_INDEX = 2;
    /**
     * l'index du score du joueur dans un message
     */
    public static final int SCORE_INDEX = 1;
    /**
     * l'index du pli du joueur dans un message
     */
    public static final int TRCIK_INDEX = 1;
    /**
     * l'index de l'atout dans un message
     */
    public static final int TRUMP_INDEX = 1;
    /**
     * l'index d'une équipe dans un message
     */
    public static final int TEAM_INDEX = 1;
    /**
     * l'index de l'état du tour dans message
     */
    public static final int TURNSTATE_INDEX = 1;
    /**
     * l'index de la main du joueur dans message (avec la commande HAND)
     */
    public static final int HAND_INDEX = 1;
    /**
     * l'index de la main du joueur dans message (avec la commande CARD)
     */
    public static final int HAND_WHEN_CARD = 2;

    /**
     * représente la fin d'un message
     */
    public static final char END_MESSAGE = '\n';
    /**
     * sépare deux composants d'un messages
     */
    public static final char SEPARATOR = ' ';

}
