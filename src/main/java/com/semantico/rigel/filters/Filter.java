package com.semantico.sipp2.solr.filters;

import org.apache.solr.client.solrj.util.ClientUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.semantico.sipp2.solr.FieldDataSource;
import com.semantico.sipp2.solr.SolrDocDataSource;
import com.semantico.sipp2.solr.fields.Field;
/**
 * A Filter represents a filter over a solr field, it is also a predicate over solr documents.
 * Filters have the contract that the predicate must be true for solr documents returned from the query it produces.
 * put more simply: the predicate must emulate the solr query
 */
public abstract class Filter implements Predicate<SolrDocument> {

    public abstract String toSolrFormat();

    public abstract Set<Field<?>> getAffectedFields();

    public static <T> Filter on(final Field<T> field, final T value) {
        return new Filter() {

            public boolean apply(@Nullable SolrDocument input) {
                T actual = getFieldValue(field, input);
                return value.equals(actual);
            }

            public String toSolrFormat() {
                return String.format("%s:%s", field.getFieldName(), ClientUtils.escapeQueryChars(value.toString()));
            }

            public Set<Field<?>> getAffectedFields() {
                return ImmutableSet.<Field<?>>of(field);
            }
        };
    }

    public static <T extends Comparable<T>> Filter on(final Field<T> field, final Range<T> range) {
        return Filter.on(field, range, Functions.toStringFunction());
    }

    public static <T extends Comparable<T>> Filter on(final Field<T> field, final Range<T> range, final Function<? super T, String> valueToSolrFormat) {

        return new Filter() {

            public boolean apply(@Nullable SolrDocument input) {
                T actual = getFieldValue(field, input);
                return actual != null && range.contains(actual);
            }

            public String toSolrFormat() {
                final String upperBound = range.hasUpperBound() ? valueToSolrFormat.apply(range.upperEndpoint()) : "*";
                final String lowerBound = range.hasLowerBound() ? valueToSolrFormat.apply(range.lowerEndpoint()) : "*";
                final String lowerBoundChar = !range.hasLowerBound() || range.lowerBoundType() == BoundType.CLOSED ? "[" : "{";
                final String upperBoundChar = !range.hasUpperBound() || range.upperBoundType() == BoundType.CLOSED ? "]" : "}";
                return String.format("%s:%s%s TO %s%s", field.getFieldName(), lowerBoundChar, lowerBound, upperBound, upperBoundChar);
            }

            public Set<Field<?>> getAffectedFields() {
                return ImmutableSet.<Field<?>>of(field);
            }
        };
    }

    public static Filter and(Filter...filters ) {
        return Filter.join(Connective.AND, filters);
    }

    public static Filter and(Iterable<Filter> filters) {
        return Filter.join(Connective.AND, filters);
    }

    public static Filter or(Filter...filters) {
        return Filter.join(Connective.OR, filters);
    }

    public static Filter or(Iterable<Filter> filters) {
        return Filter.join(Connective.OR, filters);
    }

    private static enum Connective {
        AND {
            public <T> Predicate<T> join(Iterable<? extends Predicate<T>> predicates) {
                return Predicates.and(predicates);
            }
        }, OR {
            public <T> Predicate<T> join(Iterable<? extends Predicate<T>> predicates) {
                return Predicates.or(predicates);
            }
        };

        public abstract <T> Predicate<T> join(Iterable<? extends Predicate<T>> predicates);
    }

    private static Filter join(Connective connective, Filter... filters) {
        return Filter.join(connective, Arrays.asList(filters));
    }

    private static Filter join(final Connective connective, final Iterable<Filter> filters) {
        return new Filter() {

            private final Predicate<SolrDocument> predicate = connective.join(filters);

            public boolean apply(@Nullable SolrDocument input) {
                return predicate.apply(input);
            }

            public String toSolrFormat() {
                Joiner joiner = Joiner.on(String.format(" %s ", connective.name()));

                Function<Filter,String> toSolrFormat = new Function<Filter,String>() {
                    public String apply(@Nullable Filter input) {
                        return input.toSolrFormat();
                    }
                };
                return String.format("(%s)", joiner.join(Iterables.transform(filters, toSolrFormat)));
            }

            public Set<Field<?>> getAffectedFields() {
                Builder<Field<?>> list = ImmutableSet.builder();
                for(Filter filter : filters) {
                    list.addAll(filter.getAffectedFields());
                }
                return list.build();
            }
        };
    }

    /**
     * Helper method to wrap the solr doc up in a context & get the value using the field
     */
    private static <T> T getFieldValue(Field<T> field, SolrDocument doc) {
        ImmutableClassToInstanceMap.Builder<FieldDataSource<?>> context = ImmutableClassToInstanceMap.builder();
        context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
        return field.getValue(context.build());
    }
}
