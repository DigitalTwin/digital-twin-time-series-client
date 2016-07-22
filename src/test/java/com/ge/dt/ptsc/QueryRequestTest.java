package com.ge.dt.ptsc;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class QueryRequestTest {

    @Test
    public void initializesEndDate() {
        final QueryRequest queryRequest = new QueryRequest(123L, 234L);
        assertThat(queryRequest.getEnd(), is(234L));
    }
}
