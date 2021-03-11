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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for tests.
 */
public class BaseTest {

    protected static final File TEST_TEXT_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test.txt")).getFile()));
    protected static final File TEST_TEXT_FILE2 = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test2.txt")).getFile()));
    protected static final File TEST_PDF_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test.pdf")).getFile()));
    protected static final File TEST_PNG_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("binder-icon.png")).getFile()));

    protected static final List<File> DOCUMENTS_LIST = Arrays.asList(TEST_TEXT_FILE, TEST_TEXT_FILE2, TEST_PDF_FILE);

    protected static final File RESOURCES_DIRECTORY = TEST_TEXT_FILE.getParentFile();

}
