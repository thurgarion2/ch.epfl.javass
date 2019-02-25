package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TeamIdTest {
    
    private static TeamId[] getAllTeam() {
        return new TeamId[] {TeamId.TEAM_1, TeamId.TEAM_2};
    }
    
    @Test
    void teamIdTeamAreInRightOrder() {
        assertEquals(getAllTeam(),TeamId.values());
    }
    
    @Test 
    void teamIdAllIsCorrect(){
        assertEquals(Arrays.asList(getAllTeam()),TeamId.ALL);
    }
    
    @Test 
    void teamIdCOUNTIsCorrect(){
        assertEquals(getAllTeam().length,TeamId.COUNT);
    }
    
    @Test 
    void teamIdOtherIsCorrect(){
        assertEquals(TeamId.TEAM_1,TeamId.TEAM_2.other());
        assertEquals(TeamId.TEAM_2,TeamId.TEAM_1.other());
    }

}
