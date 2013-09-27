package com.semantico.rigel;

import java.util.Map;

import com.google.common.collect.Maps;

public class ContentRepositoryFactory {

    private final Map<ContentItem.Schema<?>, ContentRepository<?>> singletons;

    public ContentRepositoryFactory() {
        singletons = Maps.newHashMap();
    }

    public <T extends ContentItem> ContentRepository<T> getRepository(ContentItem.Schema<T> schema) {
        return null;
    }
}
