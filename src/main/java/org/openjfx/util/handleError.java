package org.openjfx.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.openjfx.Controller.mediaController;

public class handleError {
    Alert alert;
    mediaController controller;

   public handleError()
    {
        Loader loader = new Loader();
         controller=loader.giveController();


    }

   public void handleMediaError(String message) {
        Platform.runLater(() -> {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Media Error");
            alert.setHeaderText(message);
            alert.setContentText("The file may be corrupted or in an unsupported format.");
            alert.show();
        });


    }
    public void QueueError(String message) {
       Platform.runLater(() -> {
           alert = new Alert(Alert.AlertType.CONFIRMATION);
           alert.setTitle("Queue Completed");
           alert.setHeaderText(message);
           alert.setContentText("Do you want to play again? or close the player?");

           if(alert.showAndWait().get().equals(ButtonType.CANCEL))
           {
               Platform.exit();
               System.exit(0);




           }


       });
    }
}