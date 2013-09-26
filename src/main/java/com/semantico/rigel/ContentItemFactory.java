package com.semantico.sipp2.solr;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Sets;
import com.semantico.sipp2.solr.fields.Field;
import com.semantico.sipp2.solr.fields.Sipp2;
import com.semantico.sipp2.solr.filters.Filter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentItemFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ContentItemFactory.class);

    private final Map<Predicate<SolrDocument>, ContentItem.Schema<?>> schemas;
    private final Set<Field<?>> typeFields;

    public ContentItemFactory(List<ContentItem.Schema<?>> schemaList) {
        this.schemas = Maps.newHashMap();
        this.typeFields = Sets.newHashSet();
        for(ContentItem.Schema<?> schema : schemaList) {
            assertValidSchema(schema);
            schemas.put(Predicates.and(schema.getFilters()), schema);

            for (Filter filter : schema.getFilters()) {
                typeFields.addAll(filter.getAffectedFields());
            }
        }
    }

    private void assertValidSchema(ContentItem.Schema<?> schema) {
        if(schema.getFilters().isEmpty()) {
            throw new RuntimeException("content item schemas must define some filters");
        }
        Map<DataKey<?,?>,?> fakeMap = ImmutableMap.of();
        if (schema.create(fakeMap) == null) {
            throw new RuntimeException("content item schemas registered to the content item factory must create concrete objects");
        }
    }

    /*
     * Forced Types
     */
    public <T extends ContentItem> ImmutableList<T> fromResponse(QueryResponse response, ContentItem.Schema<T> forcedSchema, FieldSet... additionalFields) {
        ClassToInstanceMap<FieldDataSource<?>> context = MutableClassToInstanceMap.create();
        context.put(QueryResponseDataSource.class, new QueryResponseDataSource(response));

        Builder<T> builder = ImmutableList.builder();
        for(SolrDocument doc : response.getResults()) {
            context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
            builder.add(buildItem(forcedSchema, context, additionalFields));
        }
        return builder.build();
    }

    public <T extends ContentItem> ImmutableList<T> fromDocumentList(SolrDocumentList list, ContentItem.Schema<T> forcedSchema, FieldSet... additionalFields) {
        ClassToInstanceMap<FieldDataSource<?>> context = MutableClassToInstanceMap.create();
        Builder<T> builder = ImmutableList.builder();
        for(SolrDocument doc : list) {
            context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
            builder.add(buildItem(forcedSchema, context, additionalFields));
        }
        return builder.build();
    }


    /*
     * Unknown types
     */
    public ImmutableList<ContentItem> fromResponse(QueryResponse response, FieldSet... additionalFields) {
        ClassToInstanceMap<FieldDataSource<?>> context = MutableClassToInstanceMap.create();
        context.put(QueryResponseDataSource.class, new QueryResponseDataSource(response));

        Builder<ContentItem> builder = ImmutableList.builder();
        for(SolrDocument doc : response.getResults()) {
            context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
            builder.add(buildItem(getSchemaForDocument(doc), context, additionalFields));
        }
        return builder.build();
    }

    public ImmutableList<ContentItem> fromDocumentList(SolrDocumentList list, FieldSet... additionalFields) {
        ClassToInstanceMap<FieldDataSource<?>> context = MutableClassToInstanceMap.create();
        Builder<ContentItem> builder = ImmutableList.builder();
        for(SolrDocument doc : list) {
            context.put(SolrDocDataSource.class, new SolrDocDataSource(doc));
            builder.add(buildItem(getSchemaForDocument(doc), context, additionalFields));
        }
        return builder.build();
    }

    private <T extends ContentItem> T buildItem(ContentItem.Schema<T> schema, ClassToInstanceMap<FieldDataSource<?>> context, FieldSet... additionalFields) {
        Map<DataKey<?,?>, ? super Object> dataMap = Maps.newHashMap();

        for(FieldKey<?, ?> key : schema.getFields()) {
            key.storeValue(dataMap, context);
        }

        for(FieldSet fieldSet : additionalFields) {
            for(FieldKey<?, ?> key : fieldSet.getFields()) {
                key.storeValue(dataMap, context);
            }
        }
        return schema.create(ImmutableMap.copyOf(dataMap));
    }

    private ContentItem.Schema<?> getSchemaForDocument(SolrDocument document) {
        ContentItem.Schema<?> match = null;

        for(Entry<Predicate<SolrDocument>, ContentItem.Schema<?>> entry : schemas.entrySet()) {
            if(entry.getKey().apply(document)) {
                if(match == null) {
                    match = entry.getValue();
                } else {
                    throw new RuntimeException("multiple matching schemas found for document id:" + document.getFirstValue(Sipp2.ID.getFieldName()));
                }
            }
        }

        if(match == null) {
            throw new RuntimeException("could not determine type of solr document id:" + document.getFirstValue(Sipp2.ID.getFieldName()));
        }
        return match;
    }

    public Set<Field<?>> getTypeFields() {
        return typeFields;
    }
}
