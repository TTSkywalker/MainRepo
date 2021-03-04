package view;

import java.util.Map;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;

/**
 * @author Kit
 * @version: 2018年10月1日 下午3:42:21
 * 
 */
public class RenameViewController {

	private DiskBlock block;
	private FAT fat;
	private Label icon;
	private Map<Path, TreeItem<String>> pathMap;
	private Stage stage;
	private String oldName, location, suffix;

	@FXML
	public HBox hBox;
	@FXML
	public TextField nameField;
	@FXML
	public Button cancelButton;
	@FXML
	public Button okButton;

	public void init(DiskBlock block, FAT fat, Label icon, Map<Path, TreeItem<String>> pathMap) {
		this.block = block;
		this.fat = fat;
		this.icon = icon;
		this.pathMap = pathMap;
		showView();
	}

	private void showView() {
		if (block.getObject() instanceof Folder) {
			oldName = ((Folder) block.getObject()).getFolderName();
			suffix = "";
			location = ((Folder) block.getObject()).getLocation();
		} else {
			oldName = ((FileModel) block.getObject()).getFileName();
			suffix = "." + ((FileModel) block.getObject()).getSuffix();
			location = ((FileModel) block.getObject()).getLocation();
		}
		
		nameField.setText(oldName);

		okButton.setStyle("-fx-background-color:#d3d3d3;");
		okButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #ffffff;");
			}
		});
		okButton.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				okButton.setStyle("-fx-background-color: #d3d3d3;");
			}
		});
		okButton.setOnAction(ActionEvent -> {
			String newName = nameField.getText();
			if (!newName.equals(oldName)) {
				if (fat.hasName(location, newName)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("此位置已包含同名文件/文件夹");
					alert.showAndWait();
				} else {
					if (block.getObject() instanceof Folder) {
						Folder thisFolder = (Folder) block.getObject();
						thisFolder.setFolderName(newName);
						pathMap.get(thisFolder.getPath()).setValue(newName);
						reLoc(location, location, oldName, newName, thisFolder);
					} else {
						((FileModel) block.getObject()).setFileName(newName);
					}
					icon.setText(newName+suffix);
				}					
			}
			stage.close();
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
		cancelButton.setOnAction(ActionEvent -> stage.close());
		
		hBox.setPadding(new Insets(5));
		hBox.setStyle("-fx-background-color:#a9a9a9");

		stage.initStyle(StageStyle.UNDECORATED);
		stage.setAlwaysOnTop(true);
	}

	private void reLoc(String oldP, String newP, String oldN,
			String newN, Folder folder) {
		String oldLoc = oldP + "\\" + oldN;
		String newLoc = newP + "\\" + newN;
		Path oldPath = fat.getPath(oldLoc);
		fat.replacePath(oldPath, newLoc);
		for (Object child : folder.getChildren()) {
			if (child instanceof FileModel) {
				((FileModel) child).setLocation(newLoc);
			} else {
				Folder nextFolder = (Folder) child;
				nextFolder.setLocation(newLoc);
				if (nextFolder.hasChild()) {
					reLoc(oldLoc, newLoc, nextFolder.getFolderName(),
							nextFolder.getFolderName(), nextFolder);
				} else {
					Path nextPath = fat.getPath(oldLoc + "\\" +
							nextFolder.getFolderName());
					fat.replacePath(nextPath, newLoc + "\\" +
							nextFolder.getFolderName());
				}
			}
		}
	}

	public void setDialogStage(Stage stage) {
		this.stage = stage;
	}
}
