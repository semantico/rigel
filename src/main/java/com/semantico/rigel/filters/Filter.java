package com.semantico.rigel.filters;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.DateUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.Range;
import com.semantico.rigel.FieldDataSource;
import com.semantico.rigel.SolrDocDataSource;
import com.semantico.rigel.fields.Field;
/**
 * A Filter represents a filter over a solr field, it is also a predicate over solr documents.
 * Filters have the contract that the predicate must be true for solr documents returned from the query it produces.
 * put more simply: the predicate must emulate the solr query
 */
public interface Filter extends Predicate<SolrDocument> {

    public String toSolrFormat();

    public Set<Field<?>> getAffectedFields();

}
