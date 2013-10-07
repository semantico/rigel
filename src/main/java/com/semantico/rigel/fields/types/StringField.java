package com.semantico.rigel.fields.types;

import com.semantico.rigel.fields.RangeField;
import com.semantico.rigel.filters.Filter;

public class StringField extends RangeField<String> {

    public StringField(String fieldName) {
        super(fieldName);
    }

    /*
     * Literate Filter methods
     */

    public Filter startsWith(String value) {
        return Filter.startsWith(this, value);
    }
}
