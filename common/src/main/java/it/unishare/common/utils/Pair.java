package it.unishare.common.utils;

public class Pair<T, U> {

    public T first;
    public U second;

    public Pair() {
        this(null, null);
    }

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

}
