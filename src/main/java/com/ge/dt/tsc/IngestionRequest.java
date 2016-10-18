package com.ge.dt.tsc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.*;

import static java.util.UUID.randomUUID;

@SuppressWarnings("unused")
public class IngestionRequest {

    private final UUID messageId;
    private final List<Body> bodies;

    public IngestionRequest() {
        this.messageId = randomUUID();
        this.bodies = new ArrayList<>();
    }

    public Body addBody(String tagName) {
        final Body body = new Body(tagName);
        bodies.add(body);
        return body;
    }

    public UUID getMessageId() {
        return messageId;
    }

    @JsonProperty("body")
    public List<Body> getBodies() {
        return bodies;
    }

    public static class Body {

        private final String tagName;
        private final List<DataPoint> dataPoints;
        private final Map<String, String> attributes;

        public Body(String tagName) {
            this.tagName = tagName;
            this.dataPoints = new ArrayList<>();
            this.attributes = new HashMap<>();
        }

        @SuppressWarnings("UnusedReturnValue")
        public Body addDataPoint(Date timestamp, double measure, int quality) {
            final DataPoint dataPoint = new DataPoint(timestamp, measure, quality);
            getDataPoints().add(dataPoint);
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Body addAttribute(String name, String value) {
            attributes.put(name, value);
            return this;
        }

        @JsonProperty("name")
        public String getTagName() {
            return tagName;
        }

        @JsonProperty("datapoints")
        public List<DataPoint> getDataPoints() {
            return dataPoints;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        @JsonSerialize(using = DataPointSerializer.class)
        public static class DataPoint {

            private final Date timestamp;
            private final double measure;
            private final int quality;

            private DataPoint(Date timestamp, double measure, int quality) {
                this.timestamp = timestamp;
                this.measure = measure;
                this.quality = quality;
            }

            public Date getTimestamp() {
                return timestamp;
            }

            public double getMeasure() {
                return measure;
            }

            public int getQuality() {
                return quality;
            }

        }

        private static class DataPointSerializer extends JsonSerializer<DataPoint> {
            @Override
            public void serialize(DataPoint dataPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProviders) throws IOException {
                jsonGenerator.writeStartArray();
                jsonGenerator.writeNumber(dataPoint.timestamp.getTime());
                jsonGenerator.writeNumber(dataPoint.measure);
                jsonGenerator.writeNumber(dataPoint.quality);
                jsonGenerator.writeEndArray();
            }
        }

    }
}
