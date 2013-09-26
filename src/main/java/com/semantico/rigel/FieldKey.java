package com.semantico.sipp2.solr;

import com.google.common.base.Function;
import com.semantico.sipp2.solr.fields.Field;


public interface FieldKey<R, F> extends DataKey<F> {

    Field<R> getField();

    String getMeta(String key);

    F formatRawValue(R rawValue);

    Function<R,F> getTransformFunc();
}
