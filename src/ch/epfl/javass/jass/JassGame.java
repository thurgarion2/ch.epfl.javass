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
		beginNewGame();
	}
	
	public boolean isGameOver() {
	    //dans le cas 
	    Score score = turnState.score();
	    int pointsTeam1=score.totalPoints(TeamId.TEAM_1);
	    int pointsTeam2=score.totalPoints(TeamId.TEAM_2);
		if(pointsTeam1>=Jass.WINNING_POINTS || pointsTeam2>=Jass.WINNING_POINTS) {
			    return true;
		}
		return false;
	}
	
	private List<Card> deck(){
	    List<Card> deck = new LinkedList();
        for(int i=0; i<CardSet.ALL_CARDS.size(); i++) {
           deck.add(CardSet.ALL_CARDS.get(i));
        }
        return deck;
	}
	
	private void distribution() {
	    List<Card> deck=deck();
        Collections.shuffle(deck, shuffleRng);
        
		for(PlayerId id : PlayerId.ALL) {
		    int index=id.ordinal();
		    CardSet hand = CardSet.of(deck.subList(index*9,(index+1)*9));
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
		for(Player each: players.values()) {
			each.setTrump(trump);
		}
		return trump;
	}
	
	private void updateTrick() {
	    for(Player each: players.values()) {
	           each.updateTrick(turnState.trick());
	        }
	}
	
	private void updateScore() {
        for(Player each: players.values()) {
               each.updateScore(turnState.score());
            }
    }
	
	private void beginNewGame() {
	    distribution();
        firstPlayer = firstPlayerStartOfGame();
        turnState=TurnState.initial(newTrump(), Score.INITIAL, firstPlayer);
        updateScore();
	}
	
	private void beginNewTurn() {
	    distribution();
		firstPlayer = firstPlayerToStartTurn();
		turnState=TurnState.initial(newTrump(), turnState.score().nextTurn(), firstPlayer);
		updateScore();
	}
	
	private void collect() {
		turnState=turnState.withTrickCollected();
		updateScore();
	}
	
	private void play(PlayerId playerId) {
		Player player = players.get(playerId);
		CardSet hand = hands.get(playerId);
		
		Card played = player.cardToPlay(turnState, hand);
		turnState=turnState.withNewCardPlayed(played);

		hands.put(playerId, hand.remove(played));
		player.updateHand(hands.get(playerId));
		updateTrick();
	}
	
	public void advanceToEndOfNextTrick() {
	    if(isGameOver()) {
	        return;
	    }
		//collecte le trick précédant
		if(turnState.trick().isFull() && !isGameOver()) {
			collect();
		}
		
		if(isGameOver()) {
	          Score score=turnState.score();
	          int ptTeam1=score.totalPoints(TeamId.TEAM_1);
	          int ptTeam2=score.totalPoints(TeamId.TEAM_2);
	          TeamId winning= ptTeam1>=Jass.WINNING_POINTS ? TeamId.TEAM_1 :  TeamId.TEAM_2;
	          for(Player each : players.values()) {
	              each.setWinningTeam(winning);
	          }
	          return;
	    }
		
		//fin du tour
        if(turnState.isTerminal()) {        
            beginNewTurn();
        }
               
        updateTrick();
		for(int i=0; i<4; i++) {
		   	play(turnState.nextPlayer());
		}

	}
}
