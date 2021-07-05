package com.sophisticatedapps.archiving.documentarchiver.api;

import com.sophisticatedapps.archiving.documentarchiver.controller.ApplicationController;
import javafx.stage.Stage;

public interface ApplicationContext {

    /**
     * Get an ApplicationController instance.
     *
     * @return  An ApplicationController instance
     */
    ApplicationController getApplicationController();

    /**
     * Get the application's primary Stage.
     *
     * @return  The application's primary Stage
     */
    Stage getPrimaryStage();

}
