package com.semantico.rigel.fields;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.semantico.rigel.AsyncContentRepository;

public final class ForeignKeyField {

    public static final <O> Function<String, ListenableFuture<O>> foreignKeyField(final Class<O> expectedClass) {
        return new Function<String, ListenableFuture<O>>() {

            private AsyncContentRepository repo;

            public ListenableFuture<O> apply(String input) {
                return repo.submit(input, expectedClass);
            }
        };
    }
}
