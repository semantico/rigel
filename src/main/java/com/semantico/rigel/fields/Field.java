package com.semantico.rigel.fields;

import com.google.common.base.Function;
import com.google.common.collect.ClassToInstanceMap;
import com.semantico.rigel.FieldDataSource;

public interface Field<R> {

    String getFieldName();

    R getValue(ClassToInstanceMap<FieldDataSource<?>> context);
}
