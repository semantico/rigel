package com.semantico.rigel;

import java.util.Map;

import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;

import com.google.common.collect.Maps;

public class ContentRepositoryFactory {

    private final Map<ContentItem.Schema<?>, ContentRepository<?>> singletons;

    private final ContentItemFactory contentItemFactory;
    private final SolrServer server;
    private final METHOD method;

    public ContentRepositoryFactory(SolrServer server, METHOD method, ContentItemFactory contentItemFactory) {
        this.server = server;
        this.method = method;
        this.contentItemFactory = contentItemFactory;
        this.singletons = Maps.newHashMap();
    }

    public synchronized <T extends ContentItem> ContentRepository<T> getRepository(ContentItem.Schema<T> schema) {
        ContentRepository<T> repo = (ContentRepository<T>) singletons.get(schema);
        if (repo == null) {
            repo = new ContentRepositoryImpl<T>(server, contentItemFactory, method, schema);
            singletons.put(schema, repo);
        }
        return repo;
    }

}
