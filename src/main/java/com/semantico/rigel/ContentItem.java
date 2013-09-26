package com.semantico.sipp2.solr;

import com.semantico.sipp2.annotations.Immutable;
import com.semantico.sipp2.solr.fields.Sipp2;

import java.util.Map;

@Immutable
public class ContentItem {

    public static abstract class Schema<C extends ContentItem> extends FieldSet {

        public final FieldKey<?, String> id = field(Sipp2.ID).build();

        public abstract C create(Map<DataKey<?,?>, ?> data);
    }

    public static final Schema<ContentItem> SCHEMA = new Schema<ContentItem>() {
        public ContentItem create(Map<DataKey<?, ?>, ?> data) {
            return new ContentItem(data);
        }
    };

    private final Map<DataKey<?, ?>, ?> data;

    public ContentItem(Map<DataKey<?, ?>, ?> data) {
        this.data = data;
    }

    public <T> T get(DataKey<?, T> key) {
        return (T) data.get(key);
    }

    public String getId() {
        return get(SCHEMA.id);
    }

}
