package com.example.stoper;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private LocalTime startTime;
    private LocalTime stopTime;
    private boolean running = false;
    private final List<String> laps = new ArrayList<>();
    private Label timerLabel;
    private AnimationTimer timer;
    private Duration elapsedTime = Duration.ZERO;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        timerLabel = new Label("00:00:00.000");
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button addLapButton = new Button("Dodaj punkt");

        startButton.setOnAction(event -> startTimer());
        stopButton.setOnAction(event -> stopTimer());
        addLapButton.setOnAction(event -> addLap());

        VBox root = new VBox(10, timerLabel, startButton, stopButton, addLapButton);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-font-size: 16;");

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Stoper");
        primaryStage.setScene(scene);
        primaryStage.show();

        setupTimer();
    }

    private void setupTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    elapsedTime = Duration.between(startTime, LocalTime.now());
                    updateTimerLabel(elapsedTime);
                }
            }
        };
    }

    private void startTimer() {
        if (!running) {
            startTime = LocalTime.now();
            running = true;
            timer.start();
        }
    }

    private void stopTimer() {
        if (running) {
            stopTime = LocalTime.now();
            timer.stop();
            running = false;
            elapsedTime = Duration.between(startTime, stopTime);
            updateTimerLabel(elapsedTime);
            saveResultsToFile();
        }
    }

    private void addLap() {
        if (running) {
            String lapTime = formatDuration(elapsedTime);
            laps.add("Punkt " + laps.size() + 1 + ": " + lapTime);
        }
    }

    private void updateTimerLabel(Duration elapsedTime) {
        timerLabel.setText(formatDuration(elapsedTime));
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    private void saveResultsToFile() {
        try (FileWriter writer = new FileWriter("wyniki_stopera.txt")) {
            writer.write("Start: " + formatDuration(Duration.between(LocalTime.MIDNIGHT, startTime)) + "\n");
            for (String lap : laps) {
                writer.write(lap + "\n");
            }
            writer.write("Stop: " + formatDuration(elapsedTime) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}