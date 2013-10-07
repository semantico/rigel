package com.semantico.rigel.filters;

import java.util.Collection;
import java.util.Set;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.Range;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.SolrDocDataSource;
import com.semantico.rigel.fields.Field;
/**
 * A Filter represents a filter over a solr field, it is also a predicate over solr documents.
 * Filters have the contract that the predicate must be true for solr documents returned from the query it produces.
 * put more simply: the predicate must emulate the solr query
 */
public abstract class Filter implements Predicate<SolrDocument> {

    public abstract String toSolrFormat();

    public abstract Set<Field<?>> getAffectedFields();

    public Filter and(Filter filter) {
        return new AndFilter(this, filter);
    }

    public Filter or(Filter filter) {
        return new OrFilter(this, filter);
    }

    public Filter group() {
        return new GroupFilter(this);
    }

    /**
     * Helper method to wrap the solr doc up in a context & get the value using the field
     */
    protected <T> T getFieldValue(Field<T> field, SolrDocument doc) {
        ImmutableClassToInstanceMap.Builder<FieldDataSource<?>> context = ImmutableClassToInstanceMap.builder();
        context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
        return field.getValue(context.build());
    }

    protected String escapeQueryChars(String input) {
        return ClientUtils.escapeQueryChars(input);
    }

    /*
     * Static methods for constructing filters. shield you from the actual type
     */
    public static <T> Filter isEqualTo(Field<T> field, T value) {
        return new EqualsFilter<T>(field, value);
    }

    public static Filter startsWith(Field<String> field, String value) {
        return new StartsWithFilter(field, value);
    }

    public static <T extends Comparable<T>> Filter isInRange(Field<T> field, Range<T> range) {
        return new RangeFilter<T>(field, range);
    }

    public static Filter everything() {
        return new EverythingFilter();
    }

    public static Filter or(Filter... filters) {
        return new OrFilter(filters);
    }

    public static Filter or(Collection<Filter> filters) {
        return new OrFilter(filters);
    }

    public static Filter and(Filter... filters) {
        return new AndFilter(filters);
    }

    public static Filter and(Collection<Filter> filters) {
        return new AndFilter(filters);
    }
}
