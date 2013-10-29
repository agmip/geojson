package org.agmip.geojson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonFeatureTest {
    private static final Logger LOG = LoggerFactory.getLogger(GeoJsonFeatureTest.class);
    private GeoJsonFeature feature;
    private String validString;
    private static int testId;

    @Before
    public void setup() throws IOException {
        GeoJsonPoint p = new GeoJsonPoint(0.0, 0.0);
        feature = new GeoJsonFeature(p);
        validString = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[0.0,0.0]},\"properties\":";
    }

    @Test
    public void checkBlankFeature() {
        validString += "{}}";
        testId = 0;
        LOG.info("Starting Test {}", testId);
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void addPropertyTest() throws IOException {
        validString += "{\"something\":\"new\"}}";
        testId = 1;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "new");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void addPropertiesTest() throws IOException {
        validString += "{\"something\":\"new\",\"else\":\"old\"}}";
        testId = 2;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "new");
        feature.addProperty("else", "old");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void addSamePropertyTwice() throws IOException {
        validString += "{\"something\":\"new\"}}";
        testId = 3;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "new");
        feature.addProperty("something", "old");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void addAndRemoveProperty() throws IOException {
        validString += "{}}";
        testId = 4;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "else");
        feature.removeProperty("something");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void addTwoAndRemoveOneProperty() throws IOException {
        validString += "{\"something\":\"new\"}}";
        testId = 5;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "new");
        feature.addProperty("else", "old");
        feature.removeProperty("else");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void addTwoAndRemoveFirstProperty() throws IOException {
        validString += "{\"else\":\"old\"}}";
        testId = 6;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "new");
        feature.addProperty("else", "old");
        feature.removeProperty("something");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void modifyOneProperty() throws IOException {
        validString += "{\"something\":\"old\"}}";
        testId = 7;
        LOG.info("Starting Test {}", testId);
        feature.addProperty("something", "new");
        feature.updateProperty("something", "old");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void modifyOnePropertyManyTimes() throws IOException {
        validString += "{\"something\":\"round\"}}";
        testId=8;
        LOG.info("Starting test {}", testId);
        feature.addProperty("something", "new");
        feature.updateProperty("something", "old");
        feature.updateProperty("something", "round");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @Test
    public void kitchenSink() throws IOException {
        validString += "{\"else\":\"brandnew\"}}";
        testId = 9;
        LOG.info("Starting test {}", testId);
        feature.addProperty("something", "new");
        feature.addProperty("else", "old");
        feature.updateProperty("else", "brandnew");
        feature.removeProperty("something");
        assertEquals("Invalid feature string", validString, feature.toString());
    }

    @After
    public void tearDown() {
        LOG.info("{}. Testing feature string: {}", testId, feature.toString());
    }


}
