package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * défini le minimum pour se compoter comme un joueur
 * 
 * @author esera erwan serandour (296100)
 *
 */

public interface Player {
    /**
     * retourne la carte que le joueur désire jouer selon l'état du tour et sa
     * main
     * 
     * @param state
     *            l'état courant du tour
     * @param hand
     *            la main du joueur
     * @return la carte que le joueur désire jouer selon l'état du tour et sa
     *         main
     */
    public abstract Card cardToPlay(TurnState state, CardSet hand);

    /**
     * (appelée seulement en début de partie) informe le joueur qu'il a
     * l'identité ownId et que les autres joueurs sont nommés conformément au
     * conteus de la table associative playerNames
     * 
     * @param ownId
     *            l'identité du joueur
     * @param playerNames
     *            le nom de chaque joueur lié à leur identité
     */
    public default void setPlayers(PlayerId ownId,
            Map<PlayerId, String> playerNames) {
    }

    /**
     * informe le joueur de sa nouvelle main après un changement
     * 
     * @param newHand
     *            la nouvelle main d u joueur
     */
    public default void updateHand(CardSet newHand) {
    }

    /**
     * informe le joeur du changement d'atout
     * 
     * @param trump
     *            le nouvel atout
     */
    public default void setTrump(Color trump) {
    }

    /**
     * informe le joueur du changement du pli
     * 
     * @param newTrick
     *            l'état du pli mis à jour
     */
    public default void updateTrick(Trick newTrick) {
    }

    /**
     * informe le joueur du changement du score
     * 
     * @param score
     *            l'état du score mis à jour
     */
    public default void updateScore(Score score) {
    }

    /**
     * informe le joueur qu'une équipe à gagner (appelé qu'une seule fois)
     * 
     * @param winningTeam
     *            l'équipe gagante
     */
    public default void setWinningTeam(TeamId winningTeam) {
    }
}
