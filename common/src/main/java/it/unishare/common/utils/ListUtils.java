package it.unishare.common.utils;

import java.util.List;

public class ListUtils {

    private ListUtils() {

    }


    /**
     * Check if two lists contain the same elements, ignoring order and repetitions
     *
     * @param   x       first list
     * @param   y       second list
     * @param   <T>     elements type
     *
     * @return  true if the two lists contain the same elements; false otherwise
     */
    public static <T> boolean equalsIgnoreOrderAndRepetitions(List<T> x, List<T> y) {
        if (x == null && y == null)
            return true;

        if (x == null || y == null)
            return false;

        return x.containsAll(y) && y.containsAll(x);
    }

}
