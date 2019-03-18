package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.HashMap;
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
		this.hands = new HashMap<>();
		this.players = Collections.unmodifiableMap(players);
		this.playerNames = Collections.unmodifiableMap(playerNames);
		
		this.score = Score.INITIAL;
		//commence le premier tour
		beginNewTurn();
	}
	
	public boolean isGameOver() {
		if(score.totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS || score.totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
			return true;
		}
		return false;
	}
	
	private void distribution(List<Card> shuffle) {
		for(PlayerId id : PlayerId.ALL) {
		    int index=id.ordinal();
		    CardSet hand = CardSet.of(shuffle.subList(index*9,(index+1)*9));
		    hands.put(id, hand);
		    players.get(id).updateHand(hand);
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
        firstPlayer = PlayerId.ALL.get((firstPlayer.ordinal()+1)%4);
        System.out.println("par fisrtplayetostartturn ok");
        return firstPlayer; 
	}
	
	private Card.Color newTrump() {
		int indexTrump= this.trumpRng.nextInt(4);
		Card.Color trump = Card.Color.ALL.get(indexTrump);
		for(PlayerId id : players.keySet()) {
			players.get(id).setTrump(trump);
		}
		
		System.out.println("par trump ok");
		return trump;
	}
	
	private void beginNewTurn() {
	    List<Card> allCards = new LinkedList();
	    for(int i=0; i<CardSet.ALL_CARDS.size(); i++) {
	        allCards.add(CardSet.ALL_CARDS.get(i));
	    }
	    
	    System.out.println("par ici 1");
	    
	    Collections.shuffle(allCards, shuffleRng);
		distribution(allCards);
		
		System.out.println("par ici 2");
		
		turnState=TurnState.initial(newTrump(), score, firstPlayerToStartTurn());
		
		System.out.println("par ici");
	}
	
	private void collect() {
		turnState.withTrickCollected();
	}
	
	private void play(PlayerId playerId) {
		Player player = players.get(playerId);
		CardSet hand = hands.get(playerId);
		List<Card> toPlay = new LinkedList<>();
		toPlay.add(player.cardToPlay(turnState, hand));
		
		turnState.withNewCardPlayed(toPlay.get(0));

		player.updateHand(hand.difference(CardSet.of(toPlay)));
		
	}
	
	
	public void advanceToEndOfNextTrick() {
		if(isGameOver()) {
			return ;
		}
	    //fin du tour
		if(turnState.isTerminal()) {		
		    beginNewTurn();
		}
		//collecte le trick précédant
		if(turnState.trick().isFull()) {
			collect();
		}
		
		while(!turnState.trick().isFull()) {
		   	play(turnState.nextPlayer());    
		}
	}
}
