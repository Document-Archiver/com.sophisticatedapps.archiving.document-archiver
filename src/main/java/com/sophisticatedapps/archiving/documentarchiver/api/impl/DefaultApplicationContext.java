package com.sophisticatedapps.archiving.documentarchiver.api.impl;

import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.controller.ApplicationController;
import javafx.stage.Stage;

public class DefaultApplicationContext implements ApplicationContext {

    private final ApplicationController applicationController;
    private final Stage primaryStage;

    public DefaultApplicationContext(ApplicationController anApplicationController, Stage aPrimaryStage) {

        applicationController = anApplicationController;
        primaryStage = aPrimaryStage;
    }

    @Override
    public ApplicationController getApplicationController() {

        return applicationController;
    }

    @Override
    public Stage getPrimaryStage() {

        return primaryStage;
    }

}
