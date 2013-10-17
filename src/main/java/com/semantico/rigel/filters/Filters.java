package com.semantico.rigel.filters;

/**
 * Import these static methods to aid in creating filters
 */
public final class Filters {


    /*
     * --Basic Term--
     * title:hello
     * title:[* TO 3]
     * (title:hello AND some:thing)
     * (+title:hello +some:thing)
     *
     * --Modified Term--
     * -title:hello
     * +title:hello
     *
     * --Boolean Expression--
     * title:hello OR title:goodbye
     * BasicTerm AND BasicTerm
     *
     * filter(Term...)
     * filter(BoolExpression)
     */

    private Filters(){};//Cant instansiate me

    public static ModifiedTerm require(BasicTerm term) {
        return new RequiredTerm(term);
    }

    public static ModifiedTerm prohibit(BasicTerm term) {
        return new ProhibitedTerm(term);
    }

    public static BasicTerm group(Term... terms) {
        return new GroupTerm(new TermList(terms));
    }

    public static BasicTerm group(Filter filter) {
        return new GroupTerm(filter);
    }

    public static BasicTerm everything() {
        return new EverythingTerm();
    }

}
