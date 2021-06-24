package com.sophisticatedapps.archiving.documentarchiver.api;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public interface DialogProvider {
    Dialog<ButtonType> provideWelcomeDialog();

    Alert provideDirectoryDoesNotContainFilesAlert();

    Alert provideExceptionAlert(String aMsg);
}
