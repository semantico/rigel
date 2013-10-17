package com.semantico.rigel.filters;

import org.apache.solr.common.SolrDocument;

public class RequiredTerm extends ModifiedTerm {

    public RequiredTerm(BasicTerm modifiedTerm) {
        super(modifiedTerm);
    }

    @Override
    public boolean apply(SolrDocument input) {
        return modifiedTerm.apply(input);
    }

    @Override
    public String toSolrFormat() {
        return String.format("+%s", modifiedTerm.toSolrFormat());
    }
}
