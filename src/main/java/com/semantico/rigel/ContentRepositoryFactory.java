package com.semantico.sipp2.solr;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class ContentRepositoryFactory {

    private final Map<ContentItem.Schema<?>, ContentRepository<?>> singletons;
    
    public ContentRepositoryFactory() {
        singletons = Maps.newHashMap();
    }
    
    public <T extends ContentItem> ContentRepository<T> getRepository(ContentItem.Schema<T> schema) {
        return null;
    }
}
