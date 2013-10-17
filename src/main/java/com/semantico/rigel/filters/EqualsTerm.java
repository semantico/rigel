package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.semantico.rigel.fields.Field;

public class EqualsTerm<T> extends BasicTerm {

    private final Field<T> field;
    private final T value;
    private final Function<? super T, String> toSolrFormatFunc;

    public EqualsTerm(Field<T> field, T value, Function<? super T, String> toSolrFormatFunc) {
        this.field = field;
        this.value = value;
        this.toSolrFormatFunc = toSolrFormatFunc;
    }

    @Override
    public boolean apply(SolrDocument input) {
        T actual = getFieldValue(field, input);
        return value.equals(actual);
    }

    @Override
    public String toSolrFormat() {
        return String.format("%s:%s", field.getFieldName(), toSolrFormatFunc.apply(value));
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return ImmutableSet.<Field<?>>of(field);
    }
}
