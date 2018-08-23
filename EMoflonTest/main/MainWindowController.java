package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulation.DisconnectedException;
import simulation.Server;
import translation.IOHandler;
import translation.NothingChosenException;
import translation.Translation;

public class MainWindowController {
	@FXML private Label classLabel;
	@FXML private Label methodLabel;
	@FXML private Label portLabel;
	@FXML private Button startButton;
	@FXML private Button chooseDirectoryButton;
	@FXML private Button chooseInstanceButton;
	@FXML private Button translateButton;
	@FXML private Button logButton;
	@FXML private ChoiceBox<String> classChoiceBox;
	@FXML private ChoiceBox<String> methodChoiceBox;
	@FXML private TextArea console;
	@FXML private TextField portField;
	
	private Translation translation = Translation.getTranslation();
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private FileChooser instanceChooser = new FileChooser();
	private boolean classChanged = false;
	private boolean first = false;
	private List<String> logList = new ArrayList<String>();
	private String instanceName;
	
	
	public void initialize() {
		portField.setText("9000");
		console.setWrapText(true);
		write("Choose the directory of your eMoflon project which you want to simulate with CPN tools.", true);
		
		classChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				try {
					String chosenClass = classChoiceBox.getItems().get((Integer) newValue);
					translation.chooseClass(chosenClass);
					classChanged = true;
					methodChoiceBox.setItems(FXCollections.observableArrayList(translation.getMethods()));
				}
				catch(RuntimeException e) {
					exceptionOccured();
				}
			}
		});
		
		methodChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(classChanged && first) {
					classChanged = false;
					return;
				}
				first = true;
				try {
					String chosenMethod = methodChoiceBox.getItems().get((Integer) newValue);
					translation.chooseMethod(chosenMethod);
				}
				catch(RuntimeException e) {
					exceptionOccured();
				}
			}
		});
	}
	
	@FXML
	private void chooseDirectory() {
		File directory = directoryChooser.showDialog(Main.stage);
		if(directory == null) {
			write("Invalid directory. Try again.", true);
		}
		else {
			try {
				translation.load(directory);
				classChoiceBox.setItems(FXCollections.observableArrayList(translation.getClasses()));
				write("Choose a class and then a method.", true);
			}
			catch(RuntimeException e) {
				write("Failed to read the eMoflon project.", true);
			}
		}
	}
	
	@FXML
	private void translate() {
		//System.out.println(methodChoiceBox.getSelectionModel().getSelectedItem().toString());
		try {
			translation.translate(Integer.parseInt(portField.getText()));
			write("Choose a dynamic instance for the simulation.", true);
		}
		catch(NothingChosenException e) {
			write(e.getMessage(), true);
		}
		catch(RuntimeException e) {
			e.printStackTrace();
			write("Failed to translate eMoflon code into CPN code", true);
		}
		
	}
	
	@FXML
	private void chooseInstance() {
		if(translation.getDirectoryHandler() == null) {
			write("Choose a directory before choosing an instance.", true);
			return;
		}
		System.out.println(methodLabel.toString());	
		instanceChooser.setInitialDirectory(translation.getDirectoryHandler().getXmi());
		instanceChooser.setTitle("Open Instance File");
		instanceChooser.getExtensionFilters().addAll(new ExtensionFilter("Xmi Files", "*.xmi"));
		File instance = instanceChooser.showOpenDialog(Main.stage);
		instanceName = instance.getName().replace(".xmi", "");
		translation.chooseInstance(instance);
		write("Start CPN Tools. The new file is located in your eMoflon_project_folder/cpn.", false);
		write("Wait for it to open and start the simulation in CPN Tools.", false);
		write("Then hit the start button.", true);
	}
	
	@FXML
	private void start() {
		Integer port = translation.getPort();
		if(port == null) {
			write("eMoflon project have not been translated yet.", true);
			return;
		}
		
		Server server = new Server(port, this::write, logList);
		server.start();
	}
	
	@FXML
	private void log() {
		File log = translation.getDirectoryHandler().getLogFile(instanceName);
		try {
			IOHandler.write(log, logList.stream().collect(Collectors.joining("\n")));
			write("Logged the simulation successfully.", true);
		} catch (IOException e) {
			exceptionOccured();
		}
	}
	
	private void write(String msg, boolean doubleNewLine) {
		if(doubleNewLine)
			console.appendText(msg + "\n\n");
		else
			console.appendText(msg + "\n");
	}
	
	private void exceptionOccured() {
		console.appendText("Someting went wrong\n");
	}
}
