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

package com.sophisticatedapps.archiving.documentarchiver;

import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Base class for tests.
 */
public abstract class BaseTest {

    protected static final File TEST_TEXT_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/test.txt")).getFile()));
    protected static final File TEST_TEXT_FILE2 = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/2/test2.txt")).getFile()));
    protected static final File TEST_PDF_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/test.pdf")).getFile()));
    protected static final File TEST_JPG_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/test.jpg")).getFile()));
    protected static final File TEST_JPG_FILE2 = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/2/test2.jpg")).getFile()));
    protected static final File TEST_MP3_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/test.mp3")).getFile()));
    protected static final File TEST_DOCX_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder/test.docx")).getFile()));

    protected static final File TEST_PNG_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("binder-icon.png")).getFile()));

    protected static final List<File> ALL_DOCUMENTS_LIST = Arrays.asList(TEST_TEXT_FILE, TEST_TEXT_FILE2, TEST_PDF_FILE,
            TEST_JPG_FILE, TEST_JPG_FILE2, TEST_MP3_FILE, TEST_DOCX_FILE);

    protected static final File TEST_RESOURCES_DIRECTORY = TEST_TEXT_FILE.getParentFile().getParentFile();
    protected static final File TEST_SOURCE_FOLDER = TEST_TEXT_FILE.getParentFile();
    protected static final File TEST_EMPTY_SOURCE_FOLDER = new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-source-folder_empty")).getFile());
    protected static final File TEST_ARCHIVING_FOLDER = new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test-archiving-folder")).getFile());

    protected static final EventType<KeyEvent> MOCK_EVENT_TYPE = new EventType<>("MockEventType");
    protected static final KeyEvent MOCK_KEY_EVENT_WITH_CODE_ENTER = new KeyEvent(MOCK_EVENT_TYPE,
            "\r", "enter key", KeyCode.ENTER,false,false, false, false);
    protected static final KeyEvent MOCK_KEY_EVENT_WITH_CODE_DOWN = new KeyEvent(MOCK_EVENT_TYPE,
            "↓", "down key", KeyCode.DOWN,false,false, false, false);
    protected static final KeyEvent MOCK_KEY_EVENT_WITH_CODE_UP = new KeyEvent(MOCK_EVENT_TYPE,
            "↑", "up key", KeyCode.UP,false,false, false, false);

    protected static final String NUL_CHARACTER_STRING = String.valueOf('\0');

}
