package it.unishare.common.utils;

public class Quaternary<T, U, V, W> {

    public T first;
    public U second;
    public V third;
    public W fourth;

    public Quaternary() {
        this(null, null, null, null);
    }

    public Quaternary(T first, U second, V third, W fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

}
