package com.semantico.rigel.filters;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.semantico.rigel.fields.Field;

public abstract class BooleanExpression implements Filter {

    protected abstract Iterable<? extends Filter> getJoinedTerms();

    public Set<Field<?>> getAffectedFields() {
        ImmutableSet.Builder<Field<?>> builder = ImmutableSet.builder();

        for (Filter filter : getJoinedTerms()) {
            builder.addAll(filter.getAffectedFields());
        }
        return builder.build();
    }

    protected List<String> getSolrFilters() {
        List<String> solrFilters = Lists.newArrayList();

        for (Filter filter : getJoinedTerms()) {
            String solrFilter = filter.toSolrFormat();
            if (StringUtils.isNotBlank(solrFilter)) {//isn't just whitespace
                solrFilters.add(solrFilter);
            }
        }
        return solrFilters;
    }

    public static class AmbiguousExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public AmbiguousExpressionException(BooleanExpression attemptedExpr) {
            super(String.format("The expression \"%s\" is ambigious, group the terms first",
                attemptedExpr.toSolrFormat()));
        }
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
     * these methods fail fast when you try to create an
     * ambiguous expression.
     *
     * The constructors for And & Or are package-private
     * to prevent these checks from being circumvented
     */

    public abstract BooleanExpression and(BasicTerm term);

    public abstract BooleanExpression and(BooleanExpression expr);

    public abstract BooleanExpression or(BasicTerm term);

    public abstract BooleanExpression or(BooleanExpression expr);
}
