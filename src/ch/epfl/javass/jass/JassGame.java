package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * génère une partie de jass
 * 
 * @author Jean.Daniel Rouveyrol(301480)
 *
 */
public final class JassGame {

	// la carte qui définit le premier joueur
	private final static Card BEGIN = Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN);

	private final Map<PlayerId, Player> players;
	private final Map<PlayerId, String> playerNames;
	private final Map<PlayerId, CardSet> hands;

	private final Random shuffleRng;
	private final Random trumpRng;

	private PlayerId firstPlayer = null;
	private TurnState turnState;

	/**
	 * 
	 * @param rngSeed     la graine utilisée par les méthode de la calsse Random
	 * 
	 * @param players     relie les identfiants des joueurs aux joueurs
	 * 
	 * @param playerNames relie les identifiants des joueurs aux noms des joueurs
	 */
	public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
		Random rng = new Random(rngSeed);
		this.shuffleRng = new Random(rng.nextLong());
		this.trumpRng = new Random(rng.nextLong());
		this.hands = new HashMap<>();
		this.players = Collections.unmodifiableMap(players);
		this.playerNames = Collections.unmodifiableMap(playerNames);

		for (PlayerId id : PlayerId.ALL) {
			players.get(id).setPlayers(id, this.playerNames);
		}

		beginNewGame();
	}

	/**
	 * retourne vrai ssi la partie est terminée
	 * 
	 * @return vrai ssi la partie est terminée
	 */
	public boolean isGameOver() {
		// dans le cas
		Score score = turnState.score();
		int pointsTeam1 = score.totalPoints(TeamId.TEAM_1);
		int pointsTeam2 = score.totalPoints(TeamId.TEAM_2);
		return pointsTeam1 >= Jass.WINNING_POINTS || pointsTeam2 >= Jass.WINNING_POINTS;
	}

	// créer une List<Card> représentant le jeu complet à partir de ALL_CARDS de
	// CardSet
	private List<Card> deck() {
		List<Card> deck = new LinkedList<>();
		for (int i = 0; i < CardSet.ALL_CARDS.size(); i++) {
			deck.add(CardSet.ALL_CARDS.get(i));
		}
		return deck;
	}

	// mélange puis assigne les cartes du jeu à chaque joueur
	private void distribution() {
		List<Card> deck = deck();
		Collections.shuffle(deck, shuffleRng);
		int size = Jass.HAND_SIZE;
		
		for (PlayerId id : PlayerId.ALL) {
			int index = id.ordinal();
			CardSet hand = CardSet.of(deck.subList(index * size, (index + 1) * size));
			hands.put(id, hand);
			players.get(id).updateHand(hand);
		}
	}

	// détermine le premier joueur au début de la oertie (en fct du 7 de carreau)
	private PlayerId firstPlayerStartOfGame() {
		for (PlayerId id : PlayerId.ALL) {
			if (hands.get(id).contains(BEGIN)) {
				return id;
			}
		}
		return PlayerId.PLAYER_1;
	}

	// détermine le premier joueur au début d'un tour
	private PlayerId firstPlayerToStartTurn() {
		return PlayerId.ALL.get((firstPlayer.ordinal() + 1) % PlayerId.COUNT);
	}

	// détermine le nouvel atout
	private Card.Color newTrump() {
		int indexTrump = this.trumpRng.nextInt(Card.Color.COUNT);
		Card.Color trump = Card.Color.ALL.get(indexTrump);
		for (Player each : players.values()) {
			each.setTrump(trump);
		}
		return trump;
	}

	// met à jour le score de chaque joueur
	private void updateScore(Score score) {
		for (Player each : players.values()) {
			each.updateScore(score);
		}
	}

	// débute une nouvelle partie
	private void beginNewGame() {
		distribution();
		firstPlayer = firstPlayerStartOfGame();
		turnState = TurnState.initial(newTrump(), Score.INITIAL, firstPlayer);
		updateScore(turnState.score());
	}

	// débute un nouveau tour
	private void beginNewTurn() {
		distribution();
		firstPlayer = firstPlayerToStartTurn();
		turnState = TurnState.initial(newTrump(), turnState.score().nextTurn(), firstPlayer);
		updateScore(turnState.score());
	}
	
	// fini la partie
    private void endGame() {
        Score score = turnState.score().nextTurn();
        int ptTeam1 = score.totalPoints(TeamId.TEAM_1);
        int ptTeam2 = score.totalPoints(TeamId.TEAM_2);
        TeamId winning = ptTeam1 >= Jass.WINNING_POINTS ? TeamId.TEAM_1 : TeamId.TEAM_2;
        
        for (Player each : players.values()) {
            each.setWinningTeam(winning);
        }
    }

	// rammasse le pli courant
	private void collect() {
		turnState = turnState.withTrickCollected();
		updateScore(turnState.score());
	}
	
	// met à jour la connaissance du pli de chaque joeur
    private void updateTrick() {
        for (Player each : players.values()) {
            each.updateTrick(turnState.trick());
        }
    }

	// fait jouer un joeur
	private void play(PlayerId playerId) {
		Player player = players.get(playerId);
		CardSet hand = hands.get(playerId);

		Card played = player.cardToPlay(turnState, hand);
		turnState = turnState.withNewCardPlayed(played);

		hands.put(playerId, hand.remove(played));
		player.updateHand(hands.get(playerId));
		updateTrick();
	}

	/**
	 * fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait rien si
	 * la partie est terminée
	 * 
	 */
	public void advanceToEndOfNextTrick() {
		if (isGameOver()) {
			return;
		}
		// collecte le trick précédant
		if (turnState.trick().isFull()) {
			collect();
			// teste si le pli collecté met fin à la partie
			if (isGameOver()) {
				endGame();
				return;
			}
		}

		// vérifie si il faut commencer un nouveau tour ou pas
		if (turnState.isTerminal()) {
			beginNewTurn();
		}
		updateTrick();
		for (int i = 0; i < 4; i++) {
			play(turnState.nextPlayer());
		}

	}
}
