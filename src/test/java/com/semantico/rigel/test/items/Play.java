package com.semantico.rigel.test.items;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.semantico.rigel.ContentItem;
import com.semantico.rigel.DataKey;
import com.semantico.rigel.FieldKey;

import static com.semantico.rigel.TestFields.*;

public class Play extends ContentItem {

    public static class Schema extends ContentItem.Schema<Play> {

        public FieldKey<?, String> title;
        public FieldKey<?, String> type;
        public FieldKey<?, Author> author;
        public FieldKey<?, Date> date;
        public FieldKey<?, Integer> sceneCount;
        public FieldKey<?, Long> bigNum;

        public Schema() {
            type = field(TYPE).build();
            title = field(TITLE).build();
            author = field(AUTHOR).transform(Author.parse()).build();
            date = field(DATE).build();
            sceneCount = field(SCENE_COUNT).build();
            bigNum = field(REALLY_BIG_NUMBER).build();

            filter(TYPE.isEqualTo("play"));
        }

        @Override
        public Play create(Map<DataKey<?>, ? super Object> data) {
            return new Play(this, data);
        }
    }

    private final Schema schema;

    public Play(Schema schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
        this.schema = schema;
    }

    public String getTitle() {
        return get(schema.title);
    }

    public Author getAuthor() {
        return get(schema.author);
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
