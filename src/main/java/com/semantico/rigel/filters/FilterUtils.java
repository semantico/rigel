package com.semantico.rigel.filters;

import java.util.Collection;

import org.apache.solr.client.solrj.util.ClientUtils;

import com.google.common.base.Function;
import com.google.common.collect.Range;
import com.semantico.rigel.fields.Field;


/**
 * Utilities for working with filters, Not intended for use by client code!
 * These methods are less safe than the fluent methods and the static methods
 * provided in Filters.java .
 *
 * mostly factory methods
 */
public final class FilterUtils {

    public static final Function<Object, String> DEFAULT_TO_SOLR = new Function<Object, String>() {

        public String apply(Object input) {
            return ClientUtils.escapeQueryChars(input.toString());
        }
    };

    private FilterUtils(){};//Cant instansiate me

    public static <T> BasicTerm isEqualTo(Field<T> field, T value, Function<? super T, String> toSolrFormatFunc) {
        return new EqualsTerm<T>(field, value, toSolrFormatFunc);
    }

    public static <T> BasicTerm isEqualTo(Field<T> field, T value) {
        return isEqualTo(field, value, DEFAULT_TO_SOLR);
    }

    public static BasicTerm startsWith(Field<String> field, String value) {
        return new StartsWithTerm(field, value);
    }

    public static <T extends Comparable<T>> RangeTerm<T> isInRange(Field<T> field, Range<T> range, Function<? super T, String> toSolrFormatFunc) {
        return new RangeTerm<T>(field, range, toSolrFormatFunc);
    }

    public static <T extends Comparable<T>> RangeTerm<T> isInRange(Field<T> field, Range<T> range) {
        return isInRange(field, range, DEFAULT_TO_SOLR);
    }

    public static Filter joinTerms(Term... terms) {
        return new TermList(terms);
    }

    public static BooleanExpression or(Filter...filters) {
        return new Or(filters);
    }

    public static BooleanExpression or(Iterable<Filter> filters) {
        return new Or(filters);
    }

    public static BooleanExpression and(Filter...filters) {
        return new And(filters);
    }

    public static BooleanExpression and(Collection<Filter> filters) {
        return new And(filters);
    }
}
