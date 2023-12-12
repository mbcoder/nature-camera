package com.mbcoder.iot.nature_camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

        /*
        BufferedReader br = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
        String line;
        while (true) {
          try {
            if (!((line = br.readLine()) != null)) break;
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          System.out.println(line);
        }

         */
      }
    };

    Thread pythonThread = new Thread(pythonRunnable);
    pythonThread.start();

    // create a JavaFX scene with a stack pane as the root node and add it to the scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    stage.setScene(scene);

  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws IOException {
    // stop the python pir camera detector
    Runtime.getRuntime().exec("kill " + pid);
  }
}

