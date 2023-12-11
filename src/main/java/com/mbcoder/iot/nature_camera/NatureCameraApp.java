package com.mbcoder.iot.nature_camera;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.PullResistance;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

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


  public static void main(String[] args) {

    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {

    // set the title and size of the stage and show it
    stage.setTitle("Nature camera app");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.show();

    // create a JavaFX scene with a stack pane as the root node and add it to the scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    stage.setScene(scene);

    var pi4j = Pi4J.newAutoContext();

    var config = DigitalInput.newConfigBuilder(pi4j)
        .id("pir-sensor")
        .address(17)
        .pull(PullResistance.PULL_DOWN)
        .build();

    // get a Digital Input I/O provider from the Pi4J context
    DigitalInputProvider digitalInputProvider = pi4j.provider("pigpio-digital-input");

    var input = digitalInputProvider.create(config);

    var state = input.state();


    // setup a digital output listener to listen for any state changes on the digital input
    input.addListener(event -> {
      Integer count = (Integer) event.source().metadata().get("count").value();

      System.out.println(event + " === " + count);
    });

    Button btnState = new Button("state update");
    btnState.setOnAction(event -> {
      System.out.println("button state = " + input.state());
    });

    System.out.print("THE STARTING DIGITAL INPUT STATE IS [");
    System.out.println(input.state() + "]");



  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

  }
}

