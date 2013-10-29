package org.agmip.geojson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonPointTest {
    @Test
    public void testToString() {
        String accept = "{\"type\":\"Point\",\"coordinates\":[1.0,2.0]}";
        assertEquals("Incorrect Point Specification", accept, new GeoJsonPoint(1.0, 2.0).toString());
    }
}
