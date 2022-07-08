module DocumentArchiver {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires javafx.fxml;
    requires javafx.media;
    requires com.install4j.runtime;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires com.dlsc.pdfviewfx;
    requires org.apache.commons.lang3;
    requires com.jthemedetector;
    requires java.desktop;
    requires org.apache.commons.imaging;
    requires mammoth;
    requires org.apache.poi.scratchpad;
    requires Restart4j;
    requires org.apache.commons.collections4;
    requires net.harawata.appdirs;
    requires versioncompare;
    requires net.kurobako.gesturefx;
    requires awaitility;

    opens com.sophisticatedapps.archiving.documentarchiver;
    opens com.sophisticatedapps.archiving.documentarchiver.api;
    opens com.sophisticatedapps.archiving.documentarchiver.controller;
    opens com.sophisticatedapps.archiving.documentarchiver.model;
    opens com.sophisticatedapps.archiving.documentarchiver.type;
    opens com.sophisticatedapps.archiving.documentarchiver.util;
    opens com.sophisticatedapps.archiving.documentarchiver.view;
    opens com.sophisticatedapps.archiving.documentarchiver.api.impl;

    exports com.sophisticatedapps.archiving.documentarchiver;
    exports com.sophisticatedapps.archiving.documentarchiver.api;
    exports com.sophisticatedapps.archiving.documentarchiver.controller;
    exports com.sophisticatedapps.archiving.documentarchiver.model;
    exports com.sophisticatedapps.archiving.documentarchiver.type;
	exports com.sophisticatedapps.archiving.documentarchiver.util;
    exports com.sophisticatedapps.archiving.documentarchiver.api.impl;

    uses com.sophisticatedapps.archiving.documentarchiver.api.ArchiveBrowsingService;
}
