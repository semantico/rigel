package com.semantico.rigel;

import com.semantico.rigel.ContentItem.Schema;

public class SchemaDataSource implements FieldDataSource<ContentItem.Schema<?>> {

    private final Schema<?> schema;

    public SchemaDataSource(Schema<?> schema) {
        this.schema = schema;
    }

    @Override
    public Schema<?> get() {
        return schema;
    }
}
