package org.agmip.geojson;

import java.io.IOException;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonCoordinates {
    private double x;
    private double y;

    public GeoJsonCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String toString() {
        return new StringBuilder()
                .append("[")
                .append(x)
                .append(",")
                .append(y)
                .append("]").toString();
    }

    public byte[] toByteArray() throws IOException {
        return this.toString().getBytes("UTF-8");
    }
}
