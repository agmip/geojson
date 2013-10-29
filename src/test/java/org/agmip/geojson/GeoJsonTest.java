package org.agmip.geojson;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonTest {
    private static final Logger LOG = LoggerFactory.getLogger(GeoJsonTest.class);
    private GeoJsonPoint p;
    private GeoJsonFeature f;

    @Before
    public void setup() throws IOException {
        p = new GeoJsonPoint(1.2, 3.4);
        f = new GeoJsonFeature(p);
        f.addProperty("something", "else");
        f.addProperty("else", "chain");
    }

    @Test
    public void validPointParser() throws IOException {
        String validPoint = "[1.2,3.4]";
        GeoJson gj = new GeoJson(p.toByteArray());
        GeoJsonPoint point = (GeoJsonPoint)gj.getGeometries().get(0);
        assertEquals("Invalid point parsed", validPoint, point.getFirstCoordinates().toString());
        assertEquals("Non-matching GeoJson strings", p.toString(), gj.toString());
    }

    @Test
    public void validFeatureParser() throws IOException {
        String validPoint = "[1.2,3.4]";
        String propertyValue = "else";
        GeoJson gj = new GeoJson(f.toByteArray());
        GeoJsonFeature feature = gj.getFeatures().get(0);
        assertEquals("Invalid point parsed", validPoint, feature.getGeometry().getFirstCoordinates().toString());
        assertEquals("Invalid property found", propertyValue, feature.getProperty("something"));
        assertEquals("Non-matching GeoJson strings", f.toString(), gj.toString());
    }

    @Test
    public void validFeatureCollection() throws IOException {
        GeoJsonFeature f2 = new GeoJsonFeature(p).addProperty("something", "new");
        String fCol = "{\"type\":\"FeatureCollection\",\"features\":[";
        fCol   += f.toString();
        fCol   += ",";
        fCol   += f2.toString();
        fCol   += "]}";

        GeoJson gj = new GeoJson(fCol.getBytes("UTF-8"));
        assertEquals("Not enough features in collection", 2, gj.getFeatures().size());
        assertEquals("Invalid property found", "new", gj.getFeatures().get(1).getProperty("something"));
        assertEquals("Non-matching GeoJson strings", fCol, gj.toString());
    }

    @Test
    public void validGeometryCollection() throws IOException {
        String validCoordinates = "[5.6,7.8]";
        GeoJsonPoint p2 = new GeoJsonPoint(5.6,7.8);
        String gCol = "{\"type\":\"GeometryCollection\",\"geometries\":[";
        gCol   += p.toString();
        gCol   += ",";
        gCol   += p2.toString();
        gCol   += "]}";

        GeoJson gj = new GeoJson(gCol.getBytes("UTF-8"));
        assertEquals("Not enough geometries in the collection", 2, gj.getGeometries().size());
        assertEquals("Invalid coordinates found", validCoordinates, gj.getGeometries().get(1).getFirstCoordinates().toString());
        assertEquals("Non-matching GeoJson strings", gCol, gj.toString());
    }

    @Test
    public void buildFromStratch() throws IOException {
        GeoJson gj = new GeoJson();
        GeoJsonFeature f = new GeoJsonFeature(new GeoJsonPoint(1,1));
        f.addProperty("something", "new");
        gj.addFeature(new GeoJsonFeature(new GeoJsonPoint(0,0)));
        gj.addFeature(f);
        LOG.info("Raw GeoJson {}", gj.toString());
    }

    @Test
    public void addDifferentFailure() throws IOException {
        GeoJson validGj = new GeoJson();
        GeoJson gj = new GeoJson();

        validGj.addGeometry(new GeoJsonPoint(0,0));
        gj.addGeometry(new GeoJsonPoint(0,0));
        gj.addFeature(new GeoJsonFeature(validGj.getGeometries().get(0)));
        assertEquals("These two are comparatively equal?", validGj, gj);
    }
}
