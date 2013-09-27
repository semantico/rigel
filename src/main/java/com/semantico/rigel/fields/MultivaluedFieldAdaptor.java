package com.semantico.rigel.fields;

import com.google.common.base.Function;
import com.google.common.collect.ClassToInstanceMap;
import com.semantico.rigel.FieldDataSource;

import java.util.Collection;

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
    public Collection<R> getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        return delegate.getValues(context);
    }
}
