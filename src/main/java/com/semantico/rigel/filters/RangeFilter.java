package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Function;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.semantico.rigel.fields.Field;

public class RangeFilter<T extends Comparable<T>> extends Filter {

    private final Field<T> field;
    private final Range<T> range;
    private final Function<? super T, String> valueToSolrFormat;

    public RangeFilter(Field<T> field, Range<T> range, Function<? super T, String> valueToSolrFormat) {
        this.field = field;
        this.range = range;
        this.valueToSolrFormat = valueToSolrFormat;
    }

    @Override
    public boolean apply(SolrDocument input) {
        T actual = getFieldValue(field, input);
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
}
