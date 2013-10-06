package com.semantico.rigel;

import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.semantico.rigel.fields.Field;
import com.semantico.rigel.filters.Filter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField.Count;

/**
 * yeah its an interface.
 * The reason being that the interface for the builders need to be defined seperately from
 * their implementations, so that you can 'capture' the type of the content repository.
 * They are generic, but in the context of the content repo, they need to be concrete.
 * you'd see what I meant if you tried making the builder implementations a paramaterized type
 */
public interface ContentRepository<T extends ContentItem> {

    IdQueryBuilder<T> id(String id);
    IdsQueryBuilder<T> ids(String... ids);
    IdsQueryBuilder<T> ids(Collection<String> ids);
    AllQueryBuilder<T> all();
    JoinQueryBuilder.PartOne<T> joinFrom(Field<?> field);
    GroupQueryBuilder<T> groupBy(Field<?> groupField);
    //<R> FieldQueryBuilder<R> distinctValues(Field<R> facetField);

    public interface QueryHook {
        void perform(SolrQuery solrQuery);
    }

    public interface AllQueryBuilder<T> {

        AllQueryBuilder<T> filter(Filter... filters);
        AllQueryBuilder<T> orderBy(Field<?> field, ORDER order);
        AllQueryBuilder<T> limit(int count);
        AllQueryBuilder<T> customQuery(QueryHook hook);
        AllQueryBuilder<T> forceType();
        ImmutableList<T> get();
    }

    public interface IdQueryBuilder<T> {

        IdQueryBuilder<T> forceType();
        Optional<T> get();
    }

    public interface IdsQueryBuilder<T> {

        IdsQueryBuilder<T> forceType();
        ImmutableMap<String,Optional<T>> get();
    }

    public interface FieldQueryBuilder<T> {

        FieldQueryBuilder<T> filter(Filter... filters);
        ImmutableList<Count> get();
    }

    public interface GroupQueryBuilder<T> {

        GroupQueryBuilder<T> filter(Filter... filters);
        GroupQueryBuilder<T> orderGroupsBy(Field<?> field, ORDER order);
        GroupQueryBuilder<T> limitGroups(int count);
        GroupQueryBuilder<T> orderWithinGroupBy(Field<?> field, ORDER order);
        GroupQueryBuilder<T> limitResultsPerGroup(int count);
        GroupQueryBuilder<T> forceType();
        ImmutableListMultimap<String, T> get();
    }

    /**
     * There are two parts to the join query builder,
     * these parts enforce the order of parameters and different meaning of
     * filtering at differnt stages.
     */
    public static interface JoinQueryBuilder<T> {

        public interface PartOne<T> {

            /**
             * Filter the source (from)
             */
            PartOne<T> filter(Filter... filters);

            PartTwo<T> to(Field<?> field);
        }

        public interface PartTwo<T> {

            /**
             * Filter the join results
             */
            PartTwo<T> filter(Filter... filters);
            PartTwo<T> forceType();

            ImmutableList<T> get();
        }
    }
}
