package com.semantico.rigel;

import com.google.common.base.Function;
import com.semantico.rigel.fields.Field;


public interface FieldKey<R, F> extends DataKey<F>, Function<ContentItem, F>{

    Field<R> getField();

    String getMeta(String key);

    F formatRawValue(R rawValue);
}
