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

public final class RemotePlayerClient implements Player, AutoCloseable {

	private Socket s;
	private BufferedReader r;
	private BufferedWriter w;

	public RemotePlayerClient(String host) {
		try {
			s = new Socket(host, 5108);
			r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
			w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	private static String serializeMap(Map<PlayerId, String> playerNames) {
		String[] names = new String[4];
		playerNames.forEach((k, s) -> {
			names[k.ordinal()] = StringSerializer.serializeString(s);
		});
		return StringSerializer.combineString(',', names);
	}

	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		try {
			String message = "PLRS " + Serializer.serializeEnum(ownId) + " " + serializeMap(playerNames) + '\n';
			w.write(message);
			w.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void updateHand(CardSet newHand) {
		try {
			w.write("HAND ");
			w.write(Serializer.serializeCardSet(newHand));
			w.write('\n');
			w.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void setTrump(Color trump) {
		try {
			w.write("TRMP ");
			w.write(Serializer.serializeEnum(trump));
			w.write('\n');
			w.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void updateTrick(Trick newTrick) {
		try {
			w.write("TRCK ");
			w.write(Serializer.serializeTrick(newTrick));
			w.write('\n');
			w.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void updateScore(Score score) {
		try {
			w.write("SCOR ");
			w.write(Serializer.serializeScore(score));
			w.write('\n');
			w.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void setWinningTeam(TeamId winningTeam) {
		try {
			w.write("WINR ");
			w.write(Serializer.serializeEnum(winningTeam));
			w.write('\n');
			w.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		try {
			w.write("CARD ");
			w.write(Serializer.serializeTurnState(state));
			w.write(" ");
			w.write(Serializer.serializeCardSet(hand));
			w.write('\n');
			w.flush();
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
