package com.semantico.rigel.fields.types;

import com.semantico.rigel.fields.SimpleField;

public class LongField extends SimpleField<Long> {

    public LongField(String fieldName) {
        super(fieldName);
    }

    public LongField(FieldNameSource fieldNameSource) {
        super(fieldNameSource);
    }
}
