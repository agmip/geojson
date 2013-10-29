package org.agmip.geojson;

import java.util.List;

/**
 * @author Christopher Villalobos
 */
public abstract class GeoJsonGeometry extends GeoJsonObject {
    public abstract List<GeoJsonCoordinates> getAllCoordinates();
    public abstract GeoJsonCoordinates getFirstCoordinates();
}
