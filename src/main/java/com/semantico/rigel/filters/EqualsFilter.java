package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ImmutableSet;
import com.semantico.rigel.fields.Field;

public class EqualsFilter<T> extends Filter {

    private final Field<T> field;
    private final T value;

    public EqualsFilter(Field<T> field, T value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public boolean apply(SolrDocument input) {
        T actual = getFieldValue(field, input);
        return value.equals(actual);
    }

    @Override
    public String toSolrFormat() {
        return String.format("%s:%s", field.getFieldName(), escapeQueryChars(value.toString()));
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return ImmutableSet.<Field<?>>of(field);
    }
}
