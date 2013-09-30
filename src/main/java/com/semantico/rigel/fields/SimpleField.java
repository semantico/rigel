package com.semantico.rigel.fields;

import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.RigelContext;
import com.semantico.rigel.SolrDocDataSource;

import com.google.common.collect.ClassToInstanceMap;

import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.filters.Filter;

import java.util.Collection;

/*
 * well... it started simple
 */
public abstract class SimpleField<T> implements Field<T>, MultivaluedFieldAdaptable<T> {

    private final FieldNameSource fieldNameSource;

    public SimpleField(final String fieldName) {
        this.fieldNameSource = new FieldNameSource() {
            @Override
            public String getFieldName() {
                return fieldName;
            }

            @Override
            public void bindToContext(RigelContext rigelContext) {
                //Do Nothing
            }
        };
    }

    public SimpleField(FieldNameSource fieldNameSource) {
        this.fieldNameSource = fieldNameSource;
    }

    public String getFieldName() {
        return fieldNameSource.getFieldName();
    }

    @Override
    public T getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        SolrDocument doc = context.getInstance(SolrDocDataSource.class).get();
        return (T) doc.getFieldValue(getFieldName());
    }

    @Override
    public Collection<T> getValues(ClassToInstanceMap<FieldDataSource<?>> context) {
        SolrDocument doc = context.getInstance(SolrDocDataSource.class).get();
        return (Collection<T>) doc.getFieldValues(getFieldName());
    }

    public MultivaluedField<T> multivalued() {
        return new MultivaluedFieldAdaptor<T>(this);
    }

    public void bindToContext(RigelContext rigelContext) {
        this.fieldNameSource.bindToContext(rigelContext);
    }

    public static interface FieldNameSource {

        public String getFieldName();

        public void bindToContext(RigelContext rigelContext);
    }

    /*
     * Literate Filter methods
     */
    public Filter isEqualTo(T value) {
        return null;
    }
}
