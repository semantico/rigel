package com.semantico.rigel.fields;

import com.google.common.base.Function;
import com.google.common.collect.ClassToInstanceMap;
import com.semantico.rigel.FieldDataSource;

import java.util.Collection;

/**
 * This interface indicates a field that can be adapted to provide multiple
 * values
 */
public interface MultivaluedFieldAdaptable<R> extends Field<R> {

    Collection<R> getValues(ClassToInstanceMap<FieldDataSource<?>> context);

}
