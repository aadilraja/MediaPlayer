package org.openjfx.Controller;
import org.openjfx.util.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
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

   public List<File> files = new ArrayList<>();
    public int index = 0;
    ContextMenu contextMenu = new ContextMenu();
    handleError hE;
    ImageView playIcon, pauseIcon,backIcon,forwardIcon,defaultAudioImg,speakerIcon,MuteSpeakerIcon;
    Alert alert;
    Stage stage;






    @FXML
   public MediaView mediaView;
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
   public BorderPane borderPane;
    @FXML
    VBox vbox;
    @FXML
    HBox hbox1,hbox2;
    @FXML
  public   StackPane centerPane;
    @FXML
    Button backwards;
    @FXML
    Button forward;
    @FXML
    AnchorPane DragAndDrop;

    @FXML
    Label SelectMediaDirectlyText;
    @FXML
    Button speaker;















    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       hE = new handleError();
       settingImage();

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





        mediaView.setOnMousePressed((event) -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(mediaView, event.getScreenX(), event.getScreenY());
            }
            else
            {
                contextMenu.hide();
            }
        });
        setNodesDisable(true);

        SelectMediaDirectlyText.setOnMouseClicked(_ -> chooseFile());



        // Initialize the stage after the scene is set
        Platform.runLater(() -> {
            stage = (Stage) borderPane.getScene().getWindow();
            reSize();  // Call reSize() after the stage is initialized
        });
        VolumeSlider.valueProperty().addListener((_, _, newValue) -> {
            if(newValue.doubleValue()!=0)
            {
                setSpeakerMute(false);


                mediaPlayer.setVolume(newValue.doubleValue()/100);

            }
            else{
                setSpeakerMute(true);
            }

        });

    }

    @FXML
    public void onDragDropped(DragEvent event)
    {
        Dragboard dragedMedia = event.getDragboard();
        if(event.getDragboard().hasFiles())
        {
            files.add(dragedMedia.getFiles().getFirst());
            settingMediaPlayer();





        }
        event.consume();


    }
    @FXML
    public void onDragOver(DragEvent event)
    {
        Dragboard dragMedia = event.getDragboard();
        if(dragMedia.hasFiles())
        {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();

    }



    @FXML
    public void chooseFile() {
        // File chooser to pick a media file

        FileChooser fileChooser = new FileChooser();





        fileChooser.setTitle("Open Media File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File file = fileChooser.showOpenDialog(borderPane.getScene().getWindow());



        if (file != null) {
            files.addLast(file);



            if (mediaPlayer == null || mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                settingMediaPlayer();
            }



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
                settingMediaPlayer();
            }




        }
        






    }




    public void settingMediaPlayer() {
        // Initialize the media and media player after selecting a valid file
        reSize();
        setNodesDisable(false);

        DragAndDrop.setVisible(false);
        DragAndDrop.setDisable(true);




        if(index<files.size()) {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }

            Platform.runLater(() -> {
                try {
                    Thread.sleep(100); // Small delay before loading new media
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Rest of your settingMediaPlayer code...
            });


            path = files.get(index).toURI().toString();


            media = new Media(path);
            media.setOnError(() -> {
                System.out.println("Media Error: " + media.getError().getMessage());
               hE.handleMediaError("Error loading media");
            });






            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            checKeyPress();



            mediaPlayer.setOnReady(() -> {
                String fileName=files.get(index).getName();
                stage.setTitle(fileName);
                VolumeSlider.setValue(50);
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

               hE.handleMediaError("Error loading media");
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

          hE.QueueError("All the songs in the queue have been played");
          index=0;
          settingMediaPlayer();

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


    @FXML
    public void SpeakerState()
    {
        setSpeakerMute(!mediaPlayer.isMute());

    }





    public void setSpeakerMute(boolean mute)
    {
        if (mute)
        {
            mediaPlayer.setMute(true);
            speaker.setGraphic(MuteSpeakerIcon);
            VolumeSlider.setValue(0);

        }
        else
        {
            mediaPlayer.setMute(false);
            speaker.setGraphic(speakerIcon);
            setVolumeSlider(0.0);
        }
    }



    public void setVolumeSlider(double vol) {

        double PlayerVolume = mediaPlayer.getVolume();
        double newVolume = PlayerVolume + vol;

        if (newVolume > 1.0) {
            newVolume = 1.0;
        } else if (newVolume < 0.0) {
            newVolume = 0.0;
        }

        mediaPlayer.setVolume(newVolume);
        VolumeSlider.setValue(newVolume * 100);


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
              case LEFT:jumpForward();
                         break;

              case RIGHT: JumpBackwards();
                          break;

          }


        });



    }
    @FXML
    public void jumpForward()
    {

        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));





    }
    @FXML
    public void JumpBackwards()
    {

        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));




    }




    @FXML
    public void playOrPause() {


        if(mediaPlayer.getStatus()==MediaPlayer.Status.PAUSED)
        {
            play();
        }
        else
        {
            pause();
        }

    }

    public void play() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.play();

                PlayOrPause.setGraphic(pauseIcon);

            }


        }catch (Exception e) {
            System.out.println("Error in setVideoTime(): " + e.getMessage());
           hE.handleMediaError("Error playing next media");

        }

    }


    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            PlayOrPause.setGraphic(playIcon);


        }
    }


    public void reSize()
    {
        mediaView.fitWidthProperty().bind(stage.widthProperty().subtract(10.0));
        mediaView.fitHeightProperty().bind(stage.heightProperty().subtract(10.0));


       mediaView.setPreserveRatio(true);
    }
    public void setNodesDisable(boolean fileSelected)
    {
       vbox.setDisable(fileSelected);
    }



    public  void settingImage()
    {

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


        speakerIcon =new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/images/speaker1.png")).toExternalForm()));
        MuteSpeakerIcon=new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/images/speaker.png")).toExternalForm()));

        speakerIcon.fitHeightProperty().bind(speaker.heightProperty());
        speakerIcon.fitWidthProperty().bind(speaker.widthProperty());

        MuteSpeakerIcon.fitHeightProperty().bind(speaker.heightProperty());
        MuteSpeakerIcon.fitWidthProperty().bind(speaker.widthProperty());

        speakerIcon.setPreserveRatio(true);
        MuteSpeakerIcon.setPreserveRatio(true);

        speaker.setGraphic(speakerIcon);







    }






}
