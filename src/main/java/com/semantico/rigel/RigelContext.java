package com.semantico.rigel;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class RigelContext {

    public final RigelConfig config;

    public RigelContext() {
        this(ConfigFactory.load());
    }

    public RigelContext(Config applicationConfig) {
        this.config = new RigelConfig(applicationConfig);
    }

    public void registerSchema(ContentItem.Schema<?> schema) {
        schema.bindToContext(this);
    }

    public static class RigelConfig {

        public final METHOD solrRequestMethod;
        public final SolrServer solrServer;

        public final Map<String, FieldConfig> fieldConfig;

        public RigelConfig(Config applicationConfig) {
            Config config = applicationConfig.getConfig("rigel");

            fieldConfig = Maps.newHashMap();
            Config fieldConfig = config.getConfig("field");
            for (Entry<String, ConfigValue> thing : fieldConfig.entrySet()) {
                System.out.println(thing.getKey());
            }

            String requestMethod = config.getString("solr.request.method");
            try {
                this.solrRequestMethod = SolrRequest.METHOD.valueOf(requestMethod);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("rigel.request.method must be either GET or POST");
            }

            if (config.hasPath("solr.server")) {
                this.solrServer = (SolrServer) config.getAnyRef("solr.server");
            } else {
                String url = config.getString("solr.url");
                this.solrServer = new HttpSolrServer(url);
            }
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
