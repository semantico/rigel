package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.fields.MultivaluedFieldAdaptable;
import com.semantico.rigel.fields.MultivaluedFieldAdaptor;

public class EqualsTerm<T> extends FieldBasedTerm<T> {

    private final T value;
    private final Function<? super T, String> toSolrFormatFunc;

    public EqualsTerm(Field<T> field, T value, Function<? super T, String> toSolrFormatFunc) {
        super(field);
        this.value = value;
        this.toSolrFormatFunc = toSolrFormatFunc;
    }

    @Override
    protected boolean decide(T actual) {
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
