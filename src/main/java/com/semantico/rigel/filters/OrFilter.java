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

    public OrFilter(Collection<Filter> input) {
        this.filters = Lists.newArrayList();
        for (Filter filter : input) {
            unpack(filter, filters);
        }
        this.predicate = Predicates.or(filters);
    }

    /**
     * Unpack the filters into one list.
     * Flatten the tree structure for Or that are semantically the same as a single node
     *
     *  A              A
     * /\             /|\
     *O  A  ---->    O O O
     *  /\
     * O  O
     */
    private void unpack(Filter filter, Collection<Filter> filters) {
        if (filter instanceof OrFilter) {
            OrFilter or = (OrFilter) filter;
            for(Filter f : or.filters) {
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
