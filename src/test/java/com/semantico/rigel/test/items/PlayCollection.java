package com.semantico.rigel.test.items;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.semantico.rigel.ContentItem;
import com.semantico.rigel.DataKey;
import com.semantico.rigel.FieldKey;
import com.semantico.rigel.TestFields;
import static com.semantico.rigel.TestFields.*;
import static com.semantico.rigel.transformers.CommonTransformers.*;

public class PlayCollection extends TestItem {

    public static class Schema extends TestItem.Schema<PlayCollection> {

        public FieldKey<?, List<String>> playIds;
        public FieldKey<?, String> type;

        public Schema() {
            playIds = field(CHILD_IDS.multivalued()).transform(asList(String.class)).build();
            type = field(TYPE).build();

            filter(TYPE.equalTo("play-collection"));
        }

        public PlayCollection create(Map<DataKey<?>, ? super Object> data) {
            return new PlayCollection(this, data);
        }
    }

    private final Schema schema;

    public PlayCollection(Schema schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
        this.schema = schema;
    }

    public Collection<String> getPlayIds() {
        return get(schema.playIds);
    }

}
