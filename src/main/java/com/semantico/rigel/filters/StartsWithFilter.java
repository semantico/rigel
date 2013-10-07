package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ImmutableSet;
import com.semantico.rigel.fields.Field;

import static com.google.common.base.Preconditions.*;

public class StartsWithFilter extends Filter {

    private final Field<String> field;
    private final String value;

    public StartsWithFilter(Field<String> field, String value) {
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
        return String.format("%s:%s*", field.getFieldName(), escapeQueryChars(value.toString()));
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return ImmutableSet.<Field<?>>of(field);
    }
}
