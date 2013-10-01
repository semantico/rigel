package com.semantico.rigel;

import java.util.Date;
import java.util.Map;

import static com.semantico.rigel.TestFields.*;

public class TestContentItem extends ContentItem {

    public static class TestSchema extends ContentItem.Schema<TestContentItem> {

        public FieldKey<?, String> title;
        public FieldKey<?, Date> date;
        public FieldKey<?, Integer> sceneCount;
        public FieldKey<?, Long> bigNum;

        public TestSchema() {
            title = field(TITLE).build();
            date = field(DATE).build();
            sceneCount = field(SCENE_COUNT).build();
            bigNum = field(REALLY_BIG_NUMBER).build();

        }

        @Override
        public TestContentItem create(Map<DataKey<?>, ? super Object> data) {
            return new TestContentItem(this, data);
        }
    }

    private final TestSchema schema;

    public TestContentItem(TestSchema schema, Map<DataKey<?>, ? super Object> data) {
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
