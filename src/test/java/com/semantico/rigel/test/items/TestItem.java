package com.semantico.rigel.test.items;

import java.util.Map;

import com.semantico.rigel.ContentItem;
import com.semantico.rigel.DataKey;
import com.semantico.rigel.fields.types.StringField;
import static com.semantico.rigel.TestFields.*;

public class TestItem extends ContentItem {

    public abstract static class Schema<T extends TestItem> extends ContentItem.Schema<T> {

        public Schema() {
            super(ID);
        }
    }

    public TestItem(Schema<?> schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
    }

}
