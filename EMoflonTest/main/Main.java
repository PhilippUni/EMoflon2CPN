package main;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	static Stage stage;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("/resources/MainWindow.fxml"));
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("eMoflon2Cpn");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
