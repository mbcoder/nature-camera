package com.mbcoder.iot.nature_camera;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

public class NatureCameraApp extends Application {
  private long pid;
  private Timer fileCheckTimer;
  private String cameraID = "";
  private String serviceTableURL = "https://services1.arcgis.com/6677msI40mnLuuLr/ArcGIS/rest/services/NatureCamera/FeatureServer/1";
  private ServiceFeatureTable table;


  public static void main(String[] args) {

    Application.launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {

    // set the title and size of the stage and show it
    stage.setTitle("Nature camera app");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.show();


    Runnable pythonRunnable = new Runnable() {
      @Override
      public void run() {
        Process pythonProcess;
        try {
          pythonProcess = Runtime.getRuntime().exec(new String[]{"python3","pircamera.py"});
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        // capture the process id so we can stop it later
        pid = pythonProcess.pid();

      }
    };

    // start the python process on a new thread
    Thread pythonThread = new Thread(pythonRunnable);
    pythonThread.start();

    // create a JavaFX scene with a stack pane as the root node and add it to the scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    stage.setScene(scene);

    // connect to table we will be recording images into
    table = new ServiceFeatureTable(serviceTableURL);
    table.loadAsync();
    table.addDoneLoadingListener(()-> {
      System.out.println("table loaded");
      // timer for reading sensor and logging results
      fileCheckTimer = new Timer();
      fileCheckTimer.schedule(new TimerTask() {
        public void run() {
          // check for files
          System.out.println("checking for new files");
          checkImageFiles();

        }
      }, 1000, 5000); // check for new files every 5 seconds
    });





    Button btnListFiles = new Button("list files");
    btnListFiles.setOnAction(event -> {
      //Creating a File object for directory
      File directoryPath = new File("images");
      //List of all files and directories
      String contents[] = directoryPath.list();

      for (String file : contents) {
        System.out.println("file - " + file);
      }
    });
    stackPane.getChildren().add(btnListFiles);

  }

  /**
   * Checks for new image files created by the python pir camera script
   */
  private void checkImageFiles() {
    //Creating a File object for directory
    File directoryPath = new File("images");
    //List of all files and directories
    String contents[] = directoryPath.list();

    for (String file : contents) {
      System.out.println("file - " + file);
    }

  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws IOException {
    // stop the python pir camera detector
    Runtime.getRuntime().exec("kill " + pid);

    // stop timer which looks for new files
    if (fileCheckTimer != null) fileCheckTimer.cancel();
  }
}

