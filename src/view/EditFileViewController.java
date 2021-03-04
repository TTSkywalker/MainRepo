package view;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.DiskBlock;
import model.FAT;
import model.FileModel;
import model.Folder;
import tools.DiskModel;

public class EditFileViewController {

    private FileModel file;
    private FAT fat;
    private DiskBlock block;
    private String newContent, oldContent;
    private Stage stage;
    @FXML
    public BorderPane borderPane;
    @FXML
    public TextArea contentField;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu fileMenu;
    @FXML
    public MenuItem closeItem;
    @FXML
    public MenuItem saveItem;

    public void init(FileModel file, FAT fat, DiskBlock block) {
        this.file = file;
        this.fat = fat;
        this.block = block;
        showView();
    }
    //编辑文件时显示的对话框，在这里实现对按钮功能的监听和实现
    private void showView() {
        contentField.setPrefRowCount(25);
        contentField.setWrapText(true);
        contentField.setText(file.getContent());
        if (file.getFlag() == DiskModel.FLAGREAD) {
            contentField.setDisable(true);
        }

        saveItem.setGraphic(new ImageView(DiskModel.SAVE_IMG));
        saveItem.setOnAction(ActionEvent -> {
            newContent = contentField.getText();
            oldContent = file.getContent();
            if (newContent == null) {
                newContent = "";
            }
            if (!newContent.equals(oldContent)) {
                saveContent(newContent);
            }
        });

        closeItem.setGraphic(new ImageView(DiskModel.CLOSE_IMG));
        closeItem.setOnAction(ActionEvent -> onClose(ActionEvent));

        menuBar.setPadding(new Insets(0));

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                onClose(event);
            }
        });
    }

    private void onClose(Event event) {
        newContent = contentField.getText();
        oldContent = file.getContent();
        boolean isCancel = false;
        if (newContent == null) {
            newContent = "";
        }
        if (!newContent.equals(oldContent)) {
            event.consume();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("保存更改");
            alert.setHeaderText(null);
            alert.setContentText("文件内容已更改，是否保存?");
            ButtonType saveType = new ButtonType("保存");
            ButtonType noType = new ButtonType("不保存");
            ButtonType cancelType = new ButtonType("取消", ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(saveType, noType, cancelType);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == saveType) {
                saveContent(newContent);
            } else if (result.get() == cancelType) {
                isCancel = true;
            }
        }
        if (!isCancel) {
            fat.removeOpenedFile(block);
            stage.close();
        }
    }
    private int countInstructions(String newContent){
        int sum = 0;
        for(int i = 0; i < newContent.length(); i++){
            if(newContent.charAt(i) == 'e'){
                if(newContent.charAt(i+1) == 'n' && newContent.charAt(i+2) == 'd'){
                    break;
                }
            }
            if(newContent.charAt(i) == '\n')  sum++;
        }
        return sum;
    }
    private void saveContent(String newContent) {
        int newLength = 0;
        if(file.getSuffix().equals("txt")){
            newLength = newContent.length();
        }else{
            newLength = countInstructions(newContent);
        }
        int blockCount = DiskModel.blocksCount(newLength);
        file.setLength(blockCount);
        file.setContent(newContent);
        file.setSize(DiskModel.getSize(newLength));
        if (file.hasParent()) {
            Folder parent = (Folder) file.getParent();
            parent.setSize(DiskModel.getFolderSize(parent));
            while (parent.hasParent()) {
                parent = (Folder) parent.getParent();
                parent.setSize(DiskModel.getFolderSize(parent));
            }
        }
        fat.reallocBlocks(blockCount, block);
    }

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }
}
