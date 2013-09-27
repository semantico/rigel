package com.semantico.rigel.fields;

import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.SolrDocDataSource;

import com.google.common.collect.ClassToInstanceMap;

import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.filters.Filter;

import java.util.Collection;

public abstract class SimpleField<T> implements Field<T>, MultivaluedFieldAdaptable<T> {

    protected final String fieldName;

    public SimpleField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public T getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        SolrDocument doc = context.getInstance(SolrDocDataSource.class).get();
        return (T) doc.getFieldValue(fieldName);
    }

    @Override
    public Collection<T> getValues(ClassToInstanceMap<FieldDataSource<?>> context) {
        SolrDocument doc = context.getInstance(SolrDocDataSource.class).get();
        return (Collection<T>) doc.getFieldValues(fieldName);
    }

    public MultivaluedField<T> multivalued() {
        return new MultivaluedFieldAdaptor<T>(this);
    }

    /*
     * Literate Filter methods
     */
    public Filter isEqualTo(T value) {
        return null;
    }
}
