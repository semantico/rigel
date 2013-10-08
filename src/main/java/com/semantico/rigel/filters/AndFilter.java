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

public class AndFilter extends ConnectiveFilter {

    private static final Joiner joiner = Joiner.on(" AND ");

    private List<Filter> filters;
    private Predicate<SolrDocument> predicate;

    public AndFilter(Filter... filters) {
        this(Arrays.asList(filters));
    }

    public AndFilter(Collection<Filter> input) {
        this.filters = Lists.newArrayList();
        for (Filter filter : input) {
            unpack(filter, filters);
        }
        this.predicate = Predicates.and(filters);
    }

    /**
     * Unpack the filters into one list.
     * Flatten the tree structure for Ands that are semantically the same as a single node
     *
     *  A              A
     * /\             /|\
     *O  A  ---->    O O O
     *  /\
     * O  O
     */
    private void unpack(Filter filter, Collection<Filter> filters) {
        if (filter instanceof AndFilter) {
            AndFilter and = (AndFilter) filter;
            for(Filter f : and.filters) {
                unpack(f, filters);
            }
        } else {
            filters.add(filter);
        }
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
        filters.add(filter);
        return this;
    }

    @Override
    public Filter or(Filter filter) {
        return new OrFilter(new GroupFilter(this), filter);
    }

    @Override
    protected List<Filter> getJoinedFilters() {
        return filters;
    }
}
