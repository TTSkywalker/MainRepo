package view;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import model.FileModel;
import tools.DiskModel;

public class PropertyViewController {
	
	private DiskBlock block;
	private FAT fat;
	private Label icon;
	private Map<Path, TreeItem<String>> pathMap;
	private String oldName, location;
	private Stage dialogStage;

	@FXML
	public HBox hBox;
	@FXML
	public GridPane gridPane;
	@FXML
	public TextField nameField;
	@FXML
	public Label typeField;
	@FXML
	public Label locField;
	@FXML
	public Label sizeField;
	@FXML
	public Label spaceField;
	@FXML
	public Label timeField;
	@FXML
	public Label suffixField;
	@FXML
	public Button okButton;
	@FXML
	public Button cancelButton;
	@FXML
	public Button applyButton;
	@FXML
	public RadioButton checkRead;
	@FXML
	public RadioButton checkWrite;
	@FXML
	public HBox checkBoxGroup;

	private ToggleGroup toggleGroup = new ToggleGroup();

	private Image ico;
	
	private String name;

	public void init(DiskBlock block, FAT fat, Label icon, Map<Path, TreeItem<String>> pathMap){
		this.block = block;
		this.fat = fat;
		this.icon = icon;
		this.pathMap = pathMap;
		showView();
	}
	
	private void showView() {		
		checkRead.setToggleGroup(toggleGroup);
		checkRead.setUserData(DiskModel.FLAGREAD);
		
		checkWrite.setToggleGroup(toggleGroup);
		checkWrite.setUserData(DiskModel.FLAGWRITE);
		
		if (block.getObject() instanceof Folder) {
			Folder folder = (Folder)block.getObject();
			nameField.setText(folder.getFolderName());
			typeField.setText(folder.getType());
			locField.setText(folder.getLocation());
			sizeField.setText(folder.getSize() + "KB");
			spaceField.setText(folder.getSpace());
			timeField.setText(folder.getCreateTime());
			suffixField.setText("目录");
			oldName = folder.getFolderName();
			location = folder.getLocation();
			checkRead.setDisable(true);
			checkWrite.setDisable(true);
			ico = new Image(DiskModel.FOLDER_IMG);
		} else {
			FileModel file = (FileModel) block.getObject();
			nameField.setText(file.getFileName());
			typeField.setText(file.getType());
			locField.setText(file.getLocation());
			sizeField.setText(file.getSize() + "KB");
			spaceField.setText(file.getSpace());
			timeField.setText(file.getCreateTime());
			suffixField.setText(file.getSuffix());
			oldName = file.getFileName();
			location = file.getLocation();
			toggleGroup.selectToggle(file.getFlag() == DiskModel.FLAGREAD ? checkRead : checkWrite);
			ico = new Image(DiskModel.FILE_IMG);
		}
		
		name = nameField.getText();
		
		okButton.setStyle("-fx-background-color:#d3d3d3;");
		okButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #808080;");
			}
		});
		okButton.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});
		
		cancelButton.setStyle("-fx-background-color:#d3d3d3;");
		cancelButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cancelButton.setStyle("-fx-background-color: #ffffff;");
			}
		});
		cancelButton.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				cancelButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});
		
		applyButton.setStyle("-fx-background-color:#d3d3d3;");
		applyButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				applyButton.setStyle("-fx-background-color: #808080;");
			}
		});
		applyButton.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				applyButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});
		
		buttonOnAction();
		
		nameField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.equals("") || newValue.equals(name)) {
					applyButton.setDisable(true);
					okButton.setDisable(true);
				} else {
					applyButton.setDisable(false);
				}
			}
		});
		
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				applyButton.setDisable(false);
			}
		});
		
		gridPane.setPadding(new Insets(15, 12, 0, 12));
		gridPane.setVgap(10);
		gridPane.setHgap(10);
				
	}
	
	private void buttonOnAction() {		
		applyButton.setOnAction(ActionEvent -> {
			String newName = nameField.getText();
			if (!oldName.equals(newName)) {
				if (fat.hasName(location, newName)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("此位置已包含同名文件/文件夹");
					alert.show();
				} else {
					if (block.getObject() instanceof Folder) {
						Folder thisFolder = (Folder)block.getObject();
						thisFolder.setFolderName(newName);
						pathMap.get(thisFolder.getPath()).setValue(newName);
						reLoc(location, location, oldName, newName, thisFolder);
					} else {
						((FileModel)block.getObject()).setFileName(newName);
					}
					oldName = newName;
					icon.setText(newName);
				}				
			}
			if (block.getObject() instanceof FileModel) {
				FileModel thisFile = ((FileModel)block.getObject());
				int newFlag = toggleGroup.getSelectedToggle().getUserData().hashCode();
				thisFile.setFlag(newFlag);
			}
			applyButton.setDisable(true);
		});
		cancelButton.setOnAction(ActionEvent -> {
			dialogStage.close();
		});
		okButton.setOnAction(ActionEvent -> {
			String newName = nameField.getText();
			if (!oldName.equals(newName)) {
				if (fat.hasName(location, newName)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("此位置已包含同名文件/文件夹");
					alert.showAndWait();
				} else {
					if (block.getObject() instanceof Folder) {
						Folder thisFolder = (Folder)block.getObject();
						thisFolder.setFolderName(newName);
						pathMap.get(thisFolder.getPath()).setValue(newName);
						reLoc(location, location, oldName, newName, thisFolder);
					} else {
						((FileModel)block.getObject()).setFileName(newName);
					}
					icon.setText(newName);
				}
			}
			if (block.getObject() instanceof FileModel) {
				FileModel thisFile = ((FileModel)block.getObject());
				int newFlag = toggleGroup.getSelectedToggle().getUserData().hashCode();
				thisFile.setFlag(newFlag);
			}
			dialogStage.close();
		});
	}
	
	private void reLoc(String oldP, String newP, String oldN, String newN, Folder folder) {
		String oldLoc = oldP + "\\" + oldN;
		String newLoc = newP + "\\" + newN;
		Path oldPath = fat.getPath(oldLoc);
		fat.replacePath(oldPath, newLoc);
		for (Object child : folder.getChildren()) {
			if (child instanceof FileModel) {
				((FileModel) child).setLocation(newLoc);
			} else {
				Folder nextFolder = (Folder)child;
				nextFolder.setLocation(newLoc);		
				if (nextFolder.hasChild()) {
					reLoc(oldLoc, newLoc, nextFolder.getFolderName(),
							nextFolder.getFolderName(), nextFolder);
				}
				else {
					Path nextPath = fat.getPath(oldLoc + "\\" +
							nextFolder.getFolderName());
					String newNext = newLoc + "\\" + nextFolder.getFolderName();
					fat.replacePath(nextPath, newNext);
				}
			}
		}
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}
