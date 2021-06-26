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
    public Dialog<ButtonType> provideWelcomeDialog() {

        ImageView tmpImageView = new ImageView(GlobalConstants.APP_ICON);
        tmpImageView.setFitWidth(80);
        tmpImageView.setFitHeight(80);

        ButtonType tmpOpenFilesButtonType = new ButtonType(
                LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.open-files-button.text"),
                ButtonBar.ButtonData.YES);
        ButtonType tmpOpenDirectoryButtonType = new ButtonType(
                LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.open-directory-button.text"),
                ButtonBar.ButtonData.NO);

        Dialog<ButtonType> tmpDialog = new Dialog<>();
        tmpDialog.setGraphic(tmpImageView);
        tmpDialog.setTitle(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.title"));
        tmpDialog.setHeaderText(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.header-text"));
        tmpDialog.setContentText(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.content-text"));
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
