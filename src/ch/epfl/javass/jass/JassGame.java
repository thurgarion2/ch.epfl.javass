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
		
		for(PlayerId id : PlayerId.ALL) {
		    players.get(id).setPlayers(id, playerNames);
		}
		//commence le premier tour
		initializeTurn();
	}
	
	public boolean isGameOver() {
	  
		if(score.totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS) {
		    for(PlayerId id : PlayerId.ALL) {
                players.get(id).setWinningTeam(TeamId.TEAM_1);
            }
		    return true;
		}
		
		if(score.totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
            for(PlayerId id : PlayerId.ALL) {
                players.get(id).setWinningTeam(TeamId.TEAM_2);
            }
            return true;
        }
		
		if(max>8) {
		    return true;
		}
		return false;
	}
	
	private void distribution() {
	    List<Card> allCards = new LinkedList();
        for(int i=0; i<CardSet.ALL_CARDS.size(); i++) {
            allCards.add(CardSet.ALL_CARDS.get(i));
        }
        
        Collections.shuffle(allCards, shuffleRng);
        
		for(PlayerId id : PlayerId.ALL) {
		    int index=id.ordinal();
		    CardSet hand = CardSet.of(allCards.subList(index*9,(index+1)*9));
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
        return PlayerId.ALL.get((firstPlayer.ordinal()+1)%4); 
	}
	
	private Card.Color newTrump() {
		int indexTrump= this.trumpRng.nextInt(4);
		Card.Color trump = Card.Color.ALL.get(indexTrump);
		
		for(PlayerId id : PlayerId.ALL) {
			players.get(id).setTrump(trump);
		}
		
		return trump;
	}
	
	private void initializeTurn() {
	    distribution();
	    firstPlayer=this.firstPlayerStartOfGame();
	    score=Score.INITIAL;
	    turnState=TurnState.initial(newTrump(), score, firstPlayer);
	}
	
	private void beginNewTurn() {
	    distribution();
		firstPlayer = firstPlayerToStartTurn();
		score = turnState.score();
		turnState=TurnState.initial(newTrump(), score, firstPlayer);
	}
	
	private void collect() {
		turnState=turnState.withTrickCollected();
		score=turnState.score();
		for(PlayerId id : PlayerId.ALL) {
            players.get(id).updateScore(turnState.score());
        }
	}
	
	private void play(PlayerId playerId) {
		Player player = players.get(playerId);
		CardSet hand = hands.get(playerId);
		
		Card played = player.cardToPlay(turnState, hand);
		turnState=turnState.withNewCardPlayed(played);

		hands.put(playerId, hand.remove(played));
		
		player.updateHand(hands.get(playerId));
		
		for(PlayerId id : PlayerId.ALL) {
            players.get(id).updateTrick(turnState.trick());
        }
	}
	
	int max=0;
	public void advanceToEndOfNextTrick() {
		//collecte le trick précédant
		if(turnState.trick().isFull()) {
			collect();
		}
		
		//fin du tour
        if(turnState.isTerminal()) {        
            beginNewTurn();
        }
		
		while(!turnState.trick().isFull()) {
		   	play(turnState.nextPlayer());
		   
		}
		
		  max++;
	}
}
