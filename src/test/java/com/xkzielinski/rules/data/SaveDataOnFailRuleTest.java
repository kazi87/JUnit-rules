package com.xkzielinski.rules.data;

import java.nio.charset.Charset;

import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;

/**
 * TODO: Add description...
 */
public class SaveDataOnFailRuleTest {

    @Rule
    public SaveDataOnFailRule onFailRule = new SaveDataOnFailRule();

    @Test
    public void dataShouldBeSavedInTargetFolder() throws Exception {
        // given
        byte[] testData = "Test Data".getBytes(Charset.defaultCharset());
        onFailRule.initData(testData);
        // when
        // then
        assertTrue(false);
    }

}
