package com.semantico.rigel;

import org.testng.annotations.Test;

import com.semantico.rigel.TestContentItem.Schema;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static org.testng.Assert.*;

@Test
public class ConfigurableFieldTest {

    @Test
    public void testMultipleContext() {
        //Test that the id field registered in two different contexts
        //with different configs, resolves to different solr fields

        Config config1 = ConfigFactory.load("test-config.properties");
        Schema schema1 = new Schema();
        new RigelContext(config1, schema1);

        Config config2 = ConfigFactory.load("test-config2.properties");
        Schema schema2 = new Schema();
        new RigelContext(config2, schema2);

        assertTrue(schema1.id.getField().getFieldName().equals("id"));
        assertTrue(schema2.id.getField().getFieldName().equals("s2_id"));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testRejectMultipleContexts() {
        //Test that the same instance of a schema cant be registered
        //in two different contexts

        Schema schema = new Schema();

        Config config1 = ConfigFactory.load("test-config.properties");
        new RigelContext(config1, schema);

        Config config2 = ConfigFactory.load("test-config2.properties");
        new RigelContext(config2, schema);
    }

}
