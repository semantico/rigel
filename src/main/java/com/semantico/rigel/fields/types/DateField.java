package com.semantico.rigel.fields.types;

import java.util.Date;

import org.apache.solr.common.util.DateUtil;

import com.google.common.base.Function;
import com.google.common.collect.Range;
import com.semantico.rigel.fields.RangeField;
import com.semantico.rigel.filters.BasicTerm;
import com.semantico.rigel.filters.Filter;
import com.semantico.rigel.filters.FilterUtils;
import com.semantico.rigel.filters.RangeTerm;

public class DateField extends RangeField<Date> {

    public static final Function<Date, String> DATE_TO_SOLR = new Function<Date, String>() {

        public String apply(Date input) {
            return DateUtil.getThreadLocalDateFormat().format(input);
        }
    };

    public DateField(String fieldName) {
        super(fieldName);
    }

    /*
     * Override the literate equals & range methods, dates have a custom format
     */

    @Override
    public BasicTerm equalTo(Date value) {
        return FilterUtils.isEqualTo(this, value, DATE_TO_SOLR);
    }

    @Override
    public RangeTerm<Date> greaterThan(Date value) {
        return FilterUtils.isInRange(this, Range.greaterThan(value), DATE_TO_SOLR);
    }

    @Override
    public RangeTerm<Date> lessThan(Date value) {
        return FilterUtils.isInRange(this, Range.lessThan(value), DATE_TO_SOLR);
    }

    @Override
    public RangeTerm<Date> atLeast(Date value) {
        return FilterUtils.isInRange(this, Range.atLeast(value), DATE_TO_SOLR);
    }

    @Override
    public RangeTerm<Date> atMost(Date value) {
        return FilterUtils.isInRange(this, Range.atMost(value), DATE_TO_SOLR);
    }

    @Override
    public BasicTerm isInRange(Range<Date> range) {
        return FilterUtils.isInRange(this, range, DATE_TO_SOLR);
    }
}
