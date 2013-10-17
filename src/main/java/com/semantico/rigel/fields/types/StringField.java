package com.semantico.rigel.fields.types;

import com.semantico.rigel.fields.RangeField;
import com.semantico.rigel.filters.BasicTerm;
import com.semantico.rigel.filters.FilterUtils;

public class StringField extends RangeField<String> {

    public StringField(String fieldName) {
        super(fieldName);
    }

    /*
     * Literate Filter methods
     */

    public BasicTerm startsWith(String value) {
        return FilterUtils.startsWith(this, value);
    }
}
