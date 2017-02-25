package sample;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Scanner;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        TextArea textArea = new TextArea();
        textArea.setId("HackerBox");
        AtomicInteger i = new AtomicInteger(0);
        String kernel = "";
        Scanner s = new Scanner(getClass().getResource("kernel.txt").toExternalForm());
        while(s.hasNextLine()){
            kernel = kernal + s.nextLine();
        }
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene1 = new Scene(root, 348, 106);
        Stage stage = new Stage();
        stage.setTitle("Access Granted");
        stage.setScene(scene1);
        stage.initStyle(StageStyle.UNDECORATED);
        AtomicInteger count = new AtomicInteger(0);
        textArea.setOnKeyPressed(e->{
            if(e.getCode()==KeyCode.BACK_SPACE){
                if(count.get()==3){
                    stage.show();
                    PauseTransition p = new PauseTransition(Duration.seconds(3));

                    p.setOnFinished(er->{
                        System.exit(0);
                    });
                    p.play();
                    count.set(0);
                }else {
                    count.incrementAndGet();
                }
            }else {
                count.set(0);
            }
        });
        textArea.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                event.consume();
                textArea.appendText(kernel.substring(i.get(), i.get() + 4));
                i.set(i.get() + 4);

            }
        });
        Scene scene = new Scene(textArea, 300,300);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
