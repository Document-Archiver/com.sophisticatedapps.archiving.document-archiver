module DocumentArchiver {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires PDFViewerFX;
    requires java.desktop;
    requires org.apache.commons.imaging;

    opens com.sophisticatedapps.archiving.documentarchiver;
}