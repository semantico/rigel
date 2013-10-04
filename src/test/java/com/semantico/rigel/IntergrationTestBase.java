package com.semantico.rigel;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;

import com.semantico.rigel.test.items.Book;
import com.semantico.rigel.test.items.Play;
import com.semantico.rigel.test.items.PlayCollection;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public abstract class IntergrationTestBase {

    private static SolrServer server;

    protected static RigelContext rigel;
    protected static Play.Schema playSchema;
    protected static PlayCollection.Schema collectionSchema;
    protected static Book.Schema bookSchema;

    public static String getSolrHome() {
        try {
            File file = new File(Thread.currentThread().getContextClassLoader().getResource("solr/collection1/conf/solrconfig.xml").toURI());
            return file.getParentFile().getParentFile().getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * call this in an @BeforeClass method
     */
    protected static void initialize(String propertiesFile) throws SolrServerException, IOException {
        System.setProperty("solr.solr.home", getSolrHome());
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        server = new EmbeddedSolrServer(coreContainer, "collection1");

        playSchema = new Play.Schema();
        bookSchema = new Book.Schema();
        collectionSchema = new PlayCollection.Schema();

        Config config = ConfigFactory.load(propertiesFile);
        rigel = RigelContext.builder()
            .withConfig(config)
            .registerSchemas(playSchema, bookSchema, collectionSchema)
            .customSolrServer(server)
            .build();
    }

    protected static SolrInputDocument createPlay(String id, String title, String author, Date date, int sceneCount, long bigNum) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(playSchema.type.getField().getFieldName(), "play");
        doc.addField(playSchema.id.getField().getFieldName(), id);
        doc.addField(playSchema.title.getField().getFieldName(), title);
        doc.addField(playSchema.author.getField().getFieldName(), author);
        doc.addField(playSchema.date.getField().getFieldName(), date);
        doc.addField(playSchema.sceneCount.getField().getFieldName(), sceneCount);
        doc.addField(playSchema.bigNum.getField().getFieldName(), bigNum);
        return doc;
    }

    protected static SolrInputDocument createBook(String id, String title, Date date, Integer chapterCount) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(bookSchema.type.getField().getFieldName(), "book");
        doc.addField(bookSchema.id.getField().getFieldName(), id);
        doc.addField(bookSchema.title.getField().getFieldName(), title);
        doc.addField(bookSchema.date.getField().getFieldName(), date);
        doc.addField(bookSchema.chapterCount.getField().getFieldName(), chapterCount);
        return doc;
    }

    protected static SolrInputDocument createPlayCollection(String collectionId, String... playIds) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(collectionSchema.id.getField().getFieldName(), collectionId);
        doc.addField(collectionSchema.type.getField().getFieldName(), "play-collection");
        for (String playId : playIds) {
            doc.addField(collectionSchema.playIds.getField().getFieldName(), playId);
        }
        return doc;
    }

    protected static void addAndCommit(Collection<SolrInputDocument> docs) {
        try {
            server.add(docs);
            server.commit();
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
