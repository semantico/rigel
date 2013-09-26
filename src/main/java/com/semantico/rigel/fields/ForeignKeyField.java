package com.semantico.sipp2.solr.fields;

import java.util.Collection;

import javax.annotation.Nullable;

import org.springframework.context.ApplicationContext;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.semantico.sipp2.frontend.SpringContextHolder;
import com.semantico.sipp2.solr.AsyncContentRepository;

public final class ForeignKeyField {

    public static final <O> Function<String, ListenableFuture<O>> foreignKeyField(final Class<O> expectedClass) {
        return new Function<String, ListenableFuture<O>>() {

            private AsyncContentRepository repo;

            @Nullable
            public ListenableFuture<O> apply(@Nullable String input) {
                if (repo == null) {
                    initRepo();
                }
                return repo.submit(input, expectedClass);
            }

            private void initRepo() {
                ApplicationContext context = SpringContextHolder.getApplicationContext();
                if(context == null) {
                    throw new RuntimeException("Application context not initialized");
                }
                repo = context.getBean(AsyncContentRepository.class);
            }
        };
    }
}
