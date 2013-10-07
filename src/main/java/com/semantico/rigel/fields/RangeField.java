package com.semantico.rigel.fields;

import com.google.common.collect.Range;
import com.semantico.rigel.filters.Filter;

public class RangeField<T extends Comparable<T>> extends SimpleField<T> {

    public RangeField(String fieldName) {
        super(fieldName);
    }

    public Filter greaterThan(T value) {
        return Filter.isInRange(this, Range.greaterThan(value));
    }

    public Filter lessThan(T value) {
        return Filter.isInRange(this, Range.lessThan(value));
    }

    public Filter atLeast(T value) {
        return Filter.isInRange(this, Range.atLeast(value));
    }

    public Filter atMost(T value) {
        return Filter.isInRange(this, Range.atMost(value));
    }
}
