package ch.epfl.javass.net;

import static org.junit.Assert.assertEquals;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.test.TestRandomizer;

public class StringSerializerTest {
    
    @Test
    void serializeIntWorksOnSomeCases() {
        SplittableRandom rng = TestRandomizer.newRandom();
        for(int i=0; i<1000; i++) {
            int current=rng.nextInt();
            String s=StringSerializer.serializeInt(current);
            assertEquals(current,StringSerializer.deserializeInt(s));
        }
    }
    
    @Test
    void serializeLongWorksOnSomeCases() {
        SplittableRandom rng = TestRandomizer.newRandom();
        for(int i=0; i<1000; i++) {
            long current=rng.nextLong();
            String s=StringSerializer.serializeLong(current);
            long after=StringSerializer.deserializeLong(s);
            assertEquals(current,after);
        }
    }
    
    @Test
    void serializeStringWorkOnSomeCases() {
        SplittableRandom rng = TestRandomizer.newRandom();
        for(int i=0; i<1000; i++) {
            int size=rng.nextInt(100);
            StringBuilder b = new StringBuilder();
            for(int a=0; a<size; a++) {
                b.append((char)(rng.nextInt(25)+'a'));
            }
            String s= StringSerializer.serializeString(b.toString());
            assertEquals(b.toString(),StringSerializer.deserializeString(s));
        }
    }
    
    
}
