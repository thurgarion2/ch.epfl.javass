package ch.epfl.javass.jass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

public class ScoreTest {
  
    
    @Test
    void ofPackedWorks() {
        
        Score s=Score.ofPacked(0);
        
        assertEquals(0,s.turnTricks(TeamId.TEAM_1));
        assertEquals(0,s.turnTricks(TeamId.TEAM_2));
        
        assertEquals(0,s.turnPoints(TeamId.TEAM_1));
        assertEquals(0,s.turnPoints(TeamId.TEAM_2));
        
        assertEquals(0,s.gamePoints(TeamId.TEAM_1));
        assertEquals(0,s.gamePoints(TeamId.TEAM_2));
        
        assertEquals(0,s.packed());
     
        
        for(int i=1; i<=2000; i++) {
            for(int a=1; a<=257; a++) {
                for(int b=1; b<=9; b++) {
                    
                    long packed=PackedScore.pack(b,a,i,b,a,i);
                    s=Score.ofPacked(packed);
                    
                    assertEquals(b,s.turnTricks(TeamId.TEAM_1));
                    assertEquals(b,s.turnTricks(TeamId.TEAM_2));
                    
                    assertEquals(a,s.turnPoints(TeamId.TEAM_1));
                    assertEquals(a,s.turnPoints(TeamId.TEAM_2));
                    
                    assertEquals(i,s.gamePoints(TeamId.TEAM_1));
                    assertEquals(i,s.gamePoints(TeamId.TEAM_2));
                    
                    assertEquals(i+a,s.totalPoints(TeamId.TEAM_1));
                    assertEquals(i+a,s.totalPoints(TeamId.TEAM_2));
                   
                    assertEquals(packed,s.packed());
                    
                }
            }
        }
    }
    
    @Test 
    void ofPackedFailWithOutOfBoundArgument (){
        for(int i=2001; i<=3000; i++) {
            for(int a=100; a<=200; a++) {
                for(int b=0; b<=9; b++) {
                    int b1=b, a1=a,i1=i;
                    
                    assertThrows(IllegalArgumentException.class, () -> {
                        Score.ofPacked(PackedScore.pack(b1,a1,i1,b1,a1,i1));
                    });
                }
            }
        }
    }
    
    @Test 
    void withAdditionalTrickFailsWithIllegalArgument(){
        Score s=Score.ofPacked(0);
        
        for(int i=-1; i>=-100; i--) {
            int pt=i;
            assertThrows(IllegalArgumentException.class, () -> {s.withAdditionalTrick(TeamId.TEAM_1, pt);});
        }
    }
    
    @Test
    void equalsWorksWithSameScore() {
        for(int i=0; i<100; i++) {
            for(int a=0; a<100; a++) {
                for(int b=0; b<=9; b++) {
                    Score s=Score.ofPacked(PackedScore.pack(b,a,i, b,a,i));
                    assertTrue(s.equals(s));
                }
            }
        }
    }
    
    @Test
    void equalsWorksWithDifferantScore() {
        for(int i=0; i<100; i++) {
            for(int a=0; a<100; a++) {
                for(int b=0; b<=8; b++) {
                    Score s1=Score.ofPacked(PackedScore.pack(b,a,i, b,a,i));
                    Score s2=Score.ofPacked(PackedScore.pack(b+1,a+1,i, b,a,i));
                    assertEquals(false,s1.equals(s2));
                }
            }
        }
    }

}
