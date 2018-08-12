package it.unishare.common.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class GroupTest {

    @Test
    public void pairTest() {
        String object1 = "Test";
        Integer object2 = 57;

        Pair<String, Integer> pair = new Pair<>(object1, object2);

        assertEquals(pair.first, object1);
        assertEquals(pair.second, object2);
    }


    @Test
    public void tripleTest() {
        String object1 = "Test";
        Integer object2 = 57;
        Float object3 = 57.23f;

        Triple<String, Integer, Float> pair = new Triple<>(object1, object2, object3);

        assertEquals(pair.first, object1);
        assertEquals(pair.second, object2);
        assertEquals(pair.third, object3);
    }


    @Test
    public void quaternaryTest() {
        String object1 = "Test";
        Integer object2 = 57;
        Float object3 = 57.23f;
        Double object4 = Math.PI;

        Quaternary<String, Integer, Float, Double> pair = new Quaternary<>(object1, object2, object3, object4);

        assertEquals(pair.first, object1);
        assertEquals(pair.second, object2);
        assertEquals(pair.third, object3);
        assertEquals(pair.fourth, object4);
    }

}
