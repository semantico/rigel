package com.semantico.rigel.filters;

import java.util.Arrays;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.*;

public class Or extends BooleanExpression {

    private static final Joiner joiner = Joiner.on(" OR ");

    private Iterable<? extends Filter> filters;

    Or(Filter... filters) {//Deliberately Package-Private
        this(Arrays.asList(filters));
    }

    Or(Iterable<? extends Filter> filters) {//Deliberately Package-Private
        checkArgument(Iterables.size(filters) > 0, "OR must have at least one clause");
        this.filters = filters;
    }

    @Override
    public boolean apply(SolrDocument input) {
        boolean result = false;
        for (Filter filter : filters) {
            result |= filter.apply(input);
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

    public Or or(BasicTerm term) {
        return new Or(this, term);
    }

    public Or or(Or expr) {
        return new Or(this, expr);
    }
}
