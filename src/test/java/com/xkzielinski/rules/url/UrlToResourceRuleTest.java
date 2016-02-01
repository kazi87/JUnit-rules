package com.xkzielinski.rules.url;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * TODO: Add description...
 */
public class UrlToResourceRuleTest {

    @ClassRule
    public static UrlToResourceRule resourceRule = new UrlToResourceRule(
            new String[][] {
                { "some.url.com/resource", "/File.txt" }
            });

    @Test
    public void theStreamShouldNotBeNull() throws Exception {
        // given
        URL url = new URL("http://some.url.com/resource");
        // when
        InputStream is = url.openStream();
        // then
        assertNotNull(is);
    }

    @Test
    public void shouldReadResourceFile() throws Exception {
        // given
        URL url = new URL("http://some.url.com/resource");
        String expectedText = "Test file content";
        // when
        InputStream is = url.openStream();
        // then
        String textFromStream = IOUtils.toString(is);
        assertEquals(expectedText, textFromStream);
    }

}
