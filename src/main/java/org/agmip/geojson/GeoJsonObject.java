package org.agmip.geojson;

import java.io.IOException;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonObject {
    protected byte[] source;
    public GeoJsonTypes type;

    public byte[] toByteArray() throws IOException {
        return this.toString().getBytes("UTF-8");
    }
}

