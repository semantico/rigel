package com.semantico.rigel.test.items;

import static com.semantico.rigel.TestFields.ID;

import java.util.Map;

import org.apache.solr.common.SolrDocument;

import com.google.common.collect.ClassToInstanceMap;
import com.semantico.rigel.ContentItem;
import com.semantico.rigel.DataKey;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.FieldKey;
import com.semantico.rigel.SolrDocDataSource;
import com.semantico.rigel.fields.Field;

public class TestItem extends ContentItem {

    public abstract static class Schema<T extends TestItem> extends ContentItem.Schema<T> {

        public FieldKey<?, SolrDocument> solrDoc;

        public Schema() {
            super(ID);
            solrDoc = field(new SolrDocExposingField()).build();
        }
    }

    private final Schema<?> schema;

    public TestItem(Schema<?> schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
        this.schema = schema;
    }

    public SolrDocument getSolrDoc() {
        return get(schema.solrDoc);
    }

    private static class SolrDocExposingField implements Field<SolrDocument> {

        @Override
        public String getFieldName() {
            return null;
        }

        @Override
        public SolrDocument getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
            return context.getInstance(SolrDocDataSource.class).get();
        }
    }
}
