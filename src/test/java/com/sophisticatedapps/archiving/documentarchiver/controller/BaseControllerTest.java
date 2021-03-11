/*
 * Copyright 2021 by Stephan Sann (https://github.com/stephansann)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import javafx.collections.MapChangeListener;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
class BaseControllerTest extends BaseTest {

    private BaseController baseController;

    @Mock
    MapChangeListener<Object, Object> mapChangeListener;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, DOCUMENTS_LIST);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, TEST_TEXT_FILE);

        baseController = new BaseController(){};
        baseController.rampUp(aStage);
    }

    @Test
    void testRampDown() {

        // Ramp down
        baseController.rampDown();

        // Stage should be set to NULL now
        assertNull(baseController.stage);
    }

    @Test
    void getCurrentDocument() {

        final File tmpCurrentDocument = baseController.getCurrentDocument();
        assertSame(TEST_TEXT_FILE, tmpCurrentDocument);
    }

    @Test
    void getAllDocuments() {

        final List<File> tmpAllDocuments = baseController.getAllDocuments();
        assertSame(DOCUMENTS_LIST, tmpAllDocuments);
    }

    @Test
    void addListenerForProperty() {

        // Add listener
        baseController.addListenerForProperty(mapChangeListener, GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);

        // Change current document value
        baseController.setNewCurrentDocument(TEST_TEXT_FILE2);

        // Verify listener was called
        verify(mapChangeListener,
                Mockito.times(1)).onChanged(any(MapChangeListener.Change.class));
    }

    @Test
    void addAllDocumentsChangedListener() {

        // Add listener
        baseController.addAllDocumentsChangedListener(mapChangeListener);

        // Change all documents value
        baseController.setNewAllDocuments(Collections.singletonList(TEST_TEXT_FILE2));

        // Verify listener was called
        verify(mapChangeListener,
                Mockito.times(1)).onChanged(any(MapChangeListener.Change.class));
    }

    @Test
    void addCurrentDocumentChangedListener() {

        // Add listener
        baseController.addCurrentDocumentChangedListener(mapChangeListener);

        // Change current document value
        baseController.setNewCurrentDocument(TEST_TEXT_FILE2);

        // Verify listener was called
        verify(mapChangeListener,
                Mockito.times(1)).onChanged(any(MapChangeListener.Change.class));
    }

    @Test
    void setNewAllDocumentsAndCurrentDocument() {

        List<File> tmpNewAllDocuments = Collections.singletonList(TEST_TEXT_FILE2);

        baseController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, TEST_TEXT_FILE2);
        assertSame(TEST_TEXT_FILE2, baseController.getCurrentDocument());
        assertSame(tmpNewAllDocuments, baseController.getAllDocuments());
    }

    @Test
    void setNewAllDocuments() {

        List<File> tmpNewAllDocuments = Collections.singletonList(TEST_TEXT_FILE2);

        baseController.setNewAllDocuments(tmpNewAllDocuments);
        assertSame(tmpNewAllDocuments, baseController.getAllDocuments());
    }

    @Test
    void setNewCurrentDocument() {

        baseController.setNewCurrentDocument(TEST_TEXT_FILE2);
        assertSame(TEST_TEXT_FILE2, baseController.getCurrentDocument());
    }

}
