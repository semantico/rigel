package com.semantico.rigel;

import java.util.Map;

import com.semantico.rigel.fields.types.StringField;

public class ContentItem {

    public static final StringField ID = new StringField("id");

    public static abstract class Schema<C extends ContentItem> extends FieldSet {

        public final FieldKey<?, String> id = field(ContentItem.ID).build();

        public abstract C create(Map<DataKey<?>, ? super Object> data);
    }

    public static final Schema<ContentItem> SCHEMA = new Schema<ContentItem>() {
        @Override
        public ContentItem create(Map<DataKey<?>, ? super Object> data) {
            return new ContentItem(data);
        }
    };

    private final Map<DataKey<?>, ? super Object> data;

    public ContentItem(Map<DataKey<?>, ? super Object> data) {
        this.data = data;
    }

    public <T> T get(DataKey<T> key) {
        return key.retrieveValue(data);
    }

    public String getId() {
        return get(SCHEMA.id);
    }
}
