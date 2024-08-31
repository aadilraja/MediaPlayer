package org.openjfx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.openjfx.Controller.mediaController;

import java.io.IOException;

public class Loader {
    private FXMLLoader loader;


    public Loader() {
        loader = new FXMLLoader(getClass().getResource("/org/openjfx/View.fxml"));
    }

    public Parent giveRoot() throws IOException {
        return loader.load();  // Load the FXML file and initialize the controller
    }

    public mediaController giveController() {
        return loader.getController();  // Now you can safely return the controller
    }
}
