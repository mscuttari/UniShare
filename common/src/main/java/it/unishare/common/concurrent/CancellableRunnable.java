package it.unishare.common.concurrent;

public interface CancellableRunnable extends Runnable {

    boolean cancel();

}
