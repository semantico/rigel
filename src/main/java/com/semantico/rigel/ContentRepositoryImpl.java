package com.semantico.rigel;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.semantico.rigel.ContentRepository.JoinQueryBuilder.PartOne;
import com.semantico.rigel.ContentRepository.JoinQueryBuilder.PartTwo;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.filters.Filter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.*;

public final class ContentRepositoryImpl<T extends ContentItem> implements
        ContentRepository<T> {

    private static final Logger LOG = LoggerFactory
            .getLogger(ContentRepository.class);

    private final SolrServer solr;
    private final ContentItemFactory contentItemFactory;
    private final ContentItem.Schema<T> schema;
    private final METHOD method;

    public ContentRepositoryImpl(SolrServer solr,
            ContentItemFactory contentItemFactory, METHOD method,
            ContentItem.Schema<T> schema) {
        this.solr = checkNotNull(solr);
        this.contentItemFactory = checkNotNull(contentItemFactory);
        this.method = checkNotNull(method);
        this.schema = schema;
    }

    @Override
    public IdQueryBuilder<T> id(String id) {
        checkArgument(id != null && !id.isEmpty());
        return new IdQueryBuilderImpl(id);
    }

    public class IdQueryBuilderImpl implements IdQueryBuilder<T> {

        private final SolrQuery q;
        private boolean forceType;

        public IdQueryBuilderImpl(String id) {
            this.forceType = false;
            this.q = new SolrQuery();
            q.addFilterQuery(Filter.on(schema.getIdField(), id).toSolrFormat());
            q.setRows(1);
            q.setRequestHandler("fetch");
        }

        @Override
        public IdQueryBuilder<T> forceType() {
            forceType = true;
            return this;
        }

        @Override
        public Optional<T> get() {
            ImmutableList<T> items;
            if (forceType) {
                items = getContentItemsForQueryForced(q);
            } else {
                items = getContentItemsForQuery(q);
            }
            T contentItem = (items.size() > 0) ? items.get(0) : null;
            return Optional.fromNullable(contentItem);
        }
    }

    @Override
    public IdsQueryBuilder<T> ids(String... ids) {
        checkArgument(ids != null);
        for (String id : ids) {
            checkArgument(id != null && !id.isEmpty());
        }
        return new IdsQueryBuilderImpl(ids);
    }

    @Override
    public IdsQueryBuilder<T> ids(Collection<String> ids) {
        return ids(ids.toArray(new String[ids.size()]));
    }

    private class IdsQueryBuilderImpl implements IdsQueryBuilder<T> {

        private String[] ids;
        private boolean forceType;

        public IdsQueryBuilderImpl(String... ids) {
            this.forceType = false;
            this.ids = ids;
        }

        @Override
        public IdsQueryBuilder<T> forceType() {
            forceType = true;
            return this;
        }

        @Override
        public ImmutableMap<String, Optional<T>> get() {
            if (ids.length == 0) {
                return ImmutableMap.of();
            }

            SolrQuery q = new SolrQuery();
            Set<Filter> filters = Sets.newHashSet();
            for (String id : ids) {
                filters.add(Filter.on(schema.getIdField(), id));
            }
            q.addFilterQuery(Filter.or(filters).toSolrFormat());
            q.setRows(Integer.MAX_VALUE);
            q.setRequestHandler("fetch");

            ImmutableList<T> items;
            if (forceType) {
                items = getContentItemsForQueryForced(q);
            } else {
                items = getContentItemsForQuery(q);
            }

            Map<String, Optional<T>> map = Maps.newHashMap();

            for (T item : items) {
                map.put(item.getId(), Optional.of(item));
            }
            for (String id : ids) {
                if (!map.containsKey(id)) {
                    map.put(id, Optional.<T> absent());
                }
            }
            return ImmutableMap.copyOf(map);
        }
    }

    @Override
    public AllQueryBuilder<T> all() {
        return new AllQueryBuilderImpl();
    }

    protected SolrQuery buildAllQuery() {
        SolrQuery q = new SolrQuery();
        q.addFilterQuery(Filter.and(schema.getFilters()).toSolrFormat());
        q.setRows(Integer.MAX_VALUE);
        q.setRequestHandler("fetch");
        return q;
    }

    public ImmutableList<Count> getValuesForField(Field<?> field) {
        SolrQuery sq = buildValuesForFieldsQuery(field);
        QueryResponse rsp = querySolr(sq);

        FacetField facetField = rsp.getFacetField(field.getFieldName());
        return ImmutableList.copyOf(facetField.getValues());
    }

    public ImmutableListMultimap<Field<?>, Count> getValuesForFields(
            Field<?>... fields) {
        SolrQuery sq = buildValuesForFieldsQuery(fields);
        QueryResponse rsp = querySolr(sq);

        ImmutableListMultimap.Builder<Field<?>, Count> results = ImmutableListMultimap
                .builder();
        for (Field<?> field : fields) {
            FacetField facetField = rsp.getFacetField(field.getFieldName());
            results.putAll(field, facetField.getValues());
        }
        return results.build();
    }

    protected SolrQuery buildValuesForFieldsQuery(Field<?>... fields) {
        SolrQuery q = buildAllQuery();
        q.setFacet(true);
        for (Field<?> field : fields) {
            q.addFacetField(field.getFieldName());
        }
        q.setRows(0);
        return q;
    }

    private final class AllQueryBuilderImpl implements AllQueryBuilder<T> {

        private SolrQuery q;
        private boolean forceType;
        private QueryHook hook;

        public AllQueryBuilderImpl() {
            q = buildAllQuery();
            forceType = false;
            hook = null;
        }

        @Override
        public AllQueryBuilder<T> filterBy(Filter... filters) {
            q.addFilterQuery(Filter.and(filters).toSolrFormat());
            return this;
        }

        @Override
        public AllQueryBuilder<T> orderBy(Field<?> field, ORDER order) {
            q.addSort(field.getFieldName(), order);
            return this;
        }

        @Override
        public AllQueryBuilder<T> limit(int count) {
            q.setRows(count);
            return this;
        }

        @Override
        public AllQueryBuilder<T> customQuery(QueryHook hook) {
            this.hook = hook;
            return this;
        }

        @Override
        public AllQueryBuilder<T> forceType() {
            forceType = true;
            return this;
        }

        @Override
        public ImmutableList<T> get() {
            processQueryHook();
            if (forceType) {
                return getContentItemsForQueryForced(q);
            } else {
                return getContentItemsForQuery(q);
            }
        }

        private void processQueryHook() {
            if (hook == null) {
                return;
            }
            SolrQuery combined = new SolrQuery();
            hook.perform(combined);
            if (!forceType) {
                //if the content item type isnt forced then we cant limit the fields
                //potentially any field could be used by the content item factory to
                //determine the type
                combined.remove("fl");
            }
            combined.add(q);
            q = combined;
        }
    }

    @Override
    public JoinQueryBuilder.PartOne<T> joinFrom(Field<?> field) {
        return new JoinQueryBuilderPart1(field);
    }

    private class JoinQueryBuilderPart1 implements JoinQueryBuilder.PartOne<T> {
        //Fields are public, dirty, but users are only exposed to the interface
        public Field<?> fromField;
        public Field<?> toField;
        public Filter sourceFilter;

        public JoinQueryBuilderPart1(Field<?> field) {
            this.fromField = field;
        }

        @Override
        public PartOne<T> filterBy(Filter... filters) {
            if (sourceFilter != null) {
                throw new RuntimeException("multiple filterBy statements not supported for the source filter");
                //you can join them together with and / or into one filter if you want
            }
            this.sourceFilter = Filter.and(filters);
            return this;
        }

        @Override
        public PartTwo<T> to(Field<?> field) {
            this.toField = field;
            return new JoinQueryBuilderPart2(this);
        }
    }

    private class JoinQueryBuilderPart2 implements JoinQueryBuilder.PartTwo<T> {

        private SolrQuery q;
        private boolean forceType;

        public JoinQueryBuilderPart2(JoinQueryBuilderPart1 partOne) {
            forceType = false;
            q = new SolrQuery();
            q.setQuery(String.format("{!join from=%s to=%s}%s",
                        partOne.fromField,
                        partOne.toField,
                        partOne.sourceFilter == null ? "" : partOne.sourceFilter.toSolrFormat()));
            q.addFilterQuery(Filter.and(schema.getFilters()).toSolrFormat());
        }

        @Override
        public PartTwo<T> filterBy(Filter... filters) {
            q.addFilterQuery(Filter.and(filters).toSolrFormat());
            return this;
        }

        @Override
        public PartTwo<T> forceType() {
            forceType = true;
            return this;
        }

        @Override
        public ImmutableList<T> get() {
            if (forceType) {
                return getContentItemsForQueryForced(q);
            } else {
                return getContentItemsForQuery(q);
            }
        }
    }

    @Override
    public GroupQueryBuilder<T> groupBy(Field<?> groupField) {
        return new GroupQueryBuilderImpl(groupField);
    }

    private class GroupQueryBuilderImpl implements GroupQueryBuilder<T> {

        private boolean forceType;
        private SolrQuery q;

        public GroupQueryBuilderImpl(Field<?> groupField) {
            this.forceType = false;
            this.q = new SolrQuery();
            q.set("group", true);
            q.set("group.field", groupField.getFieldName());
        }

        @Override
        public GroupQueryBuilder<T> filterBy(Filter... filters) {
            q.addFilterQuery(Filter.and(filters).toSolrFormat());
            return this;
        }

        @Override
        public GroupQueryBuilder<T> orderGroupsBy(Field<?> field, ORDER order) {
            q.setSort(field.getFieldName(), order);
            return this;
        }

        @Override
        public GroupQueryBuilder<T> limitGroups(int count) {
            q.setRows(count);
            return this;
        }

        @Override
        public GroupQueryBuilder<T> orderWithinGroupBy(Field<?> field, ORDER order) {
            q.set("group.sort", String.format("%s %s", field.getFieldName(), order));
            return this;
        }

        @Override
        public GroupQueryBuilder<T> limitResultsPerGroup(int count) {
            q.set("group.limit", count);
            return this;
        }

        @Override
        public GroupQueryBuilder<T> forceType() {
            this.forceType = true;
            return this;
        }

        @Override
        public ImmutableListMultimap<String, T> get() {
            QueryResponse response = querySolr(q);
            GroupCommand command = response.getGroupResponse().getValues().get(0);
            Builder<String, T> builder = ImmutableListMultimap.builder();
            for (Group group : command.getValues()) {
                String key = group.getGroupValue();
                if (forceType) {
                    List<T> items = contentItemFactory.fromDocumentList(group.getResult(), schema);
                    builder.putAll(key, items);
                } else {
                    List<? extends ContentItem> items = contentItemFactory.fromDocumentList(group.getResult());
                    for (ContentItem item : items) {
                        builder.put(key, doCast(item));
                    }
                }
            }
            return builder.build();
        }
    }

    @Override
    public <R> FieldQueryBuilder<R> distinctValues(Field<R> facetField) {
        // TODO Auto-generated method stub
        return null;
    }

    private ImmutableList<T> getContentItemsForQueryForced(SolrQuery q) {
        QueryResponse response = querySolr(q);
        return contentItemFactory.fromResponse(response, schema);
    }

    private ImmutableList<T> getContentItemsForQuery(SolrQuery q) {
        QueryResponse response = querySolr(q);
        List<? extends ContentItem> items = contentItemFactory.fromResponse(response);
        ImmutableList.Builder<T> b = ImmutableList.<T>builder();
        for (ContentItem item : items) {
            b.add(doCast(item));
        }
        return b.build();
    }

    private QueryResponse querySolr(SolrQuery q) {
        LOG.info("Solr Query: " + q.toString());
        try {
            return solr.query(q, method);
        } catch (SolrServerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final T doCast(ContentItem item) {
        try {
            return (T) item;
        } catch (ClassCastException e) {
            throw new UnexpectedTypeException("Unexpected content item returned from solr query", e);
        }
    }

    public static class UnexpectedTypeException extends RuntimeException {

        public UnexpectedTypeException(String message, Exception cause) {
            super(message, cause);
        }
    }

}
