package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.fields.Field;

public class GroupTerm extends BasicTerm {

    public Filter filter;

    public GroupTerm(Filter filter) {
        this.filter = filter;
    }

    @Override
    public boolean apply(SolrDocument input) {
        return filter.apply(input);
    }

    @Override
    public String toSolrFormat() {
        return String.format("(%s)", filter.toSolrFormat());
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return filter.getAffectedFields();
    }
}

