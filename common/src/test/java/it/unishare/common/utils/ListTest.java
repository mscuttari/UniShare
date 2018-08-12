package it.unishare.common.utils;

import it.unishare.common.utils.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListTest {

    @Test
    public void equalsIgnoreOrderAndRepetitionsTest() {
        List<String> listOne = new ArrayList<>();
        listOne.add("Test1");
        listOne.add("Test2");
        listOne.add("Test2");
        listOne.add("Test3");
        listOne.add("Test4");

        List<String> listTwo = new ArrayList<>();
        listTwo.add("Test2");
        listTwo.add("Test4");
        listTwo.add("Test1");
        listTwo.add("Test4");
        listTwo.add("Test3");

        assertTrue(ListUtils.equalsIgnoreOrderAndRepetitions(listOne, listTwo));
        assertTrue(ListUtils.equalsIgnoreOrderAndRepetitions(listTwo, listOne));
    }

}
