package com.semantico.rigel.filters;

/*
 * basic terms can be modified or used in a boolean expression
 * modified terms can only be used in a term list
 */
public abstract class BasicTerm implements Term {

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
     * The constructors for And & Or are package-private
     * to prevent these checks from being circumvented
     */

    public And and(BasicTerm term) {
        return new And(this, term);
    }

    public And and(And expr) {
        return new And(this, expr);
    }

    public Or or(BasicTerm term) {
        return new Or(this, term);
    }

    public Or or(Or expr) {
        return new Or(this, expr);
    }
}
