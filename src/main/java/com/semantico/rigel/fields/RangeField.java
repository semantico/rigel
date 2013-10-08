package com.semantico.rigel.fields;

import com.google.common.collect.Range;
import com.semantico.rigel.filters.Filter;
import com.semantico.rigel.filters.RangeFilter;

public class RangeField<T extends Comparable<T>> extends SimpleField<T> {

    public RangeField(String fieldName) {
        super(fieldName);
    }

    public RangeFilter<T> greaterThan(T value) {
        return Filter.isInRange(this, Range.greaterThan(value));
    }

    public RangeFilter<T> lessThan(T value) {
        return Filter.isInRange(this, Range.lessThan(value));
    }

    public RangeFilter<T> atLeast(T value) {
        return Filter.isInRange(this, Range.atLeast(value));
    }

    public RangeFilter<T> atMost(T value) {
        return Filter.isInRange(this, Range.atMost(value));
    }

    public Filter isInRange(Range<T> range) {
        return Filter.isInRange(this, range);
    }
}
