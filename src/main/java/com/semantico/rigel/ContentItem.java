package com.semantico.rigel;

import java.util.Map;

import com.google.common.base.Function;
import com.semantico.rigel.fields.types.StringField;

public abstract class ContentItem {

    public static abstract class Schema<C extends ContentItem> extends FieldSet {

        public final FieldKey<String, String> id;

        public Schema(StringField idField) {
            id = field(idField).build();
        }

        public abstract C create(Map<DataKey<?>, ? super Object> data);
    }

    private final Map<DataKey<?>, ? super Object> data;
    private final Schema<?> schema;

    public ContentItem(Schema<?> schema, Map<DataKey<?>, ? super Object> data) {
        this.schema = schema;
        this.data = data;
    }

    /**
     * get the value of any data associated with this content item, a NullPointerException is thrown
     * if you try to access a field that is missing, you can give fields a default value or make them Optional using a transformation.
     * It is a programming error to try and access data that dosen't exist.
     */
    public <T> T get(DataKey<T> key) {
        T value = key.retrieveValue(data);
        if (value == null) {
            throw new NullPointerException(String.format("The value returned for %s is null", key));
        }
        return value;
    }

    public String getId() {
        return get(schema.id);
    }

}
