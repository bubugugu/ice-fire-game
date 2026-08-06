module com.example.final_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;

    exports com.example.final_project;
    opens com.example.final_project to javafx.fxml, javafx.graphics;
}