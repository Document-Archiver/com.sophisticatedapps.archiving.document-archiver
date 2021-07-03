package com.sophisticatedapps.archiving.documentarchiver;

import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LauncherTest {

    @Test
    void testIsStageReady() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        TestLauncher tmpTestLauncher = new TestLauncher();

        assertFalse(tmpTestLauncher.callIsStageReady());

        FieldUtils.writeStaticField(Launcher.class, "primaryStage", tmpMockedStage, true);

        assertFalse(tmpTestLauncher.callIsStageReady());

        when(tmpMockedStage.isShowing()).thenReturn(true);

        assertTrue(tmpTestLauncher.callIsStageReady());

        FieldUtils.writeStaticField(Launcher.class, "primaryStage", null, true);
    }

    private static class TestLauncher extends Launcher {

        private boolean callIsStageReady() {

            return Launcher.isStageReady();
        }
    }

}
