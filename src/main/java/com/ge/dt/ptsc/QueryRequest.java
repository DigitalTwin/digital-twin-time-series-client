package com.ge.dt.ptsc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@SuppressWarnings("unused")
public class QueryRequest {

    private final Long start;
    private final List<Tag> tags;
    private Long end;

    public QueryRequest(Long start) {
        tags = new ArrayList<>();
        this.start = start;
    }

    public QueryRequest(Long start, Long end) {
        this(start);
        this.end = end;
    }

    public Long getStart() {
        return start;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    @JsonInclude(NON_NULL)
    public static class Tag {

        private final List<String> names;
        private Long limit;
        private Filter filter;

        public Tag(List<String> names) {
            this.names = names;
        }

        @JsonProperty("name")
        public List<String> getNames() {
            return names;
        }

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }

        @JsonProperty("filters")
        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        @JsonInclude(NON_NULL)
        public static class Filter {

            private Map<String, String> attributes;

            public Map<String, String> getAttributes() {
                return attributes;
            }

            public void setAttributes(Map<String, String> attributes) {
                this.attributes = attributes;
            }

        }
    }

}
