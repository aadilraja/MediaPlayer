package org.openjfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.openjfx.Controller. mediaController;
import org.openjfx.util.*;


import java.io.IOException;
import java.util.Objects;

public class MediaLauncher extends Application {
    Loader loader;
    mediaController mc;




    @Override
    public void init() throws Exception {
         loader = new Loader();




    }


    @Override
    public void start(Stage stage) throws IOException,NullPointerException {

        Scene scene = new Scene(loader.giveRoot());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/View.css")).toExternalForm());

        Image icon =new Image(Objects.requireNonNull(getClass().getResource("/images/img.png")).toExternalForm());
        mc= loader.giveController();








        stage.getIcons().add(icon);




        stage.setTitle("Media Player");

        stage.setScene(scene);
        stage.show();




        stage.setOnCloseRequest(event -> {


             if (mc.mediaPlayer != null) {
                 mc.mediaPlayer.stop();
                 mc.mediaPlayer.dispose();
                 mc.files.clear();

             }
                Platform.exit();
                System.exit(0);


            });
    }

    public static void main(String[] args) {
        launch(args);
    }
}