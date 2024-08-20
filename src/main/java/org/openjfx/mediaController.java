package org.openjfx;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;


import java.io.File;
import java.net.URL;
import java.util.*;



public class mediaController implements Initializable {

    public long durationInSeconds;
    public String path;
    public Media media;
    public MediaPlayer mediaPlayer;
    public  boolean isPlaying = false;
    public boolean fileSelected = false;
    Map<String, Double> SpeedVal= Map.of("0.5x",0.5,"0.75x",0.75,"normal",1.0,"1.5x",1.5,"2x",2.0);




    @FXML
    MediaView mediaView;
    @FXML
    Slider slider;
    @FXML
    Label timeStamp;

    @FXML
    Slider VolumeSlider;
    @FXML
    Button PlayOrPause;
    @FXML
    ChoiceBox<String> SpeedChoice;
    @FXML
    MenuBar menuBar;
    @FXML
    BorderPane borderPane;
    @FXML
    VBox vbox;
    @FXML
    HBox hbox1,hbox2;
    @FXML
    StackPane centerPane;












    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> sortedKeys = SpeedVal.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();


        SpeedChoice.getItems().addAll(sortedKeys);
        SpeedChoice.setValue("normal");
        SpeedChoice.valueProperty().addListener((observable, oldValue, newValue) -> setSpeed(SpeedVal.get(newValue)));
        borderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldWidth, newWidth) -> reSize());
                newScene.heightProperty().addListener((o, oldHeight, newHeight) -> reSize());
            }
        });

        setNodesDisable();





    }


    @FXML
    public void chooseFile() {
        // File chooser to pick a media file

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Media File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            path = file.toURI().toString();
            initializeMediaAndSliderPlayer();
            fileSelected= true;
            setNodesDisable();

        }else {

            fileSelected = false;
            setNodesDisable();
        }
    }



    private void initializeMediaAndSliderPlayer() {
        // Initialize the media and media player after selecting a valid file
        media = new Media(path);

        if (mediaPlayer != null) {
            mediaPlayer.stop();  // Stop any previous media player instance
        }


        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

       reSize();
        checkkeyPress();






        mediaPlayer.setOnReady(() -> {
            Duration duration = mediaPlayer.getMedia().getDuration();


            durationInSeconds = (long) duration.toSeconds();

            slider.setMin(0);
            slider.setMax(durationInSeconds);


            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {


                slider.setValue(newTime.toSeconds());


                int hour = (int) newTime.toHours();
                int minute = (int) newTime.toMinutes() % 60;
                int second = (int) newTime.toSeconds() % 60;
                timeStamp.setText(String.format("%02d:%02d:%02d", hour, minute, second));
            });
           play();
            setVolumeSlider();

        });


        // Listen to slider value changes and update media player time accordingly
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (slider.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
            if (slider.getValue() == slider.getMax()) {
                slider.setValue(0);
                mediaPlayer.seek(Duration.seconds(0));
                pause();


            }
        });




    }
    public void setSpeed(double speed)
    {
        mediaPlayer.setRate(speed);
    }



    public void setVolumeSlider() {
        mediaPlayer.setVolume(0.5);
        VolumeSlider.setValue(50);
        VolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue()==0)
            {
                mediaPlayer.setMute(true);
            }
            else{
                mediaPlayer.setMute(false);

            mediaPlayer.setVolume(newValue.doubleValue()/100);
            }

        });

    }

    public void checkkeyPress()
    {
        Scene scene =mediaView.getParent().getScene();

        scene.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.SPACE)
            {
                playOrPause();
            }


        });


    }



    @FXML
    public void playOrPause() {

        if(!isPlaying)
        {
            play();
        }
        else
        {
            pause();
        }

    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
            PlayOrPause.setText("pause");
        }
    }


    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            PlayOrPause.setText("play");

        }
    }


    public void reSize() {
        // Make MenuBar expand to fill width
        menuBar.prefWidthProperty().bind(borderPane.widthProperty());

        // Make bottom VBox adjust its width
        vbox.prefWidthProperty().bind(borderPane.widthProperty());


        centerPane.prefWidthProperty().bind(borderPane.widthProperty());
        centerPane.prefHeightProperty().bind(borderPane.heightProperty());

        // Adjust MediaView size while preserving aspect ratio
        mediaView.fitWidthProperty().bind(centerPane.widthProperty());
        mediaView.fitHeightProperty().bind(centerPane.heightProperty());
        mediaView.setPreserveRatio(true);


        StackPane.setAlignment(mediaView, Pos.CENTER);


        VBox.setVgrow(centerPane, Priority.ALWAYS);
    }
    public void setNodesDisable()
    {
        if (!fileSelected)
        {
            slider.setDisable(true);
            VolumeSlider.setDisable(true);
            PlayOrPause.setDisable(true);
            SpeedChoice.setDisable(true);


        }else {
            slider.setDisable(false);
            VolumeSlider.setDisable(false);
            PlayOrPause.setDisable(false);
            SpeedChoice.setDisable(false);

        }
    }



}
