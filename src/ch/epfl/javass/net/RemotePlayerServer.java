package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * représente le serveur d'un joueur
 * 
 * @author erwan serandour (296100)
 *
 */
public final class RemotePlayerServer {
    private final Player underlyingPlayer;

    /**
     * @param underlyingPlayer
     *            le joueur sous-jacent qui doit être piloter
     */
    public RemotePlayerServer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }

    /**
     * met en marche le serveur jusqu'à la fin de la partie
     */
    public void run() {
       boolean end=false;
       try (ServerSocket s0 = new ServerSocket(Protocol.PORT);
               Socket s = s0.accept();
               BufferedReader r =
                 new BufferedReader(
                   new InputStreamReader(s.getInputStream(),
                             StandardCharsets.US_ASCII));
               BufferedWriter w =
                 new BufferedWriter(
                   new OutputStreamWriter(s.getOutputStream(),
                           StandardCharsets.US_ASCII))) {
        while(!end) {

                String line = r.readLine();
                String[] message = StringSerializer.splitString(line, ' ');
                JassCommand cmd = JassCommand
                        .valueOf(message[Protocol.COMMAND_INDEX]);

                switch (cmd) {
                case PLRS:
                    PlayerId own = Serializer.deserializeEnum(
                            message[Protocol.ID_INDEX], PlayerId.values());
                    
                    Map<PlayerId, String> names = deseralizePlayersNames(
                            message[Protocol.NAMES_INDEX]);
                    
                    underlyingPlayer.setPlayers(own, names);
                    break;
                case TRMP:
                    underlyingPlayer.setTrump(Serializer.deserializeEnum(
                            message[Protocol.TRUMP_INDEX],
                            Card.Color.values()));
                    break;
                case HAND:
                    underlyingPlayer.updateHand(Serializer
                            .deserializeCardSet(message[Protocol.HAND_INDEX]));
                    break;
                case TRCK:
                    underlyingPlayer.updateTrick(Serializer
                            .deserializeTrick(message[Protocol.TRCIK_INDEX]));
                    break;
                case CARD:
                    CardSet hand = Serializer.deserializeCardSet(
                            message[Protocol.HAND_WHEN_CARD]);
                    
                    Card c = underlyingPlayer.cardToPlay(Serializer
                            .deserializeTurnState(message[Protocol.TURNSTATE_INDEX]),
                                    hand);
                    
                    w.write(Serializer.serializeCard(c));
                    w.write(Protocol.END_MESSAGE);
                    w.flush();
                    break;
                case SCOR:
                    underlyingPlayer.updateScore(Serializer
                            .deserializeScore(message[Protocol.SCORE_INDEX]));
                    break;
                case WINR:
                    underlyingPlayer.setWinningTeam(Serializer.deserializeEnum(
                            message[Protocol.TEAM_INDEX], TeamId.values()));
                    end = true;
                    break;

                }
        }   
              
       }catch (IOException e) {
           throw new UncheckedIOException(e);
       }
   }
   
   private static Map<PlayerId, String> deseralizePlayersNames(String map){
       Map<PlayerId, String> playersNames = new HashMap<>();
       String[] names =StringSerializer.splitString(map,',');
       for(PlayerId id : PlayerId.ALL) {
           playersNames.put(id, StringSerializer.deserializeString(names[id.ordinal()]));
       }
       return playersNames;
   }
   

}
