package org.openjfx.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    Map<String, Double> SpeedVal= Map.of("0.5x",0.5,"0.75x",0.75,"Normal",1.0,"1.5x",1.5,"1.75",1.75,"2x",2.0);

    List<File> files = new ArrayList<>();
    public int index = 0;
    ContextMenu contextMenu = new ContextMenu();
    ImageView playIcon, pauseIcon,backIcon,forwardIcon,defaultAudioImg;
    Alert alert;






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
    @FXML
    Button backwards;
    @FXML
    Button forward;













    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Image defaultaudio=new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/audioDefault.png"))));
        defaultAudioImg=new ImageView(defaultaudio);



        Image playIconImg=new Image(Objects.requireNonNull(getClass().getResource("/images/playIcon.png")).toExternalForm());
        Image PauseIconImg=new Image(Objects.requireNonNull(getClass().getResource("/images/Group 3.png")).toExternalForm());

        playIcon = new ImageView(playIconImg);
        pauseIcon=new ImageView(PauseIconImg);


        playIcon.fitHeightProperty().bind(PlayOrPause.heightProperty().multiply(0.7));
        playIcon.fitWidthProperty().bind(PlayOrPause.widthProperty().multiply(0.7));
        playIcon.setPreserveRatio(true);

        pauseIcon.fitHeightProperty().bind(PlayOrPause.heightProperty().multiply(0.5));
        pauseIcon.fitWidthProperty().bind(PlayOrPause.widthProperty().multiply(0.5));
        pauseIcon.setPreserveRatio(true);

        PlayOrPause.setGraphic(pauseIcon);


        Image backwardIconImg=new Image(Objects.requireNonNull(getClass().getResource("/images/backwordIcon.png")).toExternalForm());

        backIcon = new ImageView(backwardIconImg);
        backIcon.fitHeightProperty().bind(backwards.heightProperty());
        backIcon.fitWidthProperty().bind(backwards.widthProperty());
        backIcon.setPreserveRatio(true);
        backwards.setGraphic(backIcon);

        Image forwardIconImg=new Image(Objects.requireNonNull(getClass().getResource("/images/forwardIcon.png")).toExternalForm());

        forwardIcon = new ImageView(forwardIconImg);
        forwardIcon.fitHeightProperty().bind(forward.heightProperty());
        forwardIcon.fitWidthProperty().bind(forward.widthProperty());
        forwardIcon.setPreserveRatio(true);
        forward.setGraphic(forwardIcon);





        VolumeSlider.setValue(50);

        List<String> sortedKeys = SpeedVal.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();


        SpeedChoice.getItems().addAll(sortedKeys);
        SpeedChoice.setValue("Normal");
        SpeedChoice.valueProperty().addListener((_, _, newValue) -> setSpeed(SpeedVal.get(newValue)));


        MenuItem play = new MenuItem("play");
        MenuItem pause = new MenuItem("pause");
        MenuItem addfile = new MenuItem("Add file");
        contextMenu.getItems().addAll(play, pause, addfile);
        play.setOnAction((_) -> play());
      pause.setOnAction((_) ->pause());
        addfile.setOnAction((_) -> chooseFile());
        mediaView.setMouseTransparent(true);

        centerPane.setOnMousePressed((event) -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(centerPane, event.getScreenX(), event.getScreenY());
            }
            else
            {
                contextMenu.hide();
            }
        });
        setNodesDisable(true);
    }


    @FXML
    public void chooseFile() {
        // File chooser to pick a media file

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Media File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File file = fileChooser.showOpenDialog(null);



        if (file != null) {
            files.addLast(file);

            if (mediaPlayer == null || mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                setNodesDisable(false);
                settingMediaPlayer();
            }



        }else {


            setNodesDisable(true);
        }
    }
    @FXML
    public void MultiChooseFile()
    {
        if(!files.isEmpty()){
            files.clear();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Media File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        List<File> fileList = fileChooser.showOpenMultipleDialog(null);
       files.addAll(fileList);
        if(!files.contains(null))
        {

            if (mediaPlayer == null || mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                setNodesDisable(false);
                settingMediaPlayer();
            }




        }else {
            setNodesDisable(true);
        }
        






    }




    private void settingMediaPlayer() {
        // Initialize the media and media player after selecting a valid file
        reSize();
        Stage stage=(Stage) borderPane.getScene().getWindow();
        if(index<files.size()) {


            path = files.get(index).toURI().toString();


            media = new Media(path);






            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            checKeyPress();



            mediaPlayer.setOnReady(() -> {
                String fileName=files.get(index).getName();
                stage.setTitle(fileName);
                mediaPlayer.setVolume(0.5);

                if (isAudioFile(fileName)) {
                    mediaView.setVisible(false);
                    centerPane.getChildren().clear();
                    centerPane.getChildren().add(defaultAudioImg);
                    defaultAudioImg.fitHeightProperty().bind(centerPane.heightProperty().subtract(20.5));
                    defaultAudioImg.fitWidthProperty().bind(centerPane.widthProperty().subtract(20.5));
                    defaultAudioImg.setPreserveRatio(true);

                } else {
                    mediaView.setVisible(true);
                    centerPane.getChildren().clear();
                    centerPane.getChildren().add(mediaView);
                }




                setVideoTime();
                play();

                setVolumeSlider(0.0);

            });
            mediaPlayer.setOnError(() -> {
                System.out.println("Media Player Error: " + mediaPlayer.getError().getMessage());

                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Media Player Error");
                alert.setHeaderText("Some error occurred while playing media file");
                alert.setContentText("Restart the application");
                alert.showAndWait();
                files.clear();
                setNodesDisable(true);

            });












            mediaPlayer.setOnEndOfMedia(() -> {
                // Play the next media in the queue when the current one finishes

                  if(index<files.size()) {
                      index = index+1;
                      settingMediaPlayer();
                  }
                    else
                  {
                      mediaPlayer.seek(Duration.millis(0));
                      slider.setValue(0);
                      pause();
                  }


            });


        }
        else
        {
             alert = new Alert(Alert.AlertType.INFORMATION);


            alert.setContentText("Queue is empty");

        }
    }
    public boolean isAudioFile(String fileName)
    {
        String fileExtensions=fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        List<String> AUDIO_EXTENSIONS = Arrays.asList("mp3", "wav", "aiff", "aac", "m4a");
        return AUDIO_EXTENSIONS.contains(fileExtensions);
    }



    @FXML
    public void PlayNext()
    {
        if(index<files.size()-1) {
            index++;
            mediaPlayer.stop();
            settingMediaPlayer();
        }
        else
        {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("There is no next media ");
            alert.showAndWait();
        }

    }
    @FXML
    public void PlayPrev()
    {
        if(index>0) {
            index--;
            mediaPlayer.stop();
            settingMediaPlayer();
        }
        else
        {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("There is no previous media");
            alert.showAndWait();
        }

    }

    public void setVideoTime()
    {
        Duration duration = media.getDuration();


        durationInSeconds = (long) duration.toSeconds();

        slider.setMin(0);
        slider.setMax(durationInSeconds);


        mediaPlayer.currentTimeProperty().addListener((_, _, newTime) -> {


            slider.setValue(newTime.toSeconds());


            int hour = (int) newTime.toHours();
            int minute = (int) newTime.toMinutes() % 60;
            int second = (int) newTime.toSeconds() % 60;
            timeStamp.setText(String.format("%02d:%02d:%02d", hour, minute, second));
        });
        slider.valueProperty().addListener((_, _, newValue) -> {
            if (slider.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }

        });




    }







    public void setSpeed(double speed)
    {
        mediaPlayer.setRate(speed);

    }



    public void setVolumeSlider(double vol) {
        mediaPlayer.setVolume(mediaPlayer.getVolume()+vol);
        VolumeSlider.setValue(VolumeSlider.getValue()+(vol*10));
        VolumeSlider.valueProperty().addListener((_, _, newValue) -> {
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

    public void checKeyPress()
    {
        Scene scene =borderPane.getScene();

        scene.setOnKeyPressed(event -> {
           KeyCode keyAction = event.getCode();
                event.consume();
          switch (keyAction) {
              case SPACE: playOrPause();
                          break;
              case UP: setVolumeSlider(0.1);
                        break;
              case DOWN: setVolumeSlider(-0.1);
                        break;
              case LEFT: jump(false);
                         break;

              case RIGHT: jump(true);
                          break;

          }


        });



    }
    public void jump(boolean Forward) {
        Duration currentTime = mediaPlayer.getCurrentTime();
        Duration jumpTime = Duration.seconds(20.0);
        Duration newTime;

        if (Forward) {
            newTime = currentTime.add(jumpTime);

        } else {
            newTime = currentTime.subtract(jumpTime);
            if (newTime.lessThan(Duration.ZERO)) {
                newTime = Duration.ZERO; // Prevent seeking to a negative time
            }
        }

        // Ensure the new time is within the media duration
        if (newTime.greaterThan(mediaPlayer.getTotalDuration())) {
            newTime = mediaPlayer.getTotalDuration();
        }

        Duration finalNewTime = newTime;
        Platform.runLater(() -> {
            mediaPlayer.seek(finalNewTime);
            slider.setValue(finalNewTime.toSeconds());
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
            PlayOrPause.setGraphic(playIcon);

        }
    }


    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            PlayOrPause.setGraphic(pauseIcon);


        }
    }


    public void reSize()
    {
        centerPane.prefWidthProperty().bind(centerPane.getScene().widthProperty());

       mediaView.fitWidthProperty().bind(centerPane.widthProperty().subtract(20.5));
       mediaView.fitHeightProperty().bind(centerPane.heightProperty());
       mediaView.setPreserveRatio(true);
    }
    public void setNodesDisable(boolean fileSelected)
    {
       vbox.setDisable(fileSelected);
    }



}
