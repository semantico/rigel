package com.semantico.rigel;

import java.util.Map;

import com.semantico.rigel.fields.ConfigurableFieldName;
import com.semantico.rigel.fields.types.StringField;

public abstract class ContentItem {

    public static abstract class Schema<C extends ContentItem> extends FieldSet {

        private final StringField idField;

        public final FieldKey<?, String> id;

        public Schema() {
            idField = new StringField(new ConfigurableFieldName("id"));
            id = field(idField).build();
        }

        public StringField getIdField() {
            return idField;
        }

        public abstract C create(Map<DataKey<?>, ? super Object> data);
    }

    private final Map<DataKey<?>, ? super Object> data;
    private final Schema<?> schema;

    public ContentItem(Schema<?> schema, Map<DataKey<?>, ? super Object> data) {
        this.schema = schema;
        this.data = data;
    }

    public <T> T get(DataKey<T> key) {
        return key.retrieveValue(data);
    }

    public String getId() {
        return get(schema.id);
    }
}
