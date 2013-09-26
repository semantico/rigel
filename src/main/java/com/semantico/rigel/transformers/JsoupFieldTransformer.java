package com.semantico.sipp2.solr.transformers;

import java.util.Collection;

import javax.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.google.common.base.Function;

public class JsoupFieldTransformer {

    public static Function<String, String> jsoupClean(Whitelist whitelist) {
        return getTransformFunction(whitelist);
    }

    public static Function<String, String> jsoupClean() {
        return getTransformFunction(Whitelist.relaxed());
    }

    private static Function<String,String> getTransformFunction(final Whitelist whitelist) {
        return new Function<String,String>() {
            @Nullable
            public String apply(@Nullable String input) {
                if(input == null) {
                    return null;
                }
                return Jsoup.clean(input, whitelist);
            }
        };
    }
}
