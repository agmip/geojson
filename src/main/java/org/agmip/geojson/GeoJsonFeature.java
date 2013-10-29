package org.agmip.geojson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.agmip.util.json.JsonFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Christopher Villalobos
 */
public class GeoJsonFeature extends GeoJsonObject {
    private static final Logger LOG = LoggerFactory.getLogger(GeoJsonFeature.class);
    private GeoJsonGeometry geometry;
    private byte[] properties;
    private boolean updated = false;
    private String stringCache;

    public GeoJsonFeature(GeoJsonGeometry geometry) throws IOException {
        this.geometry = geometry;
        this.properties = "{}".getBytes("UTF-8");
    }

    public GeoJsonFeature(GeoJsonGeometry geometry, byte[] properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public GeoJsonFeature addProperty(String key, String value) throws IOException {
        modifyProperty(key, value, ModifyMode.ADD);
        return this;
    }

    public GeoJsonFeature updateProperty(String key, String value) throws IOException {
        modifyProperty(key, value, ModifyMode.UPDATE);
        return this;
    }

    public GeoJsonFeature removeProperty(String key) throws IOException {
        modifyProperty(key, "", ModifyMode.REMOVE);
        return this;
    }

    public String getProperty(String key) throws IOException {
        JsonParser p = JsonFactoryImpl.INSTANCE.getParser(this.properties);
        JsonToken  t = p.nextToken();
        String value = null;

        while(t != null) {
            String currentName = p.getCurrentName();
            if( currentName != null && currentName.equals(key)) {
                value = p.nextTextValue();
                break;
            }
            t = p.nextToken();
        }
        p.close();
        return value;
    }

    public GeoJsonGeometry getGeometry() {
        return this.geometry;
    }

    public String getPropertyOr(String key, String orValue) throws IOException {
        String value = this.getProperty(key);
        if (value == null) return orValue;
        return value;
    }

    private void modifyProperty(String key, String value, ModifyMode mode) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonGenerator         g = JsonFactoryImpl.INSTANCE.getGenerator(bos);
        JsonParser            p = JsonFactoryImpl.INSTANCE.getParser(this.properties);
        JsonToken             t = p.nextToken();
        boolean               pass = false;

        while(t != null) {
            String currentName = p.getCurrentName();
            if (currentName != null) {
                if(t == JsonToken.FIELD_NAME && currentName.equals(key)) {
                    if(mode == ModifyMode.REMOVE) {
                        p.nextToken();
                        updated = true;
                    } else if(mode == ModifyMode.UPDATE) {
                        g.copyCurrentEvent(p);
                        g.writeString(value);
                        p.nextToken();
                        updated = true;
                    } else if(mode == ModifyMode.ADD) {
                        // Already exists
                        g.copyCurrentEvent(p);
                        p.nextToken();
                        g.copyCurrentEvent(p);
                    }
                    pass = true;
                    t = p.nextToken();
                    continue;
                }
            }
            if (!pass) {
                if (t == JsonToken.END_OBJECT && (mode == ModifyMode.ADD || mode == ModifyMode.UPDATE)) {
                    updated = true;
                    g.writeStringField(key, value);
                }

            }
            g.copyCurrentEvent(p);
            t = p.nextToken();
        }
        g.flush();
        g.close();
        p.close();
        this.properties = bos.toByteArray();
    }

    @Override
    public String toString() {
        if (updated || stringCache == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"type\":\"Feature\",\"geometry\":");
            sb.append(geometry.toString());
            sb.append(",\"properties\":");
            try {
                sb.append(new String(this.properties, "UTF-8"));
            } catch (IOException ex) {
                LOG.error(ex.getMessage());
                sb.append("{}");
            }
            sb.append("}");
            stringCache = sb.toString();
        }
        updated = false;
        return stringCache;
    }

    public byte[] toByteArray() throws IOException {
        return this.toString().getBytes("UTF-8");
    }

    private enum ModifyMode {
        ADD,
        UPDATE,
        REMOVE
    }
}
