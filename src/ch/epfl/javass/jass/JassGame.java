package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class JassGame{
	
	private Map<PlayerId, Player> players;
	private Map<PlayerId, String> playerNames;
	private Map<PlayerId, >
	private List<Card> deck;
	private Random shuffleRng;
	private Random trumpRng;
	private long pkScore;
	private int currentTrick;
	
	public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String>, playerNames){
		Random rng = new Random(rengSeed);
		this.shuffleRng = new Random(rng.nextLong());
		this.trumpRng = new Random(rng.nextLong());
		this.players = Collections.unmodifiableMap(players);
		this.playerNames = Collections.unmodifiableMap(playerNames);
		this.pkScore = PackedScore.INITIAL;
		this.currentTrick = PackedTrick.INVALID;
		this.deck = PackedCardSet.ALL_CARDS;
	}
	
	public boolean isGameOver() {
		if(PackedScore.gamePoints(pkScore, TeamId.TEAM_1) >= Jass.WINNING_POINTS || PackedScore.gamePoints(pkScore, TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
			return true;
		}
		return false;
	}
	
	private void deck() {
		for(Card.Rank r : Card.Rank.ALL) {
			for(Card.Color c : Card.Color.ALL) {
				deck.add(new Card(PackedCard.pack(c, r)));
			}
		}
	}
	
	
	private Card.Color trump() {
		List<Card.Color> colors = Card.Color.ALL;
		Collections.shuffle(colors, trumpRng);
	}
	
	private PlayerId firstPlayer()
	
	private void beginRound(Card.Color trump, PlayerId firstPlayer) {
		deck();
		Collections.shuffle(deck, shuffleRng);
		
		this.currentTrick = PackedTrick.firstEmpty(trump, firstPlayer);
	}
	
	public void advanceToEndOfNextTrick() {
		if(!PackedTrick.isValid(currentTrick) || PackedTrick.index(currentTrick) == 9) {
			this.beginRound(this.trump(), );
		}
	}
}
