package com.semantico.rigel.filters;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.semantico.rigel.fields.Field;

//rename SymmetricConnectiveFilter ?
public abstract class ConnectiveFilter extends Filter {

    protected abstract List<Filter> getJoinedFilters();

    @Override
    public Set<Field<?>> getAffectedFields() {
        ImmutableSet.Builder<Field<?>> builder = ImmutableSet.builder();

        for (Filter filter : getJoinedFilters()) {
            builder.addAll(filter.getAffectedFields());
        }
        return builder.build();
    }

    protected Set<String> getSolrFilters() {
        Set<String> solrFilters = Sets.newHashSet();
        //yes set! we can remove duplicate terms because its symmetric

        for (Filter filter : getJoinedFilters()) {
            String solrFilter = filter.toSolrFormat();
            if (StringUtils.isNotBlank(solrFilter)) {//isnt just whitespace
                solrFilters.add(solrFilter);
            }
        }
        return solrFilters;
    }
}
