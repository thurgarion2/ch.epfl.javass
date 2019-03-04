package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;



class PackedScoreTest {

	@Test
	void isValidTest() {
		//pli
		long score = 0b1011;
		assertFalse(PackedScore.isValid(score));
		score = 0b0100;
		assertTrue(PackedScore.isValid(score));
		
		//points tour
		score = 0b10_0000_0000_0000;
		assertFalse(PackedScore.isValid(score));
		score= 0b1_0000_0000_0000;
		assertTrue(PackedScore.isValid(score));
		
		//points ggame
		score = 0b1000_0000_0000_0000_0000_0000;
		assertFalse(PackedScore.isValid(score));
		score = 0b1000_0000_0000_0000_0000;
		assertTrue(PackedScore.isValid(score));
		
		//bits nuls
		score = 0b1_0000_0000_0000_0000_0000_0000_0000;
		assertFalse(PackedScore.isValid(score));
		
	}
	// t1 : 2 19 131  t2 4 50 50
	@Test
	void packTest() {
		long expected = 0b0000_0000_0000_0110_0100_0011_0010_0100_0000_0000_0001_0000_0110_0001_0011_0010L;
		assertEquals(expected, PackedScore.pack(2, 19, 131, 4, 50, 50));
	}
	
	@Test 
	void turnTricksTest() {
		long score = 0b0000_0000_0000_0110_0100_0011_0010_0100_0000_0000_0001_0000_0110_0001_0011_0010L;
		TeamId t = TeamId.TEAM_1;
		assertEquals(2, PackedScore.turnTricks(score, t));
		t = TeamId.TEAM_2;
		assertEquals(4, PackedScore.turnTricks(score, t));
	}
	
	@Test
	void turnPointsTest() {
		long score = 0b0000_0000_0000_0110_0100_0011_0010_0100_0000_0000_0001_0000_0110_0001_0011_0010L;
		TeamId t = TeamId.TEAM_1;
		assertEquals(19, PackedScore.turnPoints(score, t));
		t = TeamId.TEAM_2;
		assertEquals(50, PackedScore.turnPoints(score, t));
	}
	
	@Test
	void gamePoints() {
		long score = 0b0000_0000_0000_0110_0100_0011_0010_0100_0000_0000_0001_0000_0110_0001_0011_0010L;
		TeamId t = TeamId.TEAM_1;
		assertEquals(131, PackedScore.gamePoints(score, t));
		t = TeamId.TEAM_2;
		assertEquals(50, PackedScore.gamePoints(score, t));
	}
	
	@Test 
	void withAdditionalTrickTest() {
		// team1 win
		// pack  must have been checked -- je prends pas en compte les 5 de der
		long score = PackedScore.pack(8,132,300, 0,0,200);
		assertEquals(PackedScore.pack(9, 252, 300, 0,0,200), PackedScore.withAdditionalTrick(score, TeamId.TEAM_1, 20));
		//team2 win the last trick
		assertEquals(PackedScore.pack(8, 132, 300, 1, 20, 200),PackedScore.withAdditionalTrick(score, TeamId.TEAM_2, 20));
	}
	
	@Test
	void nextTurnTest() {
		// pack must have been checked
		long score = PackedScore.pack(8,132,300, 1,20,200);
		assertEquals(PackedScore.pack(0, 0, 432, 0, 0, 220), PackedScore.nextTurn(score));
		score = PackedScore.pack(9, 252, 300, 0,0,200);
		assertEquals(PackedScore.pack(0, 0, 552, 0, 0, 200), PackedScore.nextTurn(score));
		
	}

}
