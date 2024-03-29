package com.github.khan301.darkbot.utils;

public class MathUtils {

    private static final double TAU = Math.PI * 2;

    public static double angleDiff(double alpha, double beta) {
        double phi = Math.abs(beta - alpha) % TAU;
        return phi > Math.PI ? TAU - phi : phi;
    }

}
