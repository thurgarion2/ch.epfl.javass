package ch.epfl.javass.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import ch.epfl.javass.jass.TeamId;

public final class ScoreBean {
    
    private final IntegerProperty turnPointsTeam1Property;
    private final IntegerProperty turnPointsTeam2Property;
    
    private final IntegerProperty gamePointsTeam1Property;
    private final IntegerProperty gamePointsTeam2Property;
    
    private final IntegerProperty totalPointsTeam1Property;
    private final IntegerProperty totalPointsTeam2Property;
    
    private final ObjectProperty<TeamId> winningTeamProperty;

    public ScoreBean(int turnPointsT1, int gamePointsT1, int totalPointsT1,
            int turnPointsT2, int gamePointsT2, int totalPointsT2) {

        this.turnPointsTeam1Property = new SimpleIntegerProperty(turnPointsT1);
        this.turnPointsTeam2Property = new SimpleIntegerProperty(turnPointsT2);

        this.gamePointsTeam1Property = new SimpleIntegerProperty(gamePointsT1);
        this.gamePointsTeam2Property = new SimpleIntegerProperty(gamePointsT2);

        this.totalPointsTeam1Property = new SimpleIntegerProperty(
                totalPointsT1);
        this.totalPointsTeam2Property = new SimpleIntegerProperty(
                totalPointsT2);
        this.winningTeamProperty = new SimpleObjectProperty<>(null);
        
    }
    
    public ScoreBean() {
        this(0,0,0,0,0,0);
    }
    
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? turnPointsTeam1Property : turnPointsTeam2Property;
    }
    
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if(team==TeamId.TEAM_1) {
            turnPointsTeam1Property.set(newTurnPoints);
            return;
        }
        turnPointsTeam2Property.set(newTurnPoints);
    }
    
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? gamePointsTeam1Property : gamePointsTeam2Property;
    }
    
    public void setGamePoints(TeamId team, int newTurnPoints) {
        if(team==TeamId.TEAM_1) {
            gamePointsTeam1Property.set(newTurnPoints);
            return;
        }
        gamePointsTeam2Property.set(newTurnPoints);
    }
    
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? totalPointsTeam1Property : totalPointsTeam2Property;
    }
    
    public void setTotalPoints(TeamId team, int newTurnPoints) {
        if(team==TeamId.TEAM_1) {
            totalPointsTeam1Property.set(newTurnPoints);
            return;
        }
        totalPointsTeam2Property.set(newTurnPoints);
    }
    
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeamProperty;
    }
    
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeamProperty.set(winningTeam);
    }

}
