package com.semantico.rigel;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class RigelContext {

    public final RigelConfig config;

    private final ContentItemFactory contentItemFactory;
    private final ContentRepositoryFactory contentRepoFactory;
    private final SolrServer server;


    private RigelContext(Set<ContentItem.Schema<?>> schemas, RigelConfig config, SolrServer server) {
        this.config = config;

        this.server = (server != null) ? server : new HttpSolrServer(config.solrUrl);

        contentItemFactory = new ContentItemFactory(schemas);
        contentRepoFactory = new ContentRepositoryFactory(this.server, config.solrRequestMethod, contentItemFactory);

        for (ContentItem.Schema<?> schema : schemas) {
            //Yeah, leaking a reference to an object that hasnt finished being constructed.
            schema.bindToContext(this);
        }
    }

    public <T extends ContentItem> ContentRepository<T> getContentRepository(ContentItem.Schema<T> schema) {
        return contentRepoFactory.getRepository(schema);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Set<ContentItem.Schema<?>> schemas;
        private SolrServer solrServer;
        private Config config;

        public Builder() {
            this.schemas = Sets.newHashSet();
        }

        public RigelContext build() {
            if (config == null) {
                config = ConfigFactory.load();
            }
            return new RigelContext(schemas, new RigelConfig(config), solrServer);
        }

        public Builder registerSchemas(ContentItem.Schema<?>... schemas) {
            this.schemas.addAll(Arrays.asList(schemas));
            return this;
        }

        public Builder withConfig(Config config) {
            this.config = config;
            return this;
        }

        public Builder customSolrServer(SolrServer solrServer) {
            this.solrServer = solrServer;
            return this;
        }
    }

    /*
     * yeah i know its all public, im feeling lazy
     */
    public static class RigelConfig {

        public final METHOD solrRequestMethod;
        public final String solrUrl;

        public final Map<String, FieldConfig> fieldConfig;

        public RigelConfig(Config applicationConfig) {
            Config config = applicationConfig.getConfig("rigel");

            fieldConfig = Maps.newHashMap();
            Config fields = config.getConfig("field");

            for (String configField : getSubPaths(fields)) {
                Config conf = fields.getConfig(configField);
                FieldConfig f = new FieldConfig(configField, conf.getString("name"));
                fieldConfig.put(configField, f);
            }

            String requestMethod = config.getString("solr.request.method");
            try {
                this.solrRequestMethod = SolrRequest.METHOD.valueOf(requestMethod);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("rigel.request.method must be either GET or POST");
            }

            this.solrUrl = config.getString("solr.url");
        }

        private Set<String> getSubPaths(Config config) {
            Set<String> subPaths = Sets.newHashSet();
            for (Entry<String, ConfigValue> entry : config.entrySet()) {
                String[] parts = entry.getKey().split("\\.");
                if (parts.length > 0) {
                    subPaths.add(parts[0]);
                }
            }
            return subPaths;
        }

        public static class FieldConfig {

            public final String configName;
            public final String solrFieldName;

            public FieldConfig(String configName, String solrFieldName) {
                this.configName = configName;
                this.solrFieldName = solrFieldName;
            }
        }
    }
}
