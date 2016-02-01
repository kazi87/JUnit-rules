package com.xkzielinski.rules.data;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Saves the registered data when a test failed.
 * The data is stored in 'target' folder.
 */
public class SaveDataOnFailRule extends TestWatcher {

    private final static Logger LOGGER = LoggerFactory.getLogger(SaveDataOnFailRule.class);

    private String dataId;
    private byte[] data;


    public void initData(byte[] data) {
        this.data = data;
    }

    /**
     * Initialize  data to be stored.
     * @param dataId - Data id (can be null).
     * @param data - data as a byte array
     */
    public void initData(String dataId, byte[] data) {
        this.data = data;
        this.dataId = dataId;
    }


    @Override
    protected void failed(Throwable e, Description description) {
        try {
            if (data != null) {
                StringBuilder sb = new StringBuilder();
                if (dataId != null) {
                    sb.append(dataId);
                    sb.append("_");
                }
                sb.append(description.getMethodName());
                sb.append(".data");
                String name = sb.toString();
                File file = new File("target", name);
                FileUtils.writeByteArrayToFile(file, data);
            }
        } catch (IOException e1) {
            LOGGER.error("Can not save data for failing test", e1);
        }
    }
}
