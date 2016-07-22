package com.ge.dt.ptsc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class QueryResponse {

    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {

        private String name;
        private List<Result> results;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Result {

            private List<DataPoint> values;

            public List<DataPoint> getValues() {
                return values;
            }

            public void setValues(List<DataPoint> values) {
                this.values = values;
            }

            @JsonDeserialize(using = DataPointDeserializer.class)
            public static class DataPoint {

                private final Date timestamp;
                private final double measure;
                private final int quality;

                public DataPoint(Date timestamp, double measure, int quality) {
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

            static class DataPointDeserializer extends JsonDeserializer<DataPoint> {
                @Override
                public DataPoint deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                    final JsonNode dataPointArray = parser.getCodec().readTree(parser);
                    return new DataPoint(
                            new Date(dataPointArray.get(0).asLong()),
                            dataPointArray.get(1).asDouble(),
                            dataPointArray.get(2).asInt());
                }
            }
        }

    }

}
