package com.sophisticatedapps.archiving.documentarchiver.api.impl;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.api.DialogProvider;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;

public class DefaultDialogProvider implements DialogProvider {

    @Override
    public Dialog<ButtonType> provideDecideWhatToOpenDialog(boolean aShowWelcomeMessage) {

        ImageView tmpImageView = new ImageView(GlobalConstants.APP_ICON);
        tmpImageView.setFitWidth(80);
        tmpImageView.setFitHeight(80);

        ButtonType tmpOpenFilesButtonType = new ButtonType(
                LanguageUtil.i18n("dialog-provider.decide-what-to-open-dialog.open-files-button.text"),
                ButtonBar.ButtonData.YES);
        ButtonType tmpOpenDirectoryButtonType = new ButtonType(
                LanguageUtil.i18n("dialog-provider.decide-what-to-open-dialog.open-directory-button.text"),
                ButtonBar.ButtonData.NO);

        String tmpContentTextKey = "dialog-provider.decide-what-to-open-dialog.content-text.welcome-message-"
                .concat(String.valueOf(aShowWelcomeMessage));
        String tmpTitleKey = "dialog-provider.decide-what-to-open-dialog.title.welcome-message-"
                .concat(String.valueOf(aShowWelcomeMessage));

        Dialog<ButtonType> tmpDialog = new Dialog<>();
        tmpDialog.setGraphic(tmpImageView);
        tmpDialog.setTitle(LanguageUtil.i18n(tmpTitleKey));
        tmpDialog.setHeaderText(LanguageUtil.i18n("dialog-provider.decide-what-to-open-dialog.header-text"));
        tmpDialog.setContentText(LanguageUtil.i18n(tmpContentTextKey));
        tmpDialog.getDialogPane().getButtonTypes().addAll(tmpOpenFilesButtonType, tmpOpenDirectoryButtonType);

        return tmpDialog;
    }

    @Override
    public Alert provideDirectoryDoesNotContainFilesAlert() {

        return (new Alert(Alert.AlertType.WARNING,
                LanguageUtil.i18n("menu-bar-controller.dialog-provider.directory-does-not-contain-files-alert"),
                ButtonType.CLOSE));
    }

    @Override
    public Alert provideExceptionAlert(String aMsg) {

        return (new Alert(Alert.AlertType.ERROR, aMsg, ButtonType.CLOSE));
    }

}
