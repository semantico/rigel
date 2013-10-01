package com.semantico.rigel;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.semantico.rigel.TestContentItem.TestSchema;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Test
public class SchemaTest {

    public RigelContext context;

    @BeforeMethod
    public void setUp() {
        Config config = ConfigFactory.load("test-config.properties");
        context = new RigelContext(config);
    }

    @Test
    public void testSchemaCreation() {
        TestSchema testSchema = new TestContentItem.TestSchema();
        context.registerSchema(testSchema);
    }

}
