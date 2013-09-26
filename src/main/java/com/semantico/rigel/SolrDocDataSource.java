package com.semantico.sipp2.solr;

import org.apache.solr.common.SolrDocument;

public class SolrDocDataSource implements FieldDataSource<SolrDocument> {

    private final SolrDocument doc;

    public SolrDocDataSource(SolrDocument doc) {
        this.doc = doc;
    }

    @Override
    public SolrDocument get() {
        return doc;
    }
}

