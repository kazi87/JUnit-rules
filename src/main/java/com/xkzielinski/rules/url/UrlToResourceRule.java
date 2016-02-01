package com.xkzielinski.rules.url;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a JUnit rule, which allows to redirect a remote URI request to defined local resource file.
 * It can be used to mock remote calls in JUnit tests.
 * <p>
 * Internally it registers a URLStreamHandlerFactory with custom handlers.
 * The handlers scan an url and based on mapping read the proper test resource.
 * <p>
 * <p>
 * It is recommended to register it as a {@code ClassRule}!
 * Usage:
 * <pre>
 *   ClassRule
 *  public static UrlToResourceRule uriResourceMappingRule = new UrlToResourceRule(new String[][] {
 *     { "http://remote.address.com/resource", "/some/test/resource/file.txt" }
 *     { "http://remote.address.com/resource2", "/some/test/resource/file2.txt" }
 *      ...
 *   });
 *   </pre>
 */
public class UrlToResourceRule extends ExternalResource {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrlToResourceRule.class);

    private final TestResourcesStreamHandlerFactory streamHandlerFactory;

    private final Map<String, String> urlMapping;

    public UrlToResourceRule(String[][] mapping) {
        streamHandlerFactory = new TestResourcesStreamHandlerFactory();
        urlMapping = new HashMap<>();
        if (ArrayUtils.isNotEmpty(mapping)) {
            for (String[] resource : mapping) {
                Validate.isTrue(ArrayUtils.getLength(resource) == 2,
                    "The resource mapping is invalid. Required format: {{url1, resource1}, {url2, resource2}}");
                urlMapping.put(resource[0], resource[1]);
            }
        }
        // finally register the streamHandlerFactory
        URL.setURLStreamHandlerFactory(streamHandlerFactory);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        for (Map.Entry<String, String> entry : urlMapping.entrySet()) {
            LOGGER.debug("The urlToResource mapping: " + entry.getKey() + " -> " + entry.getValue());
            streamHandlerFactory.registerResource(entry.getKey(), description.getTestClass(), entry.getValue());
        }
        return super.apply(base, description);
    }
}
