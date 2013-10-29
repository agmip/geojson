package org.agmip.geojson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonPoint extends GeoJsonGeometry {
    private GeoJsonCoordinates coordinates;
    GeoJsonTypes type = GeoJsonTypes.POINT;

    public GeoJsonPoint(GeoJsonCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public GeoJsonPoint(double x, double y) {
        this.coordinates = new GeoJsonCoordinates(x,y);
    }

    public GeoJsonCoordinates getFirstCoordinates() {
        return coordinates;
    }

    public List<GeoJsonCoordinates> getAllCoordinates() {
        List<GeoJsonCoordinates> l = new ArrayList<>();
        l.add(coordinates);
        return l;
    }

    public String toString() {
        return new StringBuilder()
                .append("{\"type\":\"Point\",\"coordinates\":")
                .append(this.coordinates.toString())
                .append("}").toString();
    }
}
