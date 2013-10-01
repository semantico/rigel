package com.semantico.rigel;

import org.testng.annotations.Test;

import com.semantico.rigel.TestContentItem.TestSchema;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static org.testng.Assert.*;

@Test
public class ConfigurableFieldTest {

    @Test
    public void testMultipleContext() {
        Config config1 = ConfigFactory.load("test-config.properties");
        RigelContext context1 = new RigelContext(config1);

        TestSchema schema1 = new TestSchema();

        context1.registerSchema(schema1);

        Config config2 = ConfigFactory.load("test-config2.properties");
        RigelContext context2 = new RigelContext(config2);

        TestSchema schema2 = new TestSchema();

        context2.registerSchema(schema2);

        assertTrue(schema1.id.getField().getFieldName().equals("id"));
        assertTrue(schema2.id.getField().getFieldName().equals("s2_id"));
    }

}
