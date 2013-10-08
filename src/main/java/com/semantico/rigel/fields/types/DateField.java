package com.semantico.rigel.fields.types;

import java.util.Date;

import org.apache.solr.common.util.DateUtil;

import com.google.common.base.Function;
import com.google.common.collect.Range;
import com.semantico.rigel.fields.RangeField;
import com.semantico.rigel.filters.Filter;

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

    public Filter equalTo(Date value) {
        return Filter.isEqualTo(this, value, DATE_TO_SOLR);
    }

    public Filter greaterThan(Date value) {
        return Filter.isInRange(this, Range.greaterThan(value), DATE_TO_SOLR);
    }

    public Filter lessThan(Date value) {
        return Filter.isInRange(this, Range.lessThan(value), DATE_TO_SOLR);
    }

    public Filter atLeast(Date value) {
        return Filter.isInRange(this, Range.atLeast(value), DATE_TO_SOLR);
    }

    public Filter atMost(Date value) {
        return Filter.isInRange(this, Range.atMost(value), DATE_TO_SOLR);
    }
}
