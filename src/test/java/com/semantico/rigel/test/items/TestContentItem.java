package com.semantico.rigel.test.items;

import java.util.Date;
import java.util.Map;

import com.semantico.rigel.ContentItem;
import com.semantico.rigel.DataKey;
import com.semantico.rigel.FieldKey;
import com.semantico.rigel.ContentItem.Schema;

import static com.semantico.rigel.TestFields.*;

public class TestContentItem extends ContentItem {

    public static class Schema extends ContentItem.Schema<TestContentItem> {

        public FieldKey<?, String> title;
        public FieldKey<?, String> type;
        public FieldKey<?, Date> date;
        public FieldKey<?, Integer> sceneCount;
        public FieldKey<?, Long> bigNum;

        public Schema() {
            type = field(TYPE).build();
            title = field(TITLE).build();
            date = field(DATE).build();
            sceneCount = field(SCENE_COUNT).build();
            bigNum = field(REALLY_BIG_NUMBER).build();

            filter(TYPE.isEqualTo("test"));
        }

        @Override
        public TestContentItem create(Map<DataKey<?>, ? super Object> data) {
            return new TestContentItem(this, data);
        }
    }

    private final Schema schema;

    public TestContentItem(Schema schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
        this.schema = schema;
    }

    public String getTitle() {
        return get(schema.title);
    }

    public Date getDate() {
        return get(schema.date);
    }

    public Integer getSceneCount() {
        return get(schema.sceneCount);
    }

    public Long getBigNum() {
        return get(schema.bigNum);
    }
}
