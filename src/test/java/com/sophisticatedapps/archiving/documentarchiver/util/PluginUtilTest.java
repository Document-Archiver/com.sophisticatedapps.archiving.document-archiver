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

package com.sophisticatedapps.archiving.documentarchiver.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(ApplicationExtension.class)
class PluginUtilTest {

    @Test
    void testIsPluginAvailable() {

        assertFalse(PluginUtil.isPluginAvailable("foobar"));
    }

    @Test
    void testFireArchiveBrowsingPlugin() {

        Stage tmpMockedStage = Mockito.mock(Stage.class);

        PluginUtil.fireArchiveBrowsingPlugin(tmpMockedStage);

        verify(tmpMockedStage, Mockito.times(1)).setScene(any(Scene.class));
    }

}
