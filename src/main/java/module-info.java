module DocumentArchiver {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires javafx.fxml;
    requires PDFViewerFX;
    requires java.desktop;
    requires org.apache.commons.imaging;

    opens com.sophisticatedapps.archiving.documentarchiver;
    opens com.sophisticatedapps.archiving.documentarchiver.type;
    opens com.sophisticatedapps.archiving.documentarchiver.model;
    opens com.sophisticatedapps.archiving.documentarchiver.util;
    opens com.sophisticatedapps.archiving.documentarchiver.view;
    opens com.sophisticatedapps.archiving.documentarchiver.controller;
}
