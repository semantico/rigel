package com.semantico.rigel;

import org.testng.annotations.Test;

import com.semantico.rigel.test.items.TestContentItem.Schema;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Test
public class SchemaTest {

    @Test
    public void testSchemaCreation() {
        Schema testSchema = new Schema();

        Config config = ConfigFactory.load("test-config.properties");
        new RigelContext(config, testSchema);
    }

}
