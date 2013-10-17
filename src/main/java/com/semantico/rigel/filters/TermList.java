package com.semantico.rigel.filters;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.semantico.rigel.fields.Field;

public class TermList implements Filter {

    private Term[] terms;

    public TermList(Term... terms) {
        this.terms = terms;
    }

    @Override
    public boolean apply(SolrDocument input) {
        boolean result = false;
        for (Term term : terms) {
            boolean tmp = term.apply(input);
            if (!tmp && !term.isOptional()) {
                return false;
            }
            result |= tmp;// q.op = OR
        }
        return result;
    }

    @Override
    public String toSolrFormat() {
        List<String> clauses = Lists.newArrayList();
        for (Term term : terms) {
            String clause = term.toSolrFormat();
            if (StringUtils.isNotBlank(clause)) {
                clauses.add(clause);
            }
        }
        return Joiner.on(" ").join(clauses);
    }

    @Override
    public Set<Field<?>> getAffectedFields() {
        Set<Field<?>> fields = Sets.newHashSet();
        for (Term term : terms) {
            fields.addAll(term.getAffectedFields());
        }
        return fields;
    }
}
