package com.semantico.sipp2.solr.fields;

import com.google.common.base.Function;
import com.google.common.collect.ClassToInstanceMap;
import com.semantico.sipp2.solr.FieldDataSource;

import javax.annotation.Nullable;

public interface Field<R> {

    String getFieldName();

    @Nullable
    R getValue(ClassToInstanceMap<FieldDataSource<?>> context);

}
