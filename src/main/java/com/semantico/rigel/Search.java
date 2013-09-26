package com.semantico.sipp2.solr;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.semantico.sipp2.solr.facets.FacetResults;
import com.semantico.sipp2.solr.facets.FieldFacetResults;
import com.semantico.sipp2.solr.facets.RangeFacetResults;
import com.semantico.sipp2.solr.fields.Field;
import com.semantico.sipp2.solr.fields.types.*;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.util.DateUtil;

import static com.google.common.base.Preconditions.*;

public abstract class Search extends FieldSet {

    protected final Set<FacetKey<?, ?>> facets;

    public Search() {
        super();
        this.facets = Sets.newHashSet();
    }

    /*
     * Massive ball ache numero uno
     * Solrj gives you back facet results as strings. thus for each type of field we need to know how to turn
     * that string back into a meaningful value.
     */

    protected FieldFacetKeyBuilder<String, String> facet(StringField field) {
        return new FieldFacetKeyBuilder<String, String>(field,
                Functions.<String> identity(), Functions.<String> identity());
    }

    protected FieldFacetKeyBuilder<Date, Date> facet(DateField field) {
        Function<String, Date> unmarshallFunc = new Function<String, Date>() {
            @Override
            @Nullable
            public Date apply(@Nullable String input) {
                try {
                    return DateUtil.parseDate(input);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return new FieldFacetKeyBuilder<Date, Date>(field, unmarshallFunc, Functions.<Date> identity());
    }

    protected FieldFacetKeyBuilder<Integer, Integer> facet(IntegerField field) {
        Function<String, Integer> unmarshallFunc = new Function<String, Integer>() {
            @Override
            @Nullable
            public Integer apply(@Nullable String input) {
                return Integer.valueOf(input);
            }
        };
        return new FieldFacetKeyBuilder<Integer, Integer>(field, unmarshallFunc, Functions.<Integer> identity());
    }

    protected FieldFacetKeyBuilder<Long, Long> facet(LongField field) {
        Function<String, Long> unmarshallFunc = new Function<String, Long>() {
            @Override
            @Nullable
            public Long apply(@Nullable String input) {
                return Long.valueOf(input);
            }
        };
        return new FieldFacetKeyBuilder<Long, Long>(field, unmarshallFunc, Functions.<Long> identity());
    }

    protected class FieldFacetKeyBuilder<R, F> {

        private final Field<R> field;
        private final Function<R, F> transformFunc;
        private final Function<String, R> unmarshallFunc;

        public FieldFacetKeyBuilder(Field<R> field, Function<String, R> unmarshallFunc, Function<R, F> transformFunc) {
            this.transformFunc = transformFunc;
            this.unmarshallFunc = unmarshallFunc;
            this.field = field;
        }

        public FacetKey<R, F> build() {
            FacetKey<R, F> key = new FacetKey<R, F>(field, transformFunc) {
                @Override
                protected FacetResults<R, F> buildFacetResults(QueryResponse response) {
                    FacetField facetField = response.getFacetField(this.field.getFieldName());
                    return new FieldFacetResults<R, F>(facetField, unmarshallFunc, this.transformFunc, this.field);
                }

                @Override
                protected void addToSolrQuery(SolrQuery q) {
                    q.addFacetField(field.getFieldName());
                }
            };
            facets.add(key);
            return key;
        }

        public <T> FieldFacetKeyBuilder<R, T> transformLike(FieldKey<F, T> fieldKey) {
            return new FieldFacetKeyBuilder<R, T>(field, unmarshallFunc, Functions.compose(fieldKey.getTransformFunc(),
                            transformFunc));
        }

        public <T> FieldFacetKeyBuilder<R, T> transform(Function<F, T> func) {
            return new FieldFacetKeyBuilder<R, T>(field, unmarshallFunc, Functions.compose(func, transformFunc));
        }
    }


    /*
     * Range Facets
     *
     * dont worry about those nulls, the type forces you to initialize those variables before a usable object is returned
     */

    protected RangeFacetBuilderStart<Integer, Range<Integer>, Integer> rangeFacet(IntegerField field) {
        return new IntegerRangeFacetBuilder<Range<Integer>>(field, Functions.<Range<Integer>>identity(), null, null, null);
    }

    protected RangeFacetBuilderStart<Long, Range<Long>, Long> rangeFacet(LongField field) {
        return new LongRangeFacetBuilder<Range<Long>>(field, Functions.<Range<Long>>identity(), null, null, null);
    }

    protected RangeFacetBuilderStart<Date, Range<Date>, String> rangeFacet(DateField field) {
        return new DateRangeFacetBuilder<Range<Date>>(field, Functions.<Range<Date>>identity(), null, null, null);
    }

    /*
     * Range facet builder only exposes a single interface at each step, so you're forced to call
     * each method in order. gives you the equivalent of named parameters.
     */

    protected static interface RangeFacetBuilderStart<R extends Comparable<R>, F, G> {
        public RangeFacetBuilderGap<R, F, G> start(R start);
    }

    protected static interface RangeFacetBuilderGap<R extends Comparable<R>, F, G> {
        public RangeFacetBuilderEnd<R, F, G> gap(G gap);
    }

    protected static interface RangeFacetBuilderEnd<R extends Comparable<R>, F, G> {
        public RangeFacetBuilderBuild<R, F, G> end(R end);
    }

    protected static interface RangeFacetBuilderBuild<R extends Comparable<R>, F, G> {

        public <T> RangeFacetBuilderBuild<R, T, G> transform(Function<? super F, T> func);
        public FacetKey<Range<R>, F> build();
    }

    private abstract class RangeFacetKeyBuilder<R extends Comparable<R>, F, G>
            implements RangeFacetBuilderStart<R, F, G>, RangeFacetBuilderGap<R, F, G>,
            RangeFacetBuilderEnd<R, F, G>, RangeFacetBuilderBuild<R, F, G> {

        protected final Field<?> field;
        protected R start;
        protected G gap;
        protected R end;

        public RangeFacetKeyBuilder(Field<?> field, R start, G gap, R end) {
            this.field = field;
            this.start = start;
            this.gap = gap;
            this.end = end;
        }

        @Override
        public RangeFacetBuilderGap<R, F, G> start(R start) {
            this.start = start;
            return this;
        }

        @Override
        public RangeFacetBuilderEnd<R, F, G> gap(G gap) {
            this.gap = gap;
            return this;
        }

        @Override
        public RangeFacetBuilderBuild<R, F, G> end(R end) {
            this.end = end;
            return this;
        }

        /*
         * utility method for the facet keys
         * we could subclass the facet key, but so many classes AArrgh
         */

        protected Map<String, RangeFacet<?,?>> rangeFacetsByName(QueryResponse response) {
            Builder<String, RangeFacet<?,?>> builder = ImmutableMap.builder();
            for (RangeFacet<?,?> facet : response.getFacetRanges()) {
                builder.put(facet.getName(), facet);
            }
            return builder.build();
        }

    }

    /*
     * The duplication in the following builder classes is due to needing to retain type safety, they actually do very little
     * other than tie the types together
     */

    private class IntegerRangeFacetBuilder<F> extends RangeFacetKeyBuilder<Integer, F, Integer> {

        private final Function<Range<Integer>, F> transformFunc;

        public IntegerRangeFacetBuilder(Field<?> field, Function<Range<Integer>, F> transformFunc, Integer start, Integer gap, Integer end) {
            super(field, start, gap, end);
            this.transformFunc = transformFunc;
        }

        @Override
        public FacetKey<Range<Integer>, F> build() {
            FacetKey<Range<Integer>, F> key = new FacetKey<Range<Integer>, F>(field, transformFunc) {
                @Override
                protected FacetResults<Range<Integer>, F> buildFacetResults(QueryResponse response) {
                    RangeFacet.Numeric rangeFacet = (RangeFacet.Numeric) rangeFacetsByName(response).get(field.getFieldName());
                    checkNotNull(rangeFacet, "Query didnt return range facet: " + field.getFieldName());

                    return new RangeFacetResults.IntegerFacet<F>(rangeFacet, transformFunc, field);
                }

                @Override
                protected void addToSolrQuery(SolrQuery q) {
                    q.addNumericRangeFacet(field.getFieldName(), start, end, gap);
                }
            };
            facets.add(key);
            return key;
        }

        @Override
        public <T> RangeFacetBuilderBuild<Integer, T, Integer> transform(Function<? super F, T> func) {
            return new IntegerRangeFacetBuilder<T>(field, Functions.compose(func, transformFunc), start, gap, end);
        }
    }

    private class LongRangeFacetBuilder<F> extends RangeFacetKeyBuilder<Long, F, Long> {

        private final Function<Range<Long>, F> transformFunc;

        public LongRangeFacetBuilder(Field<?> field, Function<Range<Long>, F> transformFunc, Long start, Long gap, Long end) {
            super(field, start, gap, end);
            this.transformFunc = transformFunc;
        }

        @Override
        public FacetKey<Range<Long>, F> build() {
            FacetKey<Range<Long>, F> key = new FacetKey<Range<Long>, F>(field, transformFunc) {
                @Override
                protected FacetResults<Range<Long>, F> buildFacetResults(QueryResponse response) {
                    RangeFacet.Numeric rangeFacet = (RangeFacet.Numeric) rangeFacetsByName(response).get(field.getFieldName());
                    checkNotNull(rangeFacet, "Query didnt return range facet: " + field.getFieldName());

                    return new RangeFacetResults.LongFacet<F>(rangeFacet, transformFunc, field);
                }

                @Override
                protected void addToSolrQuery(SolrQuery q) {
                    q.addNumericRangeFacet(field.getFieldName(), start, end, gap);
                }
            };
            facets.add(key);
            return key;
        }

        @Override
        public <T> RangeFacetBuilderBuild<Long, T, Long> transform(Function<? super F, T> func) {
            return new LongRangeFacetBuilder<T>(field, Functions.compose(func, transformFunc), start, gap, end);
        }
    }

    private class DateRangeFacetBuilder<F> extends RangeFacetKeyBuilder<Date, F, String> {

        private final Function<Range<Date>, F> transformFunc;

        public DateRangeFacetBuilder(Field<?> field, Function<Range<Date>, F> transformFunc, Date start, String gap, Date end) {
            super(field, start, gap, end);
            this.transformFunc = transformFunc;
        }

        @Override
        public FacetKey<Range<Date>, F> build() {
            FacetKey<Range<Date>, F> key = new FacetKey<Range<Date>, F>(field, transformFunc) {
                @Override
                protected FacetResults<Range<Date>, F> buildFacetResults(QueryResponse response) {
                    RangeFacet.Date rangeFacet = (RangeFacet.Date) rangeFacetsByName(response).get(field.getFieldName());
                    checkNotNull(rangeFacet, "Query didnt return range facet: " + field.getFieldName());

                    return new RangeFacetResults.DateFacet<F>(rangeFacet, transformFunc, field);
                }

                @Override
                protected void addToSolrQuery(SolrQuery q) {
                    q.addDateRangeFacet(field.getFieldName(), start, end, gap);
                }
            };
            facets.add(key);
            return key;
        }

        @Override
        public <T> RangeFacetBuilderBuild<Date, T, String> transform(Function<? super F, T> func) {
            return new DateRangeFacetBuilder<T>(field, Functions.compose(func, transformFunc), start, gap, end);
        }
    }

    protected static abstract class FacetKey<R, F> implements DataKey<FacetResults<R, F>>  {

        protected final Field<?> field;
        protected final Function<R,F> transformFunc;

        public FacetKey(Field<?> field, Function<R, F> transformFunc) {
            this.field = field;
            this.transformFunc = transformFunc;
        }

        @Override
        public void storeValue(Map<DataKey<?>, ? super Object> map, ClassToInstanceMap<FieldDataSource<?>> context) {
            QueryResponse response = context.getInstance(QueryResponseDataSource.class).get();
            FacetResults<R,F> results = buildFacetResults(response);
            map.put(this, results);
        }

        @Override
        public FacetResults<R,F> retrieveValue(Map<DataKey<?>, ? super Object> map) {
            return (FacetResults<R,F>) map.get(this);
        }

        protected abstract FacetResults<R, F> buildFacetResults(QueryResponse response);

        protected abstract void addToSolrQuery(SolrQuery q);
    }


    public SolrQuery toSolrQuery() {
        SolrQuery q = new SolrQuery();

        return q;
    }
}
