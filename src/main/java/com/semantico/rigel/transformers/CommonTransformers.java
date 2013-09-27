package com.semantico.rigel.transformers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;

import com.google.common.base.Function;

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
