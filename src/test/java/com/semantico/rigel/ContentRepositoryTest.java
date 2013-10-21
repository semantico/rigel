package com.semantico.rigel;

import static com.semantico.rigel.TestFields.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.semantico.rigel.test.items.Book;
import com.semantico.rigel.test.items.Play;
import com.semantico.rigel.test.items.TestItem;

import static com.semantico.rigel.filters.Filters.*;

@RunWith(JUnit4.class)
public class ContentRepositoryTest extends IntergrationTestBase {

    private static ContentRepository<Play> plays;
    private static ContentRepository<Book> books;
    private static ContentRepository<TestItem> items;

    @BeforeClass
    public static void beforeClass() throws SolrServerException, IOException {
        initialize("test-config.properties");

        List<SolrInputDocument> docs = Lists.newArrayList();

        docs.add(createPlay("play1",
                "The Play that got away",
                "King, Edd",
                new Date(),
                5,
                1234567L));

        docs.add(createPlay("play2",
                "The Other Play with 5 Scenes!",
                "Mc Roar, Malcolm",
                new Date(),
                5,
                1L));

        docs.add(createPlay("play3",
                "Yet Another Play",
                "King, Edd",
                new Date(),
                2,
                1234567L));

        docs.add(createPlay("play4",
                "superPlay!",
                "Mc Roar, Malcolm",
                new Date(),
                6,
                1234567L));

        docs.add(createBook("book1",
                "A lonely book",
                new Date(),
                5));

        docs.add(createPlayCollection("collection1",
                "play1",
                "play2"));

        docs.add(createPlayCollection("collection2",
                "play4"));

        addAndCommit(docs);

        plays = rigel.getContentRepository(playSchema);
        books = rigel.getContentRepository(bookSchema);
        items = rigel.getContentRepository(testItemSchema);
    }

    @AfterClass
    public static void afterClass() {
        shutDown();
    }

    @Test
    public void testDefaultAllQuery() {
        List<Play> results = plays.all().get();
        assertEquals(results.size(), 4);
    }

    @Test
    public void testAllQueryLimit() {
        List<Play> results = plays.all()
            .limit(2)
            .get();
        assertEquals(results.size(), 2);
    }

    @Test
    public void testAllQueryFilter() {
        List<Play> results = plays.all()
            .filter(SCENE_COUNT.equalTo(5))
            .get();
        assertEquals(results.size(), 2);
    }

    @Test
    public void testAllQueryFilter2() {
        List<Play> results = plays.all()
            .filter(SCENE_COUNT.equalTo(5), SCENE_COUNT.equalTo(6))
            .get();
        assertEquals(results.size(), 3);
    }

    @Test
    public void testAllQueryFilter3() {
        List<Play> results = plays.all()
            .filter(SCENE_COUNT.equalTo(5), require(REALLY_BIG_NUMBER.lessThan(2L)))
            .get();
        assertEquals(results.size(), 1);
    }

    @Test
    public void testAllQueryForced() {
        List<TestItem> results = items.all()
            .filter(TYPE.equalTo("play"))
            .forceType()
            .get();

        assertEquals(4, results.size());
        for (TestItem item : results) {
            //Test that the runtime type is actually TestItem
            assertTrue(item.getClass().isAssignableFrom(TestItem.class));
        }
    }

    @Test
    public void testAllQueryNotForcedByDefault() {
        List<TestItem> results = items.all()
            .filter(TYPE.equalTo("play"))
            .get();

        assertEquals(4, results.size());
        for (TestItem item : results) {
            //Test that the runtime type is actually a subclass of TestItem
            assertTrue(!item.getClass().isAssignableFrom(TestItem.class));//TestItem is not a Play
            assertTrue(item instanceof TestItem);//A Play is a TestItem
        }
    }

    @Test
    public void testDefaultIdQuery() {
        Optional<Play> op = plays.id("play1").get();

        assertTrue(op.isPresent());
        //Make sure the data was retrieved as expected
        assertEquals("play1", op.get().getId());
    }

    @Test
    public void testMissingIdQuery() {
        Optional<Play> op = plays.id("somethingMadeUp").get();
        assertTrue(!op.isPresent());
    }

    @Test
    public void testForcedIdQuery() {
        Optional<Book> op = books.id("play1").forceType().get();
        assertTrue(op.get() instanceof Book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdQueryBrokenPrecondition() {
        plays.id(null).get();//Null not allowed
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdQueryBrokenPrecondition2() {
        plays.id("").get();//Empty Id not allowed
    }

    @Test
    public void testDefaultIdsQuery() {
        Map<String, Optional<Play>> results = plays.ids("play1", "play4", "somthingMadeUp").get();

        assertEquals(3, results.size());
        assertTrue(results.get("play1").isPresent());
        assertTrue(results.get("play4").isPresent());
        assertTrue(!results.get("somthingMadeUp").isPresent());
        assertTrue(results.get("some thing i didnt ask for") == null);
    }

    @Test
    public void testCollectionIds() {
        Map<String, Optional<Play>> results = plays.ids(ImmutableSet.of("play1", "play2")).get();
        assertEquals(2, results.size());
        assertTrue(results.get("play1").isPresent());
        assertTrue(results.get("play2").isPresent());
    }

    @Test
    public void testEmptyIdsQuery() {
        Map<String, Optional<Play>> results = plays.ids().get();
        assertEquals(0, results.size());
    }

    @Test
    public void testForcedIdsQuery() {
        Map<String, Optional<Book>> results = books.ids("play1", "play2").forceType().get();

        assertTrue(results.get("play1").get() instanceof Book);
        assertTrue(results.get("play2").get() instanceof Book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdsQueryBrokenPrecondition() {
        plays.ids("bla", "bla", null).get();//Null Not allowed
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdsQueryBrokenPrecondition2() {
        plays.ids("bla", "", "bla").get();//Empty Id not allowed
    }

    @Test
    public void testDefaultGroupQuery() {
        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT).get();

        //There are 3 distinct scene counts
        assertEquals(3, groups.keySet().size());

        //there are two items with 5 scenes, but the default only returns
        //one document per group
        assertEquals(1, groups.get("5").size());
    }

    @Test
    public void testGroupQuery2() {
        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT).limitResultsPerGroup(100).get();

        List<Play> group = groups.get("5");
        assertEquals(2, group.size());
    }

    @Test
    public void testFilterGroupQuery() {
        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT)
            .limitResultsPerGroup(100)
            .filter(SCENE_COUNT.atLeast(3))//Filter out scene count = 2
            .get();

        assertEquals(2, groups.keySet().size());
    }

    @Test
    public void testFilterGroupQuery2() {
        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT)
            .limitResultsPerGroup(100)
            .filter(SCENE_COUNT.atMost(3), SCENE_COUNT.atLeast(6))//Filter out scene count = 5
            .get();

        assertEquals(2, groups.keySet().size());
    }

    @Test
    public void testFilterGroupQuery3() {
        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT)
            .limitResultsPerGroup(100)
            .filter(group(ID.equalTo("play1").or(ID.equalTo("play2"))).and(REALLY_BIG_NUMBER.atMost(2L)))
            .get();

        assertEquals(1, groups.keySet().size());
        assertEquals("play2", groups.get("5").get(0).getId());
    }
    /*
     * Force type means we dont dynamically decide which schema to use
     * when creating the item. it is forced to use the schema provided.
     *
     * This gives you full control over which wrapper object is used on a
     * solr document but also makes it more possible for programming errors because
     * you may try to access data that dosent exist on that document
     */

    @Test
    public void testForceType() {
        Optional<Book> op = books.id("play1").forceType().get();
        assertTrue(op.isPresent());

        Book book = op.get();
        assertEquals("The Play that got away", book.getTitle());
    }

    /*
     * Yes null pointer exception. trying to access missing data
     * is a programming error
     */
    @Test(expected = NullPointerException.class)
    public void testForceTypeMissingData() {
        Optional<Book> op = books.id("play1").forceType().get();
        assertTrue(op.isPresent());

        Book book = op.get();

        //This should throw an exception because plays dont have a chapter count
        book.getChapterCount();
    }

    /*
     * Join queries compress two searches into one
     */

    @Test
    public void testJoinQuery() {
        //Get all plays that are in a collection
        List<Play> results = plays.joinFrom(CHILD_IDS)
            .joinTo(ID)
            .get();

        Collection<String> ids = Collections2.transform(results, playSchema.id);
        assertTrue(ids.containsAll(ImmutableSet.of("play1", "play2", "play4")));
        assertTrue(!ids.contains("play3"));
    }

    @Test
    public void testJoinQueryWithFilter() {
        //Get plays that are in a specific collection
        List<Play> results = plays.joinFrom(CHILD_IDS)
            .filter(ID.equalTo("collection1"))
            .joinTo(ID)
            .get();

        Collection<String> ids = Collections2.transform(results, playSchema.id);
        assertTrue(ids.containsAll(ImmutableSet.of("play1", "play2")));
    }

    @Test
    public void testJoinQueryWithFilter2() {
        List<Play> results = plays.joinFrom(CHILD_IDS)
            .filter(ID.equalTo("collection1"))
            .joinTo(ID)//do the join
            .filter(REALLY_BIG_NUMBER.equalTo(1L))//This time filter out play1
            .get();

        Collection<String> ids = Collections2.transform(results, playSchema.id);
        assertTrue(ids.contains("play2"));
        assertTrue(!ids.contains("play1"));
    }

    @Test
    public void testJoinQueryWithFilter3() {
        List<Play> results = plays.joinFrom(CHILD_IDS)
            .filter(ID.equalTo("collection1"))
            .joinTo(ID)//do the join
            //Test it behaves well with Term list filters (default op is OR)
            .filter(REALLY_BIG_NUMBER.equalTo(1L), REALLY_BIG_NUMBER.equalTo(1234567L))
            .get();

        Collection<String> ids = Collections2.transform(results, playSchema.id);
        assertTrue(ids.containsAll(ImmutableSet.of("play1", "play2")));
    }

    @Test
    public void testJoinQueryWithFilter4() {
        List<Play> results = plays.joinFrom(CHILD_IDS)
            //Test it behaves well with Term list filters (default op is OR)
            .filter(ID.equalTo("collection1"), ID.equalTo("collection2"))
            .joinTo(ID)//do the join
            .filter(REALLY_BIG_NUMBER.equalTo(1234567L))
            .get();

        Collection<String> ids = Collections2.transform(results, playSchema.id);
        assertTrue(ids.containsAll(ImmutableSet.of("play1", "play4")));
    }
}
