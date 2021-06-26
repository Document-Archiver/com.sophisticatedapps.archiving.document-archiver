package com.sophisticatedapps.archiving.documentarchiver.api.impl;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.api.DialogProvider;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class DefaultDialogProviderTest {

    @Test
    void provideWelcomeDialog() {

        DialogProvider tmpDialogProvider = new DefaultDialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.provideWelcomeDialog()));

        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        assertSame(GlobalConstants.APP_ICON, ((ImageView)tmpDialog.getGraphic()).getImage());
        assertEquals("Welcome to Document Archiver", tmpDialog.getTitle());
        assertEquals("Thanks for using Document Archiver!", tmpDialog.getHeaderText());
        assertTrue(tmpDialog.getContentText().startsWith("Next you will have to choose what you want to archive."));
    }

    @Test
    void provideDirectoryDoesNotContainFilesAlert() {

        DialogProvider tmpDialogProvider = new DefaultDialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideDirectoryDoesNotContainFilesAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("The chosen directory doesn't contain files.", tmpAlert.getContentText());
    }

    @Test
    void provideExceptionAlert() {

        DialogProvider tmpDialogProvider = new DefaultDialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideExceptionAlert("This is a test")));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("This is a test", tmpAlert.getContentText());
    }

}
