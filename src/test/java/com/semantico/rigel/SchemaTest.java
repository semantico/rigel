package com.semantico.rigel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.semantico.rigel.test.items.Play.Schema;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@RunWith(JUnit4.class)
public class SchemaTest {

    @Test
    public void testSchemaCreation() {
        Schema testSchema = new Schema();

        Config config = ConfigFactory.load("test-config.properties");
        RigelContext.builder().withConfig(config).registerSchemas(testSchema).build();
    }

}
