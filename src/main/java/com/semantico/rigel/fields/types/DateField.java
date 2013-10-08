package com.semantico.rigel.fields.types;

import java.util.Date;

import org.apache.solr.common.util.DateUtil;

import com.google.common.base.Function;
import com.google.common.collect.Range;
import com.semantico.rigel.fields.RangeField;
import com.semantico.rigel.filters.Filter;
import com.semantico.rigel.filters.RangeFilter;

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
    public Filter equalTo(Date value) {
        return Filter.isEqualTo(this, value, DATE_TO_SOLR);
    }

    @Override
    public RangeFilter<Date> greaterThan(Date value) {
        return Filter.isInRange(this, Range.greaterThan(value), DATE_TO_SOLR);
    }

    @Override
    public RangeFilter<Date> lessThan(Date value) {
        return Filter.isInRange(this, Range.lessThan(value), DATE_TO_SOLR);
    }

    @Override
    public RangeFilter<Date> atLeast(Date value) {
        return Filter.isInRange(this, Range.atLeast(value), DATE_TO_SOLR);
    }

    @Override
    public RangeFilter<Date> atMost(Date value) {
        return Filter.isInRange(this, Range.atMost(value), DATE_TO_SOLR);
    }

    @Override
    public Filter isInRange(Range<Date> range) {
        return Filter.isInRange(this, range, DATE_TO_SOLR);
    }
}
