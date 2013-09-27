package com.semantico.rigel;

public class Sample {

    //public class ExampleContentItem extends ContentItem {

        //public ExampleContentItem(Map<DataKey<?>, ? super Object> data) {
            //super(data);
        //}

        //public class ExampleSchema extends ContentItem.Schema<ExampleContentItem> {

            //public FieldKey<?, String> publisher = field(DublinCore.PUBLISHER).build();

            //@Override
            //public ExampleContentItem create(Map<DataKey<?>, ? super Object> data) {
                //return new ExampleContentItem(data);
            //}
        //}

    //}

    //public Sample() {
        //ContentRepositoryFactory repoFactory = new ContentRepositoryFactory();

        //ContentRepository<DefaultContentItem> items = repoFactory.getRepository(DefaultContentItem.SCHEMA);

        //items.all()
            //.filterBy(DublinCore.PUBLISHER.isEqualTo("YEAH!"))
            //.limit(10)
            //.orderBy(DublinCore.DATE, ORDER.desc)
            //.get();

        //items.joinFrom(DublinCore.SUBJECT)
            //.filterBy(Sipp2.ID.isEqualTo("playId"))
            //.to(Sipp2.ID)
            //.get();
    //}

    //public class SampleSearch extends Search {

        //public final FacetKey<Range<Date>, String> dateFacet = rangeFacet(DublinCore.DATE)
            //.start(new Date())
            //.gap("-1DAY")
            //.end(new Date())
            //.transform(formatRange(formatDate("yyyy/mm/dd")))
            //.build();

        //public SampleSearch() {
            //FacetResults<Range<Date>, String> facetResults = dateFacet.retrieveValue(null);
            //for (Count<Range<Date>, String> count : facetResults.getValues()) {
                //System.out.println(count.getFormattedValue());
            //}
        //}
    //}
}
