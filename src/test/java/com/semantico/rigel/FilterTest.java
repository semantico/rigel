package com.semantico.rigel;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.Range;
import com.semantico.rigel.fields.types.*;
import com.semantico.rigel.filters.Filter;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class FilterTest {

    private static final StringField TYPE = new StringField("type");
    private static final StringField NAME = new StringField("name");
    private static final IntegerField COUNT = new IntegerField("count");
    private static final DateField DATE = new DateField("date");

    @Test
    public void testIsEqualTo() {
        Filter filter;
        filter = TYPE.equalTo("hello");
        assertEquals("type:hello", filter.toSolrFormat());

        filter = NAME.equalTo("all the things");
        assertEquals("name:all\\ the\\ things", filter.toSolrFormat());

        filter = COUNT.equalTo(5);
        assertEquals("count:5", filter.toSolrFormat());

        Calendar c = Calendar.getInstance();
        c.set(2000, 0, 1, 0, 0, 0);//jan 1st 2000 00:00:00
        c.set(Calendar.MILLISECOND, 0);

        filter = DATE.equalTo(c.getTime());
        assertEquals("date:2000-01-01T00:00:00.000Z", filter.toSolrFormat());
    }

    @Test
    public void testStartsWith() {
        Filter filter;

        filter = TYPE.startsWith("Hello");
        assertEquals("type:Hello*", filter.toSolrFormat());

        filter = TYPE.startsWith("(ThingInBrackets)");
        assertEquals("type:\\(ThingInBrackets\\)*", filter.toSolrFormat());
    }

    @Test
    public void testRanges() {
        Filter filter;

        filter = COUNT.greaterThan(5);
        assertEquals("count:{5 TO *]", filter.toSolrFormat());

        filter = COUNT.atLeast(5);
        assertEquals("count:[5 TO *]", filter.toSolrFormat());

        filter = COUNT.lessThan(3);
        assertEquals("count:[* TO 3}", filter.toSolrFormat());

        filter = COUNT.atMost(3);
        assertEquals("count:[* TO 3]", filter.toSolrFormat());

        filter = COUNT.isInRange(Range.closed(10, 20));
        assertEquals("count:[10 TO 20]", filter.toSolrFormat());

        filter = COUNT.atLeast(5).andAtMost(10);
        assertEquals("count:[5 TO 10]", filter.toSolrFormat());

        filter = COUNT.greaterThan(16).andLessThan(21);
        assertEquals("count:{16 TO 21}", filter.toSolrFormat());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRange() {
        COUNT.lessThan(3).andGreaterThan(5);//Disjoint
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRange2() {
        COUNT.atLeast(100).andAtMost(99);//Disjoint
    }

    @Test
    public void testAndFilter() {
        Filter filter;

        filter = COUNT.atLeast(10).and(TYPE.startsWith("easy")).group();
        assertEquals("(count:[10 TO *] AND type:easy*)", filter.toSolrFormat());

        filter = COUNT.equalTo(6).and(TYPE.equalTo("void").and(NAME.equalTo("shiz")));
        assertEquals("count:6 AND type:void AND name:shiz", filter.toSolrFormat());
    }

    @Test
    public void testOrFilter() {
        Filter filter;

        filter = COUNT.atLeast(10).or(TYPE.startsWith("easy")).group();
        assertEquals("(count:[10 TO *] OR type:easy*)", filter.toSolrFormat());

        filter = COUNT.equalTo(6).or(TYPE.equalTo("void").or(NAME.equalTo("shiz")));
        assertEquals("count:6 OR type:void OR name:shiz", filter.toSolrFormat());
    }

    @Test
    public void testAssocitivity() {
        //When an expresssion is Ambigious we assume left-associativity
        //the precedence of AND and OR in solr is actually not well defined
        Filter filter;

        filter = COUNT.equalTo(5).and(TYPE.equalTo("void")).or(COUNT.equalTo(10));
        assertEquals("(count:5 AND type:void) OR count:10", filter.toSolrFormat());
    }
}
