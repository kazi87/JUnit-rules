package com.xkzielinski.rules.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.Validate;

/**
 * Implementation of URLStreamHandlerFactory.
 * Extends the logic of the protocol handlers by additional the resource pattern scanning.
 *
 * Support protocols: http, https, file;
 */
class TestResourcesStreamHandlerFactory implements java.net.URLStreamHandlerFactory {

    /**
     * The URL resource mapping
     */
    private final static Map<String, TestResourceConnection> testResourceConnectionMapping = new ConcurrentHashMap<>();

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals("http")) {
            return new TestResourcesHttpHandler();
        } else if (protocol.equals("https")) {
            return new TestResourcesHttpsHandler();
        } else if (protocol.equals("file")) {
            return new TestResourcesFileHandler();
        }
        return null;
    }

    public void registerResource(String pattern, Class clazz, String path) {
        Validate.notNull(path, "Resource path is null");
        Validate.notNull(pattern, "The resource pattern is null");
        Validate.notNull(clazz, "Resource class is null");
        testResourceConnectionMapping.put(pattern, new TestResourceConnection(clazz, path));
    }

    private URLConnection getTestResourceConnection(URL url) {
        for (Map.Entry<String, TestResourceConnection> entry : testResourceConnectionMapping.entrySet()) {
            if (url.toString().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private class TestResourcesHttpHandler extends sun.net.www.protocol.http.Handler {

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            URLConnection entry = getTestResourceConnection(url);
            if (entry != null) {
                return entry;
            }
            return super.openConnection(url);
        }

    }

    private class TestResourcesHttpsHandler extends sun.net.www.protocol.https.Handler {

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            URLConnection entry = getTestResourceConnection(url);
            if (entry != null) {
                return entry;
            }
            return super.openConnection(url);
        }

    }

    private class TestResourcesFileHandler extends sun.net.www.protocol.file.Handler {

        @Override
        public synchronized URLConnection openConnection(URL url) throws IOException {
            URLConnection entry = getTestResourceConnection(url);
            if (entry != null) {
                return entry;
            }
            return super.openConnection(url);
        }
    }

    private class TestResourceConnection extends URLConnection {

        private final Class clazz;
        private final String resourcePath;

        /**
         * A simple URLConnection implementation.
         * Reads a file from given resourcePath using a classLoader from the clazz.
         */
        protected TestResourceConnection(Class clazz, String resourcePath) {
            super(null);
            this.clazz = clazz;
            this.resourcePath = resourcePath;
        }

        @Override
        public void connect() throws IOException {
            // skip
        }

        @Override
        public InputStream getInputStream() throws IOException {
            InputStream resourceAsStream = clazz.getResourceAsStream(resourcePath);
            if (resourceAsStream == null) {
                throw new IllegalStateException("Resource: " + resourcePath +
                    "is not available for the ClassLoader of: " + clazz);
            }
            return resourceAsStream;
        }
    }


}
