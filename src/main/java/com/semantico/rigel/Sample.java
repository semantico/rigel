package com.semantico.sipp2.solr;

import java.util.Date;

import com.google.common.collect.Range;
import com.semantico.sipp2.solr.facets.FacetResults;
import com.semantico.sipp2.solr.facets.FacetResults.Count;
import com.semantico.sipp2.solr.fields.Sipp2;

import static com.semantico.sipp2.solr.transformers.CommonTransformers.*;

import org.apache.solr.client.solrj.SolrQuery.ORDER;

import com.semantico.sipp2.solr.fields.DublinCore;

public class Sample {

    public class ExampleContentItem extends ContentItem {

        public static class ExampleSchema extends ContentItem.Schema {

            public String publisher = field(DublinCore.PUBLISHER).build();
        }

    }

    public Sample() {
        ContentRepositoryFactory repoFactory = new ContentRepositoryFactory();

        ContentRepository<DefaultContentItem> items = repoFactory.getRepository(DefaultContentItem.SCHEMA);

        items.all()
            .filterBy(DublinCore.PUBLISHER.isEqualTo("YEAH!"))
            .limit(10)
            .orderBy(DublinCore.DATE, ORDER.desc)
            .get();

        items.joinFrom(DublinCore.SUBJECT)
            .filterBy(Sipp2.ID.isEqualTo("playId"))
            .to(Sipp2.ID)
            .get();
    }

    public class SampleSearch extends Search {

        public final FacetKey<Range<Date>, String> dateFacet = rangeFacet(DublinCore.DATE)
            .start(new Date())
            .gap("-1DAY")
            .end(new Date())
            .transform(formatRange(formatDate("yyyy/mm/dd")))
            .build();

        public SampleSearch() {
            FacetResults<Range<Date>, String> facetResults = dateFacet.retrieveValue(null);
            for (Count<Range<Date>, String> count : facetResults.getValues()) {
                System.out.println(count.getFormattedValue());
            }
        }
    }
}
