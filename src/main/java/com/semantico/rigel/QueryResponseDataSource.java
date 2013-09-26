package com.semantico.sipp2.solr;

import org.apache.solr.client.solrj.response.QueryResponse;

public class QueryResponseDataSource implements FieldDataSource<QueryResponse> {

    private final QueryResponse response;

    public QueryResponseDataSource(QueryResponse response) {
        this.response = response;
    }

    @Override
    public QueryResponse get() {
        return response;
    }
}
