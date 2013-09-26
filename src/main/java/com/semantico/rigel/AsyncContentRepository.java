package com.semantico.sipp2.solr;


import org.springframework.stereotype.Repository;

import com.google.common.util.concurrent.ListenableFuture;

@Repository
public class AsyncContentRepository {

    public <T> ListenableFuture<T> submit(String id, Class<T> expectedClass) {
        return null;
    }
}
