package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class JassGame{
	
	private Map<PlayerId, Player> players;
	private Map<PlayerId, String> playerNames;
	private Map<PlayerId, List<Card>> hands;
	private List<Card> deck;
	private Random shuffleRng;
	private Random trumpRng;
	private Score score;
	private PlayerId lastPlayer; 
	private int currentTrick;
	
	public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames){
		Random rng = new Random(rngSeed);
		this.shuffleRng = new Random(rng.nextLong());
		this.trumpRng = new Random(rng.nextLong());
		this.players = Collections.unmodifiableMap(players);
		this.playerNames = Collections.unmodifiableMap(playerNames);
		this.score = Score.INITIAL;
		this.currentTrick = PackedTrick.INVALID;
		this.lastPlayer = null;
	}
	
	public boolean isGameOver() {
		if(score.gamePoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS || score.gamePoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
			return true;
		}
		return false;
	}
	
	private Card.Color trump() {
		List<Card.Color> colors = Card.Color.ALL;
		Collections.shuffle(colors, trumpRng);
		return colors.get(0);
	}
	
	private void deck() {
		for(Card.Rank r : Card.Rank.ALL) {
			for(Card.Color c : Card.Color.ALL) {
				deck.add(new Card(PackedCard.pack(c, r)));
			}
		}
	}
	
	private void distribution() {
		List<Card> hand = new LinkedList<>();
		for(int i = 0; i < 8 ;++i) {
			hand.add(deck.get(i));
		}
		hands.put(PlayerId.ALL.get(0), hand);
		hand.clear();
		for(int i = 0; i < 8 ;++i) {
			hand.add(deck.get(i+9));
		}
		hands.put(PlayerId.ALL.get(1), hand);
		hand.clear();
		for(int i = 0; i < 8 ;++i) {
			hand.add(deck.get(i+18));
		}
		hands.put(PlayerId.ALL.get(2), hand);
		hand.clear();
		for(int i = 0; i < 8 ;++i) {
			hand.add(deck.get(i+27));
		}
		hands.put(PlayerId.ALL.get(3), hand);
	}
	
	private PlayerId firstPlayer() {
		boolean checkp1 = false;
		boolean checkp2 = false;
		boolean checkp3 = false;
		boolean checkp4 = false;
		List<Card> handP1 = hands.get(PlayerId.PLAYER_1);
		for(Card c : handP1) {
			if((new Card(PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN)).equals(c))){
				checkp1 = true;
			}
		}
		List<Card> handP2 = hands.get(PlayerId.PLAYER_1);
		for(Card c : handP2) {
			if((new Card(PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN)).equals(c))){
				checkp2 = true;
			}
		}
		List<Card> handP3 = hands.get(PlayerId.PLAYER_1);
		for(Card c : handP3) {
			if((new Card(PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN)).equals(c))){
				checkp3 = true;
			}
		}
		List<Card> handP4 = hands.get(PlayerId.PLAYER_1);
		for(Card c : handP4) {
			if((new Card(PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN)).equals(c))){
				checkp4 = true;
			}
		}
		if(checkp1 || checkp2) {
			if(checkp1) {
				return PlayerId.PLAYER_1;
			}
			return PlayerId.PLAYER_2;
		}else {
			if(checkp3) {
				return PlayerId.PLAYER_3;
			}
			return PlayerId.PLAYER_4;
		}
	}
	
	private void beginRound(Card.Color trump, PlayerId firstPlayer) {
		deck();
		Collections.shuffle(deck, shuffleRng);
		distribution();
		this.currentTrick = PackedTrick.firstEmpty(trump, firstPlayer);
	}
	
	public void advanceToEndOfNextTrick() {
		if(!PackedTrick.isValid(currentTrick) || PackedTrick.index(currentTrick) == 9) {
			this.beginRound(this.trump(), this.firstPlayer());
		}
	}
}
