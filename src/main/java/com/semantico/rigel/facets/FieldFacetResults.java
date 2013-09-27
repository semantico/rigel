package com.semantico.rigel.facets;

import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.filters.Filter;

public class FieldFacetResults<R, F> extends FacetResults<R, F> {

    private final List<Count<R, F>> values;

    public FieldFacetResults(FacetField facetField,
            Function<String, R> unmarshallFunc, Function<R, F> transformFunc,
            Field<?> field) {
        super(field);

        ImmutableList.Builder<Count<R, F>> builder = ImmutableList.builder();
        for (FacetField.Count count : facetField.getValues()) {
            R rawValue = unmarshallFunc.apply(count.getName());
            F formattedValue = transformFunc.apply(rawValue);
            builder.add(new FieldCount<R, F>(field, rawValue, formattedValue, count
                    .getCount()));
        }
        values = builder.build();
    }

    @Override
    public List<Count<R, F>> getValues() {
        return values;
    }

    private static class FieldCount<R, F> extends FacetResults.Count<R, F> {

        public FieldCount(Field<?> field, R rawValue, F formattedValue, long count) {
            super(field, rawValue, formattedValue, count);
        }

        @Override
        public Filter toFilter() {
            return null;//TODO implement
        }

    }
}
