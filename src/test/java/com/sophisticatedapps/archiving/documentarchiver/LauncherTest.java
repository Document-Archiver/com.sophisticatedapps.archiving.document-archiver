package com.sophisticatedapps.archiving.documentarchiver;

import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LauncherTest {

    @Test
    void testIsStageReady() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        Launcher tmpLauncher = new Launcher();

        Object tmpResult = MethodUtils.invokeStaticMethod(Launcher.class, "isStageReady");
        assertFalse(((Boolean)tmpResult).booleanValue());

        FieldUtils.writeStaticField(Launcher.class, "primaryStage", tmpMockedStage, true);

        tmpResult = MethodUtils.invokeStaticMethod(Launcher.class, "isStageReady");
        assertFalse(((Boolean)tmpResult).booleanValue());

        when(tmpMockedStage.isShowing()).thenReturn(true);

        tmpResult = MethodUtils.invokeStaticMethod(Launcher.class, "isStageReady");
        assertTrue(((Boolean)tmpResult).booleanValue());
    }

}
