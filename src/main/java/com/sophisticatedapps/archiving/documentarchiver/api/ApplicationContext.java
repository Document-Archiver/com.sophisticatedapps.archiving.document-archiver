package com.sophisticatedapps.archiving.documentarchiver.api;

import javafx.application.HostServices;
import javafx.stage.Stage;

public interface ApplicationContext {

    /**
     * Get an ApplicationServices instance.
     *
     * @return  An ApplicationServices instance
     */
    ApplicationServices getApplicationServices();

    /**
     * Get an DialogProvider instance.
     *
     * @return  An DialogProvider instance
     */
    DialogProvider getDialogProvider();

    /**
     * Get the application's HostServices.
     *
     * @return  The application's HostServices
     */
    HostServices getHostServices();

    /**
     * Get the application's primary Stage.
     *
     * @return  The application's primary Stage
     */
    Stage getPrimaryStage();

}
