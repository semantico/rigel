package com.semantico.rigel;

import com.google.common.collect.Lists;

import com.google.common.collect.Maps;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.fields.MultivaluedField;
import com.semantico.rigel.fields.SimpleField;
import com.semantico.rigel.filters.Filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A FieldSet defines a set of fields that are applicable when a set of filters are matched
 * if the field set has no filters then it is assumed applicable to any query.
 * It is the responsibility of the user to ensure this is so
 */
public abstract class FieldSet {

    protected final Set<FieldKey<?, ?>> fields;
    protected final Set<Filter> filters;

    public FieldSet() {
        this.fields = Sets.newHashSet();
        this.filters = Sets.newHashSet();
    }

    public Set<FieldKey<?, ?>> getFields() {
        return ImmutableSet.copyOf(fields);
    }

    public Set<Filter> getFilters() {
        return ImmutableSet.copyOf(filters);
    }

    protected void extend(FieldSet subset) {
        fields.addAll(subset.getFields());
        filters.addAll(subset.getFilters());
    }

    protected <T> void filter(Filter filter) {
        filters.add(filter);
    }

    private void addFieldKey(FieldKey<?, ?> key) {
        if(fields.contains(key)) {
            throw new RuntimeException(String.format("Could not build the field, a field with the same key already exists"));
        }
        fields.add(key);
    }

    protected <R> FieldKeyBuilder<R, R> field(Field<R> field) {
        return new FieldKeyBuilder<R,R>(field, Maps.<String,String>newHashMap(), Functions.<R>identity(), false, this);
    }

    /*
     * Field Key implementation
     */

    private static abstract class AbstractFieldKey<R> {

        protected final Field<R> field;
        protected final Map<String,String> metadata;
        protected final boolean eagerTransform;

        public AbstractFieldKey(Field<R> field, Map<String,String> metadata, boolean eagerTransform) {
            this.field = field;
            this.metadata = metadata;
            this.eagerTransform = eagerTransform;
        }

        public Field<R> getField() {
            return field;
        }

        public String getMeta(String key) {
            return metadata.get(key);
        }
    }

    protected static class FieldKeyImpl<R, F> extends AbstractFieldKey<R> implements FieldKey<R, F> {

        private final Function<R, F> transformFunction;

        public FieldKeyImpl(Field<R> field, Map<String,String> metadata, Function<R, F> transformFunction, boolean eagerTransform) {
            super(field, metadata, eagerTransform);
            this.transformFunction = transformFunction;
        }

        @Override
        public F formatRawValue(R rawValue) {
            return transformFunction.apply(rawValue);
        }

        @Override
        public void storeValue(Map<DataKey<?>, ? super Object> map, ClassToInstanceMap<FieldDataSource<?>> context) {
            R rawValue = field.getValue(context);
            if (eagerTransform) {
                map.put(this, formatRawValue(rawValue));
            } else {
                map.put(this, rawValue);
            }
        }

        @Override
        public F retrieveValue(Map<DataKey<?>, ? super Object> map) {
            if (eagerTransform) {
                return (F) map.get(this);
            } else {
                return formatRawValue(((R) map.get(this)));
            }
        }

        @Override
        public F apply(ContentItem item) {
            return item.get(this);
        }
    }


    /*
     * Field Key Builders
     */

    public static class FieldKeyBuilder<R,F> {

        private FieldSet fieldSet;
        private Field<R> field;
        private Function<R,F> formatFunction;
        private HashMap<String,String> metadata;
        private boolean eagerTransform;

        public FieldKeyBuilder(Field<R> field, HashMap<String,String> metadata, Function<R,F> formatFunction, boolean eagerTransform, FieldSet fieldSet) {
            this.field = field;
            this.formatFunction = formatFunction;
            this.metadata = metadata;
            this.fieldSet = fieldSet;
        }

        public FieldKey<R, F> build() {
            FieldKeyImpl<R,F> key = new FieldKeyImpl<R,F>(field, metadata, formatFunction, eagerTransform);
            fieldSet.addFieldKey(key);
            return key;
        }

        public <N> FieldKeyBuilder<R, N> transform(Function<? super F, N> func) {
            return new FieldKeyBuilder<R, N>(field, metadata, Functions.compose(func, formatFunction), eagerTransform, fieldSet);
        }

        public FieldKeyBuilder<R,F> meta(String key, String value) {
            metadata.put(key,value);
            return this;
        }

        public FieldKeyBuilder<R,F> eagerTransform() {
            eagerTransform = true;
            return this;
        }
    }
}
