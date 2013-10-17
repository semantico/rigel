package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.fields.Field;

public abstract class ModifiedTerm implements Term {

    protected BasicTerm modifiedTerm;

    public ModifiedTerm(BasicTerm modifiedTerm) {
        this.modifiedTerm = modifiedTerm;
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return modifiedTerm.getAffectedFields();
    }

    @Override
    public boolean isOptional() {
        return false;
    }
}
