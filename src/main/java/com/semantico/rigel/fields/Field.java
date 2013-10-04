package com.semantico.rigel.fields;

import com.google.common.collect.ClassToInstanceMap;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.RigelContext;

public interface Field<R> {

    String getFieldName();

    R getValue(ClassToInstanceMap<FieldDataSource<?>> context);

}
