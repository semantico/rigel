package com.semantico.rigel.facets;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.util.DateUtil;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.semantico.rigel.DateMathParser;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.filters.Filter;

/*
 * R = The raw type of the field, its comparable e.g. Long, Integer
 * F = the formatted type (what the transorm func turns our Range<R> into)
 * B = The solrj range facet type (Number or Date)
 * G = The solrj gap type, Number or String
 *
 * couldnt combine R and B, because Number isnt comparable.. grrrrrr
 */
public abstract class RangeFacetResults<R extends Comparable<R>, F, B, G>
        extends FacetResults<Range<R>, F> {

    private final List<Count<Range<R>, F>> values;

    public RangeFacetResults(RangeFacet<B, G> rangeFacet, Function<Range<R>, F> transformFunc, Field<?> field) {
        super(field);

        ImmutableList.Builder<Count<Range<R>, F>> builder = ImmutableList.builder();
        G gap = rangeFacet.getGap();
        B rangeEnd = rangeFacet.getEnd();

        for (RangeFacet.Count count : rangeFacet.getCounts()) {
            R start = unmarshall(count.getValue());
            R end = getRangeEnd(start, gap, rangeEnd);
            //Default range inclusion is "lower"
            //lower bound inclusive, upper bound exclusive
            Range<R> range = Range.closedOpen(start, end);
            F formattedValue = transformFunc.apply(range);

            builder.add(new RangeCount<R, F>(field, range, formattedValue, Long.valueOf(count.getCount())));
        }
        values = builder.build();
    }

    public List<Count<Range<R>, F>> getValues() {
        return values;
    }

    protected abstract R getRangeEnd(R countStart, G gap, B rangeEnd);

    protected abstract R unmarshall(String input);

    public static class IntegerFacet<F> extends
            RangeFacetResults<Integer, F, Number, Number> {

        public IntegerFacet(RangeFacet<Number, Number> rangeFacet,
                Function<Range<Integer>, F> transformFunc, Field<?> field) {
            super(rangeFacet, transformFunc, field);
        }

        @Override
        protected Integer getRangeEnd(Integer countStart, Number gap, Number rangeEnd) {
            Integer end = Integer.valueOf(gap.intValue() + countStart.intValue());
            if (end.compareTo(rangeEnd.intValue()) < 0) {
                return end;
            } else {
                return rangeEnd.intValue();
            }
        }

        @Override
        protected Integer unmarshall(String input) {
            return Integer.valueOf(input);
        }
    }

    public static class LongFacet<F> extends
            RangeFacetResults<Long, F, Number, Number> {

        public LongFacet(RangeFacet<Number, Number> rangeFacet,
                Function<Range<Long>, F> transformFunc, Field<?> field) {
            super(rangeFacet, transformFunc, field);
        }

        @Override
        protected Long getRangeEnd(Long countStart, Number gap, Number rangeEnd) {
            Long end = Long.valueOf(gap.longValue() + countStart.longValue());
            if (end.compareTo(rangeEnd.longValue()) < 0) {
                return end;
            } else {
                return rangeEnd.longValue();
            }
        }

        @Override
        protected Long unmarshall(String input) {
            return Long.valueOf(input);
        }
    }

    public static class DateFacet<F> extends
            RangeFacetResults<Date, F, Date, String> {

        public DateFacet(RangeFacet<Date, String> rangeFacet,
                Function<Range<Date>, F> transformFunc, Field<?> field) {
            super(rangeFacet, transformFunc, field);
        }

        @Override
        protected Date getRangeEnd(Date countStart, String gap, Date rangeEnd) {
            //DateMathParser is copied from solr, so we can
            //determine the end of the range
            DateMathParser parser = new DateMathParser();
            parser.setNow(countStart);
            try {
                return parser.parseMath(gap);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected Date unmarshall(String input) {
            try {
                return DateUtil.parseDate(input);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class RangeCount<R extends Comparable<R>, F> extends FacetResults.Count<Range<R>, F> {

        public RangeCount(Field<?> field, Range<R> rawValue, F formattedValue, long count) {
            super(field, rawValue, formattedValue, count);
        }

        @Override
        public Filter toFilter() {
            //TODO return a range filter
            //the type of field & the type of range need to be different type parameters,
            //but the builder methods can still combine the two.
            return null;
        }
    }
}
