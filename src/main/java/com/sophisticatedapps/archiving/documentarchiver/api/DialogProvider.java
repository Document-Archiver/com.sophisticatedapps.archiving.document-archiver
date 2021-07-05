package com.sophisticatedapps.archiving.documentarchiver.api;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public interface DialogProvider {

    Dialog<ButtonType> provideDecideWhatToOpenDialog(boolean aShowWelcomeMessage);

    Alert provideDirectoryDoesNotContainFilesAlert();

    Alert provideExceptionAlert(String aMsg);
}
