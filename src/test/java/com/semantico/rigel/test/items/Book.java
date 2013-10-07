package com.semantico.rigel.test.items;

import java.util.Date;
import java.util.Map;

import com.semantico.rigel.ContentItem;
import com.semantico.rigel.DataKey;
import com.semantico.rigel.FieldKey;
import com.semantico.rigel.ContentItem.Schema;

import static com.semantico.rigel.TestFields.*;

public class Book extends TestItem {

    public static class Schema extends TestItem.Schema<Book> {

        public FieldKey<?, String> title;
        public FieldKey<?, String> type;
        public FieldKey<?, Date> date;
        public FieldKey<?, Integer> chapterCount;

        public Schema() {
            type = field(TYPE).build();
            title = field(TITLE).build();
            date = field(DATE).build();
            chapterCount = field(CHAPTER_COUNT).build();

            filter(TYPE.equalTo("test"));
        }

        @Override
        public Book create(Map<DataKey<?>, ? super Object> data) {
            return new Book(this, data);
        }
    }

    private final Schema schema;

    public Book(Schema schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
        this.schema = schema;
    }

    public String getTitle() {
        return get(schema.title);
    }

    public Date getDate() {
        return get(schema.date);
    }

    public Integer getChapterCount() {
        return get(schema.chapterCount);
    }
}
