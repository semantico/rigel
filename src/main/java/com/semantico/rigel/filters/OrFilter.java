package com.semantico.rigel.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class OrFilter extends ConnectiveFilter {

    private static final Joiner joiner = Joiner.on(" OR ");

    private List<Filter> filters;
    private Predicate<SolrDocument> predicate;

    public OrFilter(Filter... filters) {
        this(Arrays.asList(filters));
    }

    public OrFilter(Collection<Filter> filters) {
        this.filters = Lists.newArrayList(filters);
        this.predicate = Predicates.or(filters);
    }

    @Override
    public boolean apply(SolrDocument input) {
        return predicate.apply(input);
    }

    @Override
    public String toSolrFormat() {
        return joiner.join(getSolrFilters());
    }

    @Override
    public Filter and(Filter filter) {
        return new AndFilter(new GroupFilter(this), filter);
    }

    @Override
    public Filter or(Filter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    protected List<Filter> getJoinedFilters() {
        return filters;
    }
}
