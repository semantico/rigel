package com.semantico.rigel.transformers;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;

public final class CommonTransformers {

    /*
     * Generic Transformers
     */

    public static final <T> Function<T, T> defaultValue(final T value) {
        return new Function<T, T>() {

            public T apply(T input) {
                return input == null ? value : input;
            }
        };
    }

    public static final <I, O> Function<I, O> mapValues(final ImmutableMap<I,O> map) {
        return new Function<I, O>() {

            public O apply(I input) {
                return map.get(input);
            }
        };
    }

    public static final <I> Function<I,Optional<I>> optional() {
        return new Function<I, Optional<I>>() {

            public Optional<I> apply(I input) {
                return Optional.<I>of(input);
            }
        };
    }

    public static final <I> Function<I,Optional<I>> optional(Class<I> clazz) {
        return CommonTransformers.<I>optional();
    }

    /*
     * Functions to deal with Collections
     */

    public static final <I, O> Function<Iterable<? extends I>, Collection<O>> asCollection(final Function<? super I, O> func) {
        return new Function<Iterable<? extends I>, Collection<O>>() {

            public Collection<O> apply(Iterable<? extends I> input) {
                List<O> output = Lists.newArrayList();
                for (I value : input) {
                    output.add(func.apply(value));
                }
                return output;
            }
        };
    }

    public static final <I> Function<Iterable<? extends I>, Collection<I>> asCollection() {
        return asCollection(Functions.<I>identity());
    }

    public static final <I> Function<Iterable<? extends I>, Collection<I>> asCollection(Class<I> clazz) {
        return CommonTransformers.<I>asCollection();
    }

    public static final <I, O> Function<Iterable<? extends I>, List<O>> asList(final Function<? super I, O> func) {
        return new Function<Iterable<? extends I>, List<O>>() {

            public List<O> apply(Iterable<? extends I> input) {
                List<O> output = Lists.newArrayList();
                for (I value : input) {
                    output.add(func.apply(value));
                }
                return output;
            }
        };
    }

    public static final <I> Function<Iterable<? extends I>, List<I>> asList() {
        return asList(Functions.<I>identity());
    }

    public static final <I> Function<Iterable<? extends I>, List<I>> asList(Class<I> clazz) {
        return CommonTransformers.<I>asList();
    }

    public static final <I, O> Function<Iterable<? extends I>, Set<O>> asSet(final Function<? super I, O> func) {
        return new Function<Iterable<? extends I>, Set<O>>() {

            public Set<O> apply(Iterable<? extends I> input) {
                Set<O> output = Sets.newHashSet();
                for (I value : input) {
                    output.add(func.apply(value));
                }
                return output;
            }
        };
    }

    public static final <I> Function<Iterable<? extends I>, Set<I>> asSet() {
        return asSet(Functions.<I>identity());
    }

    public static final <I> Function<Iterable<? extends I>, Set<I>> asSet(Class<I> clazz) {
        return CommonTransformers.<I>asSet();
    }

    /*
     * Grouping
     */

    public static final <I, K> GroupByFunction<I, K, I> groupBy(Function<I, K> keyFunc) {
        return new GroupByFunction<I, K, I>(keyFunc, Functions.<I>identity());
    }

    public static class GroupByFunction<I, K, V> implements Function<Iterable<? extends I>, ListMultimap<K, V>> {

        private final Function<? super I, K> keyFunc;
        private final Function<? super I, V> valueFunc;

        public GroupByFunction(Function<? super I, K> keyFunc, Function<? super I, V> valueFunc) {
            this.keyFunc = keyFunc;
            this.valueFunc = valueFunc;
        }

        public ListMultimap<K, V> apply(Iterable<? extends I> input) {
            ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
            for (I value : input) {
                K key = keyFunc.apply(value);
                V outputVal = valueFunc.apply(value);
                builder.put(key, outputVal);
            }
            return builder.build();
        }

        public <O> Function<Iterable<? extends I>, ListMultimap<K, O>> as(Function<V, O> newValueFunc) {
            return new GroupByFunction<I, K, O>(keyFunc, Functions.compose(newValueFunc, valueFunc));
        }
    }


    /*
     * String Transformers
     */

    //public static Function<String, String> convert(final Format from, final Format to) {
        //return new Function<String, String>() {

            //public String apply(String string) {
                //return Formats.convert(from, to, string);
            //}
        //};
    //}

    public static <T extends Comparable<T>> Function<Range<T>, String> formatRange(final Function<T, String> formatT) {
        return new Function<Range<T>, String>() {
            @Override
            public String apply(Range<T> input) {
                String lowerBound = input.hasLowerBound() ? formatT.apply(input.lowerEndpoint()) : "*";
                String upperBound = input.hasUpperBound() ? formatT.apply(input.upperEndpoint()) : "*";
                return String.format("%s - %s", lowerBound, upperBound);
            }
        };
    }

    public static Function<Date, String> formatDate(String formatString) {
        final SimpleDateFormat format = new SimpleDateFormat(formatString);
        return new Function<Date, String>() {
            @Override
            public String apply(Date input) {
                return format.format(input);
            }
        };
    }
}
