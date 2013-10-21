package com.semantico.rigel;

import static com.semantico.rigel.TestFields.*;
import static org.junit.Assert.assertTrue;
import static com.semantico.rigel.filters.Filters.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.semantico.rigel.filters.BasicTerm;
import com.semantico.rigel.test.items.TestItem;

public class FilterEmulationTest extends IntergrationTestBase {

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

        items = rigel.getContentRepository(testItemSchema);
    }

    @AfterClass
    public static void afterClass() {
        shutDown();
    }

    private void testFilter(BasicTerm term) {
        List<TestItem> matching = items.all().filter(term).forceType().get();

        for (TestItem item : matching) {
            assertTrue(term.apply(item.getSolrDoc()));
        }
        //Query the inverse & make sure they dont match
        List<TestItem> notMatching = items.all().filter(prohibit(term)).forceType().get();

        for (TestItem item : notMatching) {
            assertTrue(!term.apply(item.getSolrDoc()));
        }
    }

    @Test
    public void testEverything() {
        testFilter(everything());
    }

    @Test
    public void testEqualToInt() {
        testFilter(SCENE_COUNT.equalTo(5));
    }

    @Test
    public void testEqualToString() {
        testFilter(ID.equalTo("play3"));
    }

    @Test
    public void testEqualToString2() {
        testFilter(TITLE.equalTo("Yet Another Play"));
    }

    @Test
    public void testEqualToLong() {
        testFilter(REALLY_BIG_NUMBER.equalTo(1234567L));
    }

    @Test
    public void testStartsWith() {
        testFilter(ID.startsWith("pla"));
    }

    @Test
    public void testStartsWith2() {
        testFilter(AUTHOR.startsWith("King"));
    }

    @Test
    public void testAnd() {
        testFilter(group(AUTHOR.startsWith("King").and(SCENE_COUNT.equalTo(2))));
    }

    @Test
    public void testOr() {
        testFilter(group(SCENE_COUNT.equalTo(5).or(SCENE_COUNT.equalTo(6))));
    }

    @Test
    public void testGroupBoolean() {
        testFilter(group(group(SCENE_COUNT.equalTo(5).and(AUTHOR.startsWith("K"))).or(SCENE_COUNT.equalTo(2))));
    }

    @Test
    public void testTermList() {
        testFilter(group(require(SCENE_COUNT.equalTo(5)), prohibit(REALLY_BIG_NUMBER.equalTo(1L))));
    }
}
