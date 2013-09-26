package com.semantico.sipp2.solr.fields;

import com.google.common.collect.ClassToInstanceMap;
import com.semantico.sipp2.solr.FieldDataSource;
import com.semantico.sipp2.solr.QueryResponseDataSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.solr.client.solrj.response.QueryResponse;

public class HighlightField extends SimpleField<String> {

    public HighlightField(String fieldName) {
        super(fieldName);
    }

    @Override
    public Collection<String> getValues(ClassToInstanceMap<FieldDataSource<?>> context) {
        return getHighlights(context);
    }

    @Override
    @Nullable
    public String getValue(ClassToInstanceMap<FieldDataSource<?>> context) {
        List<String> highlights = getHighlights(context);
        if (highlights == null || highlights.size() == 0) {
            return null;
        }
        return highlights.get(0);
    }

    private List<String> getHighlights(ClassToInstanceMap<FieldDataSource<?>> context) {
        QueryResponse response = context.getInstance(QueryResponseDataSource.class).get();
        String docId = Sipp2.ID.getValue(context);
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
        return hilightingForDoc.get(fieldName);
    }
}
