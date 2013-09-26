package com.semantico.sipp2.solr.facets;

import java.util.List;

import com.semantico.sipp2.solr.fields.Field;
import com.semantico.sipp2.solr.filters.Filter;

public abstract class FacetResults<R, F> {

    private final Field<?> field;

    public FacetResults(Field<?> field) {
        this.field = field;
    }

    public abstract List<Count<R, F>> getValues();

    public Field<?> getField() {
        return field;
    }

    public abstract static class Count<R, F> {

        protected Field<?> field;
        protected R rawValue;
        protected F formattedValue;
        protected long count;

        public Count(Field<?> field, R rawValue, F formattedValue, long count) {
            this.field = field;
            this.rawValue = rawValue;
            this.formattedValue = formattedValue;
            this.count = count;
        }

        public R getRawValue() {
            return rawValue;
        }

        public F getFormattedValue() {
            return formattedValue;
        }

        public long getCount() {
            return count;
        }

        public abstract Filter toFilter();
    }
}
