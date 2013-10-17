package com.semantico.rigel.filters;

import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.SolrDocDataSource;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.filters.BooleanExpression.AmbiguousExpressionException;

/*
 * basic terms can be modified or used in a boolean expression
 * modified terms can only be used in a term list
 */
public abstract class BasicTerm implements Term {

    /**
     * Helper method to wrap the solr doc up in a context & get the value using the field
     */
    protected <T> T getFieldValue(Field<T> field, SolrDocument doc) {
        ImmutableClassToInstanceMap.Builder<FieldDataSource<?>> context = ImmutableClassToInstanceMap.builder();
        context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
        return field.getValue(context.build());
    }

    @Override
    public boolean isOptional() {
        return true;
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

    public BooleanExpression and(BasicTerm term) {
        return new And(this, term);
    }

    public BooleanExpression and(BooleanExpression expr) {
        if (expr instanceof Or) {
            throw new AmbiguousExpressionException(new And(this, expr));
        }
        return new And(this, expr);
    }

    public BooleanExpression or(BasicTerm term) {
        return new Or(this, term);
    }

    public BooleanExpression or(BooleanExpression expr) {
        if (expr instanceof And) {
            throw new AmbiguousExpressionException(new Or(this, expr));
        }
        return new Or(this, expr);
    }
}