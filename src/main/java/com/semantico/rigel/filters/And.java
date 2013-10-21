package com.semantico.rigel.filters;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class And extends BooleanExpression {

    private static final Joiner joiner = Joiner.on(" AND ");

    private Iterable<? extends Filter> filters;

    And(Filter... filters) {//Deliberately Package-Private
        this(Arrays.asList(filters));
    }

    And(Iterable<? extends Filter> filters) {//Deliberately Package-Private
        checkArgument(Iterables.size(filters) > 0, "AND must have at least one clause");
        this.filters = filters;
    }

    @Override
    public boolean apply(SolrDocument input) {
        boolean result = true;
        for (Filter filter : filters) {
            result &= filter.apply(input);
        }
        return result;
    }

    @Override
    public String toSolrFormat() {
        return joiner.join(getSolrFilters());
    }

    @Override
    protected Iterable<? extends Filter> getJoinedTerms() {
        return filters;
    }

    /*
     * Fluent Boolean methods
     *
     * The concrete classes combine filters, but
     * the fluent api restricts this to Basic terms
     * and boolean expressions. This prevents you
     * from mixing (prohibited / required) with
     * (and / or / not) unless you use group them
     * using parentheses
     *
     * The constructors for And & Or are package-private
     * to prevent these checks from being circumvented
     */

    public And and(BasicTerm term) {
        return new And(this, term);
    }

    public And and(And expr) {
        return new And(this, expr);
    }
}
