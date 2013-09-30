package com.semantico.rigel.fields.types;

import java.util.Date;

import com.semantico.rigel.fields.SimpleField;

public class DateField extends SimpleField<Date> {

    public DateField(String fieldName) {
        super(fieldName);
    }

    public DateField(FieldNameSource fieldNameSource) {
        super(fieldNameSource);
    }
}
