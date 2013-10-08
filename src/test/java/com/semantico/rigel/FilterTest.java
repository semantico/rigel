package com.semantico.rigel;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
        c.set(2000, 0, 1, 0,0,0);//jan 1st 2000 00:00:00
        c.set(Calendar.MILLISECOND, 0);

        filter = DATE.equalTo(c.getTime());
        assertEquals("date:2000-01-01T00:00:00.000Z", filter.toSolrFormat());
    }
}
