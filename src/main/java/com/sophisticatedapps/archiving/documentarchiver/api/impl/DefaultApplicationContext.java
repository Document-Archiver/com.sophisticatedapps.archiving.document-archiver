package com.sophisticatedapps.archiving.documentarchiver.api.impl;

import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationServices;
import com.sophisticatedapps.archiving.documentarchiver.api.DialogProvider;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class DefaultApplicationContext implements ApplicationContext {

    private final ApplicationServices applicationServices;
    private final DialogProvider dialogProvider;
    private final HostServices hostServices;
    private final Stage primaryStage;

    public DefaultApplicationContext(ApplicationServices anApplicationServices, DialogProvider aDialogProvider,
                                     HostServices aHostServices, Stage aPrimaryStage) {

        applicationServices = anApplicationServices;
        dialogProvider = aDialogProvider;
        hostServices = aHostServices;
        primaryStage = aPrimaryStage;
    }

    @Override
    public ApplicationServices getApplicationServices() {

        return applicationServices;
    }

    @Override
    public DialogProvider getDialogProvider() {

        return dialogProvider;
    }

    @Override
    public HostServices getHostServices() {

        return hostServices;
    }

    @Override
    public Stage getPrimaryStage() {

        return primaryStage;
    }

}
