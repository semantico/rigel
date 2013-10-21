package com.semantico.rigel.filters;

import java.util.Collection;

import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.SolrDocDataSource;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.fields.MultivaluedFieldAdaptable;
import com.semantico.rigel.fields.MultivaluedFieldAdaptor;

import static com.google.common.base.Preconditions.*;

public abstract class FieldBasedTerm<T> extends BasicTerm {

    protected final Field<T> field;

    public FieldBasedTerm(Field<T> field) {
        checkNotNull(field);
        this.field = field;
    }

    /*
     * Handles getting the value out of the solr doc and deals with multivalued fields,
     * subclasses implement the decide function
     */
    @Override
    public boolean apply(SolrDocument doc) {
        if (field instanceof MultivaluedFieldAdaptable) {
            Collection<T> actuals = getFieldValue(new MultivaluedFieldAdaptor<T>((MultivaluedFieldAdaptable<T>)field), doc);
            if (actuals == null) {
                return false;
            }
            for (T actual : actuals) {
                if (decide(actual)) {
                    return true;
                }
            }
            return false;
        } else {
            T actual = getFieldValue(field, doc);
            if (actual == null) {
                return false;
            }
            return decide(actual);
        }
    }

    protected abstract boolean decide(T actual);

    /**
     * Helper method to wrap the solr doc up in a context & get the value using the field
     */
    private <R> R getFieldValue(Field<R> field, SolrDocument doc) {
        ImmutableClassToInstanceMap.Builder<FieldDataSource<?>> context = ImmutableClassToInstanceMap.builder();
        context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
        return field.getValue(context.build());
    }

}
