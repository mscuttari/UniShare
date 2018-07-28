package it.unishare.common.utils;

import java.util.Random;

public final class RandomGaussian {

    private Random fRandom = new Random();

    public double getGaussian(double mean, double variance){
        return mean + fRandom.nextGaussian() * variance;
    }

}
