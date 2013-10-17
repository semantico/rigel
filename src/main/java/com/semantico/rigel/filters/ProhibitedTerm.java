package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.fields.Field;

public class ProhibitedTerm extends ModifiedTerm {

    public ProhibitedTerm(BasicTerm modifiedTerm) {
        super(modifiedTerm);
    }

    @Override
    public boolean apply(SolrDocument input) {
        return !modifiedTerm.apply(input);
    }

    @Override
    public String toSolrFormat() {
        return String.format("-%s", modifiedTerm.toSolrFormat());
    }
}
