package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ImmutableSet;
import com.semantico.rigel.fields.Field;

import static com.google.common.base.Preconditions.*;

public class StartsWithTerm extends BasicTerm {

    private final Field<String> field;
    private final String value;

    public StartsWithTerm(Field<String> field, String value) {
        checkNotNull(value);
        checkNotNull(field);
        this.field = field;
        this.value = value;
    }

    @Override
    public boolean apply(SolrDocument input) {
        String actual = getFieldValue(field, input);
        if (actual == null) {
            return false;
        }
        return actual.startsWith(value);
    }

    @Override
    public String toSolrFormat() {
        return String.format("%s:%s*", field.getFieldName(), ClientUtils.escapeQueryChars(value));
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return ImmutableSet.<Field<?>>of(field);
    }
}
