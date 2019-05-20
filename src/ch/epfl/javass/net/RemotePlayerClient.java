package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * le client pour communiquer avec un joueur distant
 * 
 * @author Jean-Daniel Rouveyrol(301480)
 *
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

	private final Socket s;
	private final BufferedReader r;
	private final BufferedWriter w;

    /**
     * @param host
     *            l'adresse ip du joeur distant
     */
	public RemotePlayerClient(String host) {
		try {
		    s = new Socket(host, Protocol.PORT);
			r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
			w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	private static String serializeMap(Map<PlayerId, String> playerNames) {
		String[] names = new String[PlayerId.COUNT];
		playerNames.forEach((k, s) -> {
			names[k.ordinal()] = StringSerializer.serializeString(s);
		});
		return StringSerializer.combineString(',', names);
	}
	
    private void sendMessage(String mess) {
        try {
            w.write(mess);
            w.write(Protocol.END_MESSAGE);
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.PLRS.name(),
                Serializer.serializeEnum(ownId),
                serializeMap(playerNames)));
		   
	}

	@Override
	public void updateHand(CardSet newHand) {

        sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.HAND.name(),
                Serializer.serializeCardSet(newHand)));
			
	}

	@Override
	public void setTrump(Color trump) {
		
        sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.TRMP.name(),
                Serializer.serializeEnum(trump)));	
	}

	@Override
	public void updateTrick(Trick newTrick) {
		
        sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.TRCK.name(),
                Serializer.serializeTrick(newTrick)));
	}

	@Override
	public void updateScore(Score score) {
		
        sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.SCOR.name(),
                Serializer.serializeScore(score)));
		   	
	}

	@Override
	public void setWinningTeam(TeamId winningTeam) {

        sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.WINR.name(),
                Serializer.serializeEnum(winningTeam)));
        
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
	    sendMessage(StringSerializer.combineString(Protocol.SEPARATOR,
                JassCommand.CARD.name(),
                Serializer.serializeTurnState(state),
                Serializer.serializeCardSet(hand)));
		try {
			return Serializer.deserializeCard(r.readLine());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void close() throws Exception {
		r.close();
		w.close();
		s.close();
	}

}
