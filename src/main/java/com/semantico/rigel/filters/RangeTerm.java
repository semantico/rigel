package com.semantico.rigel.filters;

import java.util.Set;


import com.google.common.base.Function;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.semantico.rigel.fields.Field;

public class RangeTerm<T extends Comparable<T>> extends FieldBasedTerm<T> {

    private final Range<T> range;
    private final Function<? super T, String> valueToSolrFormat;

    public RangeTerm(Field<T> field, Range<T> range, Function<? super T, String> valueToSolrFormat) {
        super(field);
        this.range = range;
        this.valueToSolrFormat = valueToSolrFormat;
    }

    @Override
    public boolean decide(T actual) {
        return actual != null && range.contains(actual);
    }

    @Override
    public String toSolrFormat() {
        final String upperBound = range.hasUpperBound() ? valueToSolrFormat.apply(range.upperEndpoint()) : "*";
        final String lowerBound = range.hasLowerBound() ? valueToSolrFormat.apply(range.lowerEndpoint()) : "*";
        final String lowerBoundChar = !range.hasLowerBound() || range.lowerBoundType() == BoundType.CLOSED ? "[" : "{";
        final String upperBoundChar = !range.hasUpperBound() || range.upperBoundType() == BoundType.CLOSED ? "]" : "}";
        return String.format("%s:%s%s TO %s%s", field.getFieldName(), lowerBoundChar, lowerBound, upperBound, upperBoundChar);
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return ImmutableSet.<Field<?>>of(field);
    }

    public Filter andGreaterThan(T value) {
        return new RangeTerm<T>(field, Range.greaterThan(value).intersection(range),valueToSolrFormat);
    }

    public Filter andLessThan(T value) {
        return new RangeTerm<T>(field, Range.lessThan(value).intersection(range),valueToSolrFormat);
    }

    public Filter andAtLeast(T value) {
        return new RangeTerm<T>(field, Range.atLeast(value).intersection(range),valueToSolrFormat);
    }

    public Filter andAtMost(T value) {
        return new RangeTerm<T>(field, Range.atMost(value).intersection(range),valueToSolrFormat);
    }
}
