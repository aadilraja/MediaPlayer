module sample {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;



    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}