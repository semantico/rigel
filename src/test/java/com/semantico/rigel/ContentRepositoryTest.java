package com.semantico.rigel;

import static com.semantico.rigel.TestFields.SCENE_COUNT;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.base.Optional;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.semantico.rigel.test.items.Author;
import com.semantico.rigel.test.items.Book;
import com.semantico.rigel.test.items.Play;

@RunWith(JUnit4.class)
public class ContentRepositoryTest extends IntergrationTestBase {

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
                1234567L));

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

        addAndCommit(docs);
    }

    @Test
    public void testDefaultAllQuery() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

        List<Play> results = plays.all().get();
        assertEquals(results.size(), 4);
    }

    @Test
    public void testDefaultIdQuery() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

        Optional<Play> op = plays.id("play1").get();
        assertTrue(op.isPresent());
        //Make sure the data was retrieved as expected
        Play play = op.get();
        assertEquals(new Author("King, Edd"), play.getAuthor());
        assertEquals(new Integer(5), play.getSceneCount());
        assertEquals(new Long(1234567L), play.getBigNum());
        assertEquals("The Play that got away", play.getTitle());
    }

    @Test
    public void testDefaultGroupQuery() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);
        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT).get();

        //There are 3 distinct scene counts
        assertEquals(3, groups.keySet().size());

        //there are two items with 5 scenes, but the default only returns
        //one document per group
        assertEquals(1, groups.get("5").size());
    }

    @Test
    public void testGroupQuery2() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT).limitResultsPerGroup(100).get();

        List<Play> group = groups.get("5");
        assertEquals(2, group.size());
    }

    @Test
    public void testIdForceType() {
        ContentRepository<Book> books = rigel.getContentRepository(bookSchema);
        /*
         * Force type means we dont dynamically decide which schema to use
         * when creating the item. it is forced to use the schema provided.
         *
         * This gives you full control over which wrapper object is used on a
         * solr document
         */
        Optional<Book> op = books.id("play1").forceType().get();
        assertTrue(op.isPresent());

        Book book = op.get();
        assertEquals("The Play that got away", book.getTitle());

    }
}
