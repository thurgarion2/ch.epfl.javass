package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class PlayerIdTest {
    private static PlayerId[] getAllPlayer() {
        return new PlayerId[] {PlayerId.PLAYER_1,PlayerId.PLAYER_2,PlayerId.PLAYER_3,PlayerId.PLAYER_4};
    }
    
    @Test
    void playerIdPlayerAreInRightOrder() {
        assertEquals(getAllPlayer(),PlayerId.values());
    }
    
    @Test 
    void playerIdAllIsCorrect(){
        assertEquals(Arrays.asList(getAllPlayer()),PlayerId.ALL);
    }
    
    @Test 
    void playerIdCOUNTIsCorrect(){
        assertEquals(getAllPlayer().length,PlayerId.COUNT);
    }
    
    @Test 
    void playerIdTeamIsCorrect(){
        assertEquals(TeamId.TEAM_1,PlayerId.PLAYER_1.team());
        assertEquals(TeamId.TEAM_1,PlayerId.PLAYER_3.team());
        
        assertEquals(TeamId.TEAM_2,PlayerId.PLAYER_4.team());
        assertEquals(TeamId.TEAM_2,PlayerId.PLAYER_2.team());
    }

}
