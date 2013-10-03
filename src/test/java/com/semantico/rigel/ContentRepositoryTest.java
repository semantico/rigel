package com.semantico.rigel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.base.Optional;
import com.google.common.collect.ListMultimap;
import com.semantico.rigel.test.items.Author;
import com.semantico.rigel.test.items.Book;
import com.semantico.rigel.test.items.Play;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static org.junit.Assert.*;
import static com.semantico.rigel.TestFields.*;

@RunWith(JUnit4.class)
public class ContentRepositoryTest {

    private static Config config;
    private static RigelContext rigel;
    private static SolrServer server;

    private static Play.Schema playSchema;
    private static Book.Schema bookSchema;

    public static String getSolrHome() {
        try {
            File file = new File(Thread.currentThread().getContextClassLoader().getResource("solr/collection1/conf/solrconfig.xml").toURI());
            return file.getParentFile().getParentFile().getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void beforeClass() throws SolrServerException, IOException {
        playSchema = new Play.Schema();
        bookSchema = new Book.Schema();

        System.setProperty("solr.solr.home", getSolrHome());
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        server = new EmbeddedSolrServer(coreContainer, "collection1");


        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "play1");
        doc.addField("type", "play");
        doc.addField("dc_title", "The Play that got away");
        doc.addField("dc_creator", "King, Edd");
        doc.addField("dc_issued", new Date());
        doc.addField("scene_count", 5);
        doc.addField("big_num", 123456789123456L);

        server.add(doc);
        server.commit();

        config = ConfigFactory.load("test-config.properties");
        rigel = RigelContext.builder()
            .withConfig(config)
            .registerSchemas(playSchema, bookSchema)
            .customSolrServer(server)
            .build();
    }

    @AfterClass
    public static void afterClass() {

    }

    @Test
    public void testAllQuery() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

        List<Play> results = plays.all().get();
        assertTrue(results.size() == 1);
    }

    @Test
    public void testIdQuery() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

        Optional<Play> op = plays.id("play1").get();

        Play play = op.get();
        Author theAuthor = play.getAuthor().iterator().next();
        assertEquals("Edd", theAuthor.getGivenName());
    }

    @Test
    public void testGroupQuery() {
        ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

        ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT).get();

        List<Play> group = groups.get("5");
        assertEquals(1, group.size());

    }
}
