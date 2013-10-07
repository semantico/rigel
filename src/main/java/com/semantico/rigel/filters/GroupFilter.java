package com.semantico.rigel.filters;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;

import com.semantico.rigel.fields.Field;

import static com.google.common.base.Preconditions.*;

public class GroupFilter extends Filter {

    private Filter delegate;

    public GroupFilter(Filter delegate) {
        checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public boolean apply(SolrDocument input) {
        return delegate.apply(input);
    }

    @Override
    public String toSolrFormat() {
        String solrFormat = delegate.toSolrFormat();
        if (StringUtils.isBlank(solrFormat)) {
            return "";
        }
        return String.format("(%s)", solrFormat);
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        return delegate.getAffectedFields();
    }

    //replace the default group method
    @Override
    public Filter group() {
        return this;
    }
}
