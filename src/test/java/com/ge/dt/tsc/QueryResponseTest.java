package com.ge.dt.tsc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class QueryResponseTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private ObjectCodec objectCodec;

    @Before
    public void setup() {
        initMocks(this);
        given(jsonParser.getCodec()).willReturn(objectCodec);
    }

    @Test
    public void deserializesDataPoint() throws IOException {
        final JsonNode jsonNode = mock(JsonNode.class);
        given(objectCodec.readTree(same(jsonParser))).willReturn(jsonNode);
        given(jsonNode.get(0)).willReturn(LongNode.valueOf(123L));
        given(jsonNode.get(1)).willReturn(DoubleNode.valueOf(234D));
        given(jsonNode.get(2)).willReturn(IntNode.valueOf(5));

        final QueryResponse.Tag.Result.DataPoint dataPoint =
                new QueryResponse.Tag.Result.DataPointDeserializer().deserialize(jsonParser, null);

        assertThat(dataPoint.getTimestamp().getTime(), is(123L));
        assertThat(dataPoint.getMeasure(), is(234D));
        assertThat(dataPoint.getQuality(), is(5));
    }
}
