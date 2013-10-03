package com.semantico.rigel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.semantico.rigel.test.items.Play.Schema;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static org.junit.Assert.*;


@RunWith(JUnit4.class)
public class ConfigurableFieldTest {

    @Test
    public void testMultipleContext() {
        //Test that the id field registered in two different contexts
        //with different configs, resolves to different solr fields

        Config config1 = ConfigFactory.load("test-config.properties");
        Schema schema1 = new Schema();
        RigelContext.builder()
            .withConfig(config1)
            .registerSchemas(schema1)
            .build();

        Config config2 = ConfigFactory.load("test-config2.properties");
        Schema schema2 = new Schema();
        RigelContext.builder()
            .withConfig(config2)
            .registerSchemas(schema2)
            .build();

        assertTrue(schema1.id.getField().getFieldName().equals("id"));
        assertTrue(schema2.id.getField().getFieldName().equals("s2_id"));
    }

    @Test(expected = RuntimeException.class)
    public void testRejectMultipleContexts() {
        //Test that the same instance of a schema cant be registered
        //in two different contexts

        Schema schema = new Schema();

        Config config1 = ConfigFactory.load("test-config.properties");
        RigelContext.builder()
            .withConfig(config1)
            .registerSchemas(schema)
            .build();

        Config config2 = ConfigFactory.load("test-config2.properties");
        RigelContext.builder()
            .withConfig(config2)
            .registerSchemas(schema)
            .build();
    }

}
