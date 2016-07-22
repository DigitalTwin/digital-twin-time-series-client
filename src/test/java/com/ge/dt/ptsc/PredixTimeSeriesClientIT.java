package com.ge.dt.ptsc;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PredixTimeSeriesClientConfiguration.class)
public class PredixTimeSeriesClientIT {

    @Autowired
    private IngestionClient ingestionClient;

    @Autowired
    private QueryClient queryClient;

    @Test
    @Ignore
    public void ingestDataIntoTimeSeriesService() throws PredixTimeSeriesClientException {
        ingestionClient.doInSession(this::ingestDataUsingSession);
    }

    private void ingestDataUsingSession(IngestionSession ingestionSession) throws PredixTimeSeriesClientException {
        final IngestionRequest ingestionRequest = new IngestionRequest();
        ingestionRequest.addBody("integration-test2").addDataPoint(new Date(), 123.0, 3);
        ingestionSession.ingest(ingestionRequest);
    }

    @Test
    @Ignore
    public void queryDataFromTimeSeriesService() {
        final QueryRequest queryRequest = new QueryRequest(0L);
        queryRequest.getTags().add(new QueryRequest.Tag(singletonList("integration-test")));

        final QueryResponse.Tag.Result.DataPoint firstDataPoint =
                queryClient.query(queryRequest).getTags().get(0).getResults().get(0).getValues().get(0);
        assertThat(firstDataPoint.getTimestamp(), is(new Date(1461182799569L)));
        assertThat(firstDataPoint.getMeasure(), is(123D));
        assertThat(firstDataPoint.getQuality(), is(3));
    }
}
