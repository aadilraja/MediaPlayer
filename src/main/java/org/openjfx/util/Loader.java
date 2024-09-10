package org.openjfx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.openjfx.Controller.mediaController;

import java.io.IOException;

public class Loader {
    private final FXMLLoader Mainloader;


    public Loader() {
        Mainloader = new FXMLLoader(getClass().getResource("/org/openjfx/View.fxml"));
    }

    public Parent giveRoot() throws IOException {
        return Mainloader.load();  // Load the FXML file and initialize the controller
    }



    public mediaController giveController() {
        return Mainloader.getController();  // Now you can safely return the controller
    }



}
