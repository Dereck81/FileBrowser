module pe.edu.utp.filebrowser {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.instrument;
    requires com.sun.jna.platform;
    requires com.sun.jna;

    opens pe.edu.utp.filebrowser to javafx.fxml;
    exports pe.edu.utp.filebrowser;
    exports pe.edu.utp.filebrowser.Controllers;
    exports pe.edu.utp.filebrowser.DSA;
    opens pe.edu.utp.filebrowser.Controllers to javafx.fxml;
    exports pe.edu.utp.filebrowser.FileSystem;
    opens pe.edu.utp.filebrowser.FileSystem to javafx.fxml;
    exports pe.edu.utp.filebrowser.Enums;
    opens pe.edu.utp.filebrowser.Enums to javafx.fxml;
}