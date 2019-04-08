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
 * @author erwan serandour (296100)
 *
 */
public final class RemotePlayerServer {
   private Player underlyingPlayer;
   
   public RemotePlayerServer (Player underlyingPlayer) {
       this.underlyingPlayer=underlyingPlayer;
   }
   
   public void run() {
       boolean end=false;
       try (ServerSocket s0 = new ServerSocket(5108);
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
            String[] message=StringSerializer.splitString(r.readLine(),' ');
            JassCommand cmd = JassCommand.valueOf(message[0]);
            
            switch(cmd) {
              case PLRS:
                  PlayerId own = PlayerId.ALL.get(StringSerializer.deserializeInt(message[1]));
                  Map<PlayerId, String> names=playersNames(StringSerializer.splitString(message[1],','));
                  underlyingPlayer.setPlayers(own, names);
                  break;
              case TRMP:
                  underlyingPlayer.setTrump(Card.Color.ALL.get(StringSerializer.deserializeInt(message[1])));
                  break;
              case HAND:
                  underlyingPlayer.updateHand(Serializer.deserializeCardSet(message[1]));
                  break;
              case TRCK:
                  underlyingPlayer.updateTrick(Serializer.deserializeTrick(message[1]));
                  break;
              case CARD:
                  CardSet hand=Serializer.deserializeCardSet(message[2]);
                  Card c = underlyingPlayer.cardToPlay(Serializer.deserializeTurnState(message[1]),hand);
                  w.write(Serializer.serializeCard(c));
                  w.write('\n');
                  break;
              case SCOR:
                  underlyingPlayer.updateScore(Serializer.deserializeScore(message[1]));
                  break;
              case WINR:
                  underlyingPlayer.setWinningTeam(TeamId.ALL.get(StringSerializer.deserializeInt(message[1])));
                  end=true;
                  break;

            }
        }   
              
       }catch (IOException e) {
           throw new UncheckedIOException(e);
       }
   }
   
   private static Map<PlayerId, String> playersNames(String[] names){
       Map<PlayerId, String> playersNames = new HashMap<>();
       for(PlayerId id : PlayerId.ALL) {
           playersNames.put(id, StringSerializer.deserializeString(names[id.ordinal()]));
       }
       return playersNames;
   }
   

}
