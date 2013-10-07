package com.semantico.rigel.fields;

import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.RigelContext;
import com.semantico.rigel.SolrDocDataSource;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Range;

import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.filters.Filter;

import java.util.Collection;

/*
 * well... it started simple
 */
public abstract class SimpleField<T> implements Field<T>, MultivaluedFieldAdaptable<T> {

    private final String fieldName;

    public SimpleField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public T getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        SolrDocument doc = context.getInstance(SolrDocDataSource.class).get();
        return (T) doc.getFirstValue(getFieldName());
    }

    @Override
    public Collection<T> getValues(ClassToInstanceMap<FieldDataSource<?>> context) {
        SolrDocument doc = context.getInstance(SolrDocDataSource.class).get();
        return (Collection<T>) doc.getFieldValues(getFieldName());
    }

    public MultivaluedField<T> multivalued() {
        return new MultivaluedFieldAdaptor<T>(this);
    }

    public static interface FieldNameSource {

        public String getFieldName();

        public void bindToContext(RigelContext rigelContext);
    }

    /*
     * Literate Filter methods
     */
    public Filter equalTo(T value) {
        return Filter.isEqualTo(this, value);
    }

}
