package com.semantico.rigel.fields;

import com.google.common.collect.Range;
import com.semantico.rigel.filters.Filter;
import com.semantico.rigel.filters.FilterUtils;
import com.semantico.rigel.filters.RangeTerm;

public class RangeField<T extends Comparable<T>> extends SimpleField<T> {

    public RangeField(String fieldName) {
        super(fieldName);
    }

    public RangeTerm<T> greaterThan(T value) {
        return FilterUtils.isInRange(this, Range.greaterThan(value));
    }

    public RangeTerm<T> lessThan(T value) {
        return FilterUtils.isInRange(this, Range.lessThan(value));
    }

    public RangeTerm<T> atLeast(T value) {
        return FilterUtils.isInRange(this, Range.atLeast(value));
    }

    public RangeTerm<T> atMost(T value) {
        return FilterUtils.isInRange(this, Range.atMost(value));
    }

    public Filter isInRange(Range<T> range) {
        return FilterUtils.isInRange(this, range);
    }
}
