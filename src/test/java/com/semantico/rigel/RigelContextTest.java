package com.semantico.rigel;

import org.testng.annotations.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static org.testng.Assert.*;

@Test
public class RigelContextTest {

    @Test
    public void testRigelContextCreation() {
        Config config = ConfigFactory.load("test-config.properties");
        RigelContext context = new RigelContext(config);
        assertNotNull(context);
    }

}
