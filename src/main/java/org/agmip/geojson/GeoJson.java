package org.agmip.geojson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.agmip.util.json.JsonFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoJson {
    private static final Logger LOG = LoggerFactory.getLogger(GeoJson.class);
    private List<GeoJsonGeometry> geometryCollection = new ArrayList<>();
    private List<GeoJsonFeature>  featureCollection  = new ArrayList<>();

    public GeoJson() {}

    public GeoJson(byte[] source) throws IOException {
        this.parse(source);
    }

    public List<GeoJsonGeometry> getGeometries() {
        return geometryCollection;
    }

    public List<GeoJsonFeature> getFeatures() {
        return featureCollection;
    }

    public GeoJson addGeometry(GeoJsonGeometry geometry) {
        this.geometryCollection.add(geometry);
        return this;
    }

    public GeoJson addFeature(GeoJsonFeature feature) {
        this.featureCollection.add(feature);
        return this;
    }

    public GeoJson removeGeometry(GeoJsonGeometry geometry) {
        this.geometryCollection.remove(geometry);
        return this;
    }

    public GeoJson removeGeometry(int index) {
        this.geometryCollection.remove(index);
        return this;
    }

    public GeoJson removeFeature(GeoJsonFeature feature) {
        this.featureCollection.remove(feature);
        return this;
    }

    public GeoJson removeFeature(int index) {
        this.featureCollection.remove(index);
        return this;
    }

    public GeoJson parse(byte[] source) throws IOException {
        int objectDepth = -1;
        JsonParser p = JsonFactoryImpl.INSTANCE.getParser(source);
        JsonToken t = p.nextToken();
        if (t != JsonToken.START_OBJECT) {
            LOG.error("Invalid GeoJson String: {}", new String(source, "UTF-8"));
            return this;
        }
        while (t != null) {
            if (t == JsonToken.START_OBJECT) {
                objectDepth++;
            }
            String currentName = p.getCurrentName();
            if(objectDepth == 0 && t == JsonToken.FIELD_NAME && currentName.equals("type")) {
                String type = p.nextTextValue();

                if (type != null) {
                    if (type.equals("FeatureCollection") || type.equals("GeometryCollection")) {
                        collectionParser(p);
                    } else if (type.equals("Feature")) {
                        this.featureCollection.add(featureParser(p));
                    } else if (type.equals("Point")) {
                        this.geometryCollection.add(pointParser(p));
                    } else {
                        LOG.error("Unsupported GeoJson Type: {}", type);
                    }
                }
            }
            t = p.nextToken();
        }
        p.close();
        return this;
    }

    @Override
    public String toString() {
        int featureSize = featureCollection.size();
        int geometrySize = geometryCollection.size();

        if(featureSize > 0) {
            if(featureSize == 1) {
                return featureCollection.get(0).toString();
            } else {
                StringBuilder sb = new StringBuilder("{\"type\":\"FeatureCollection\",\"features\":[");
                for(GeoJsonFeature f: featureCollection) {
                    sb.append(f.toString());
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append("]}");
                return sb.toString();
            }
        } else if (geometryCollection.size() > 0) {
            if(geometrySize == 1) {
                return geometryCollection.get(0).toString();
            } else {
                StringBuilder sb = new StringBuilder("{\"type\":\"GeometryCollection\",\"geometries\":[");
                for(GeoJsonGeometry g: geometryCollection) {
                    sb.append(g.toString());
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append("]}");
                return sb.toString();
            }
        } else {
            return "{}";
        }
    }

    public byte[] toByteArray() throws IOException {
        return this.toString().getBytes("UTF-8");
    }

    private GeoJsonPoint pointParser(JsonParser p) throws IOException {
        JsonToken t = p.nextToken();
        GeoJsonPoint point = null;
        while (t != null) {
            String currentName = p.getCurrentName();
            if(t == JsonToken.FIELD_NAME && currentName.equals("coordinates")) {
                t = p.nextToken();
                if(t != JsonToken.START_ARRAY) {
                    LOG.error("Invalid coordinate format");
                    throw new IOException("Invalid coordinate format");
                }
                p.nextToken();
                double x = p.getDoubleValue();
                p.nextToken();
                double y = p.getDoubleValue();
                point = new GeoJsonPoint(x, y);
            }
            if(t == JsonToken.END_OBJECT) {
                return point;
            }
            t = p.nextToken();
        }
        throw new IOException("Invalid GeoJson format");
    }

    private GeoJsonFeature featureParser(JsonParser p) throws IOException {

        JsonToken t = p.nextToken();
        GeoJsonGeometry geometry = null;

        while (t != null) {

            String currentName = p.getCurrentName();
            if(t == JsonToken.FIELD_NAME && currentName.equals("geometry")) {
                t = p.nextToken();
                if (t != JsonToken.START_OBJECT) {
                    throw new IOException("Invalid Geometry object");
                }
                //TODO: Rewire for various data types
                geometry = pointParser(p);
            }
            if(t == JsonToken.FIELD_NAME && currentName.equals("properties")) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(bos);
                p.nextToken();
                g.copyCurrentStructure(p);
                g.flush();
                g.close();
                p.nextToken();
                return new GeoJsonFeature(geometry, bos.toByteArray());
            }
            t = p.nextToken();
        }
        throw new IOException("Invalid GeoJson format");
    }

    private void collectionParser(JsonParser p) throws IOException {
        JsonToken t = p.nextToken();
        boolean processing = false;
        GeoJsonTypes parseType = null;

        while (t != null) {

            String currentName = p.getCurrentName();
            if(t == JsonToken.FIELD_NAME && (currentName.equals("features") || currentName.equals("geometries"))) {
                t = p.nextToken();
                if(t != JsonToken.START_ARRAY) {
                    throw new IOException("Invalid GeoJson format");
                }
                processing = true;
                if(currentName.equals("features")) {
                    parseType = GeoJsonTypes.FEATURECOLLECTION;
                } else {
                    parseType = GeoJsonTypes.GEOMETRYCOLLECTION;
                }
            }

            if(t == JsonToken.END_ARRAY) {
                return;
            }

            if (processing) {
                switch(parseType) {
                    case FEATURECOLLECTION:
                        featureCollection.add(featureParser(p));
                        break;
                    case GEOMETRYCOLLECTION:
                        geometryCollection.add(pointParser(p));
                        break;
                }
            }
            t = p.nextToken();
        }
    }

    private void geometryCollectionParser(JsonParser p) throws IOException {
        JsonToken t = p.nextToken();

        boolean processing = false;

        while (t != null) {
            String currentName = p.getCurrentName();
            if(t == JsonToken.FIELD_NAME && currentName.equals("geometries")) {
                t = p.nextToken();
                if(t != JsonToken.START_ARRAY) {

                }
            }
        }
    }
}
