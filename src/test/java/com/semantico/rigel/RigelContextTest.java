package com.semantico.rigel;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@RunWith(JUnit4.class)
public class RigelContextTest {

    @Test
    public void testRigelContextCreation() {
        Config config = ConfigFactory.load("test-config.properties");
        RigelContext context = RigelContext.builder().withConfig(config).build();
        assertNotNull(context);
    }

}
