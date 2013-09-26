package com.semantico.sipp2.solr.fields;

import com.google.common.base.Function;
import com.google.common.collect.ClassToInstanceMap;
import com.semantico.sipp2.solr.FieldDataSource;

import java.util.Collection;

import javax.annotation.Nullable;

public class MultivaluedFieldAdaptor<R> implements MultivaluedField<R> {

    private final MultivaluedFieldAdaptable<R> delegate;

    public MultivaluedFieldAdaptor(MultivaluedFieldAdaptable<R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getFieldName() {
        return delegate.getFieldName();
    }

    @Override
    @Nullable
    public Collection<R> getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        return delegate.getValues(context);
    }
}
