package ch.epfl.javass.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

public class SerializerTest {
    
    @Test
    void serializeCardSet1() {
        String set = "210002008d0088";
        CardSet s = Serializer.deserializeCardSet(set);
        System.out.println(s);
        assertEquals(set, Serializer.serializeCardSet(s)); 
    }
    
    @Test
    void serializeCardSet2() {
        String set = "21000200890088";
        CardSet s = Serializer.deserializeCardSet(set);
        System.out.println(s);
        assertEquals(set, Serializer.serializeCardSet(s)); 
    }
    
    @Test
    void serializeCardSet3() {
        String set = "1ff01ff01ff01ff";
        CardSet s = Serializer.deserializeCardSet(set);
        System.out.println(s);
        assertEquals(set, Serializer.serializeCardSet(s)); 
    }
    
    @Test
    void serializeTurnState() {
        String t = "0,1ff01ff01ff01ff,60ffffff";
        TurnState turn = Serializer.deserializeTurnState(t);
        System.out.println(turn);
        assertEquals(t,Serializer.serializeTurnState(turn));
    }
    
    @Test
    void serializeTrick1() {
        String t = "60ffffd2";
        Trick trick = Serializer.deserializeTrick(t);
        System.out.println(trick);
        assertEquals(t,Serializer.serializeTrick(trick));
    }
    
    @Test
    void serializeTrick2() {
        String t = "60fd4452";
        Trick trick = Serializer.deserializeTrick(t);
        System.out.println(trick);
        assertEquals(t,Serializer.serializeTrick(trick));
    }
    
    @Test
    void serializeTrick3() {
        String t = "60fff452";
        Trick trick = Serializer.deserializeTrick(t);
        System.out.println(trick);
        assertEquals(t,Serializer.serializeTrick(trick));
    }
    
    @Test
    void serializeTrick4() {
        String t = "60554452";
        Trick trick = Serializer.deserializeTrick(t);
        System.out.println(trick);
        assertEquals(t,Serializer.serializeTrick(trick));
    }
    
    @Test
    void serializeScore() {
        String s = "1e100000000";
        Score score = Serializer.deserializeScore(s);
        System.out.println(score);
        assertEquals(s, Serializer.serializeScore(score));
    }
    
    @Test
    void serializeCard() {
        String c = "12";
        Card card= Serializer.deserializeCard(c);
        System.out.println(card);
        assertEquals(c,Serializer.serializeCard(card));
    }
}
