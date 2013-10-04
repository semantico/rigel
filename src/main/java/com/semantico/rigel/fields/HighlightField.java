package com.semantico.rigel.fields;

import com.google.common.collect.ClassToInstanceMap;
import com.semantico.rigel.ContentItem.Schema;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.QueryResponseDataSource;
import com.semantico.rigel.SchemaDataSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

public class HighlightField extends SimpleField<String> {

    public HighlightField(String fieldName) {
        super(fieldName);
    }

    public HighlightField(FieldNameSource nameSource) {
        super(nameSource);
    }

    @Override
    public Collection<String> getValues(ClassToInstanceMap<FieldDataSource<?>> context) {
        return getHighlights(context);
    }

    @Override
    public String getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        List<String> highlights = getHighlights(context);
        if (highlights == null || highlights.size() == 0) {
            return null;
        }
        return highlights.get(0);
    }

    private List<String> getHighlights(ClassToInstanceMap<FieldDataSource<?>> context) {
        QueryResponse response = context.getInstance(QueryResponseDataSource.class).get();
        Schema<?> schema = context.getInstance(SchemaDataSource.class).get();

        String docId = schema.id.getField().getValue(context);
        if (docId == null) {
            return null;
        }
        Map<String, Map<String, List<String>>> hilighting = response.getHighlighting();
        if (hilighting == null) {
            return null;
        }
        Map<String, List<String>> hilightingForDoc =  hilighting.get(docId);
        if (hilightingForDoc == null) {
            return null;
        }
        return hilightingForDoc.get(getFieldName());
    }
}
