package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ImmutableSet;
import com.semantico.rigel.fields.Field;

public class EverythingTerm extends BasicTerm {

    @Override
    public boolean apply(SolrDocument input) {
        return true;
    }

    @Override
    public String toSolrFormat() {
        return "*:*";
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return ImmutableSet.<Field<?>>of();
    }
}
