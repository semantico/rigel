package com.semantico.rigel.fields.types;

import com.semantico.rigel.fields.SimpleField;

public class StringField extends SimpleField<String> {

    public StringField(String fieldName) {
        super(fieldName);
    }

    public StringField(FieldNameSource fieldNameSource) {
        super(fieldNameSource);
    }
}
