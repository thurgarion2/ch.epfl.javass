package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class JassGame{
    //la carte qui définit le premier joueur
    private final static Card BEGIN = Card.of(Card.Color.DIAMOND,Card.Rank.SEVEN);
	
	private Map<PlayerId, Player> players;
	private Map<PlayerId, String> playerNames;
	
	private Map<PlayerId, CardSet> hands;
	
	private Random shuffleRng;
	private Random trumpRng;
	
	private Score score;
	
	private PlayerId firstPlayer; 
	private TurnState turnState;
	
	
	public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames){
		Random rng = new Random(rngSeed);
		this.shuffleRng = new Random(rng.nextLong());
		this.trumpRng = new Random(rng.nextLong());
		
		this.players = Collections.unmodifiableMap(players);
		this.playerNames = Collections.unmodifiableMap(playerNames);
		
		this.score = Score.INITIAL;
		//commence le premier tour
		beginNewTurn();
	}
	
	public boolean isGameOver() {
		if(score.gamePoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS || score.gamePoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
			return true;
		}
		return false;
	}
	
	private void distribution(List<Card> shuffle) {
		for(PlayerId id : PlayerId.ALL) {
		    int index=id.ordinal();
		    CardSet hand = CardSet.of(shuffle.subList(Math.min(0,(index-1)*9),index*9));
		    hands.put(id, hand);
		}
	}
	
	private PlayerId firstPlayerStartOfGame() {
	    for(PlayerId id : PlayerId.ALL) {
	        if( hands.get(id).contains(BEGIN)) {
	            return id;
	        }
	    }
	    return PlayerId.PLAYER_1;
	}
	

	private PlayerId firstPlayerToStartTurn() {
	    //teste si premier tour
        if(firstPlayer==null) {
            return firstPlayerStartOfGame();
        }
        return PlayerId.ALL.get((firstPlayer.ordinal()+1)%4);
	}
	
	private Card.Color newTrump() {
		int indexTrump= this.trumpRng.nextInt(3);
		return Card.Color.ALL.get(indexTrump);
	}
	
	private void beginNewTurn() {
	    List<Card> allCards = new LinkedList();
	    for(int i=0; i<CardSet.ALL_CARDS.size(); i++) {
	        allCards.add(CardSet.ALL_CARDS.get(i));
	    }
	    
	    Collections.shuffle(allCards, shuffleRng);
		distribution(allCards);
		
		turnState=TurnState.initial(newTrump(), score, firstPlayerToStartTurn());
		
	}
	
	private void play(PlayerId player) {}
	
	public void advanceToEndOfNextTrick() {
		if(this.isGameOver()) {
			return ;
		}
	    //fin du tour
		if(turnState.isTerminal()) {
		    beginNewTurn();
		}else {
		    
		}
		
	}
}
