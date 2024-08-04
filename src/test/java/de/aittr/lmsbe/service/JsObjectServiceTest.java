package de.aittr.lmsbe.service;

import de.aittr.lmsbe.service.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsZoomObjectServiceTestData {

    @Test
    void jsObjectToJSON() {
        String jsObjectString = TestUtils.readFileToString("json/jsObject.js");
        assertEquals(
                "{cohort: [\"25\",\"26\"], module: \"basic_programming\", type: \"lecture\", lesson: \"lesson26\", topic: \"Hello, World!!!\"}",
                jsObjectString
        );
    }
}
