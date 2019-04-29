package ch.epfl.javass.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import ch.epfl.javass.jass.TeamId;

/**
 * le bean du score d'une partie de jass
 * 
 * @author erwan serandour (296100)
 *
 */
public final class ScoreBean {
    
    private final IntegerProperty turnPointsTeam1Property;
    private final IntegerProperty turnPointsTeam2Property;
    
    private final IntegerProperty gamePointsTeam1Property;
    private final IntegerProperty gamePointsTeam2Property;
    
    private final IntegerProperty totalPointsTeam1Property;
    private final IntegerProperty totalPointsTeam2Property;
    
    private final ObjectProperty<TeamId> winningTeamProperty;

  
    /**
     * 
     */
    public ScoreBean() {

        this.turnPointsTeam1Property = new SimpleIntegerProperty(0);
        this.turnPointsTeam2Property = new SimpleIntegerProperty(0);

        this.gamePointsTeam1Property = new SimpleIntegerProperty(0);
        this.gamePointsTeam2Property = new SimpleIntegerProperty(0);

        this.totalPointsTeam1Property = new SimpleIntegerProperty(0);
        this.totalPointsTeam2Property = new SimpleIntegerProperty(0);
        
        this.winningTeamProperty = new SimpleObjectProperty<>(null);
        
    }
    
    /**
     * retourne la propriété des points du tours de l'équipe donnée
     * 
     * @param team
     *            l'équipe 
     * @return la propriété des points du tours de l'équipe donnée
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? turnPointsTeam1Property
                : turnPointsTeam2Property;
    }

    /**
     * met à jour la propriété des points du tour de l'équipe donnée
     * 
     * @param team
     *            l'équipe
     * @param newTurnPoints
     *            les points du tour mis à jour
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        (team == TeamId.TEAM_1 ? turnPointsTeam1Property
                : turnPointsTeam2Property).set(newTurnPoints);
    }

    /**
     * retourne la propriété des points de la partie de l'équipe donnée
     * 
     * @param team
     *            l'équipe
     * @return la propriété des points de la partie de l'équipe donnée
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? gamePointsTeam1Property
                : gamePointsTeam2Property;
    }

    /**
     * met à jour les points de la partie de l'équipe donnée
     * 
     * @param team
     *            l'équipe
     * @param newGamePoints
     *            les points mis à jour
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        (team == TeamId.TEAM_1 ? gamePointsTeam1Property
                : gamePointsTeam2Property).set(newGamePoints);
    }

    /**
     * retourne la propriété des points totaux de la partie de l'équipe donnée
     * 
     * @param team
     *            l'équipe
     * @return la propriété des points totaux de la partie de l'équipe donnée
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? totalPointsTeam1Property
                : totalPointsTeam2Property;
    }

    /**
     * met à jour la propriété des points totaux de la partie de l'équipe donnée
     * 
     * @param team
     *            l'équipe
     * @param newTotalPoints
     *            les points totaux mis à jour
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        (team == TeamId.TEAM_1 ? totalPointsTeam1Property
                : totalPointsTeam2Property).set(newTotalPoints);
    }
    
    /**
     * retourne la propriété de l'équipe gagante
     * 
     * @return la propriété de l'équipe gagante
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeamProperty;
    }

    /**
     * met à jour la propriété de l'équipe gagante
     * 
     * @param winningTeam
     *            l'équipe gagante
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeamProperty.set(winningTeam);
    }

}
