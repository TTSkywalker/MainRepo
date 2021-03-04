package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.DiskBlock;
import model.FAT;
import model.FileModel;
import model.Path;
import tools.DiskModel;
import tools.ProcessGlobal;


import java.io.IOException;
import java.util.Map;

import static tools.ProcessGlobal.showProcessViewStage;

public class MainAppController extends Application {
    public Stage primaryStage;
    //FileTreeViewController fileTreeViewController = new FileTreeViewController();

    public void showRenameView(DiskBlock block, FAT fat, Label icon, Map<Path, TreeItem<String>> pathMap){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("RenameView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Scene scene = new Scene(page);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("重命名");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.getIcons().add(new Image("file:picture/timg6.png"));

            RenameViewController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.init(block,fat,icon,pathMap);

            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showEditFileViewView(FileModel file, FAT fat, DiskBlock block){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("EditFileView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Scene scene = new Scene(page);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(file.getFileName());
            stage.initOwner(primaryStage);
            stage.getIcons().add(new Image("file:picture/timg6.png"));

            EditFileViewController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.init(file,fat,block);

            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showPropertyView(DiskBlock block, FAT fat, Label icon, Map<Path, TreeItem<String>> pathMap){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("PropertyView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Scene scene = new Scene(page);
            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);
            dialogStage.setTitle("查看属性");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(new Image("file:picture/timg6.png"));

            PropertyViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.init(block,fat,icon, pathMap);

            dialogStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showCmdView(FileTreeViewController fileTreeViewController){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CmdView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Scene scene = new Scene(page);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cmd");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.getIcons().add(new Image("file:picture/timg6.png"));

            CmdViewController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.cmdinit(fileTreeViewController);

            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainAppController.class.getResource("MainApp.fxml"));
            TabPane root = (TabPane) loader.load();
            Scene s = new Scene(root);

            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("OS");
            this.primaryStage.setScene(s);
            this.primaryStage.show();

            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(getClass().getResource("ProcessView.fxml"));
            showProcessViewStage = (AnchorPane) loader1.load();
            Tab changeToProcessView = new Tab("调度系统", showProcessViewStage);
            ProcessViewController controller1 = loader1.getController();
            controller1.setMainApp(this);

            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(getClass().getResource("FileTreeView.fxml"));
            AnchorPane showTreeViewStage = (AnchorPane) loader2.load();
            Tab changeToTreeView  = new Tab("文件系统",showTreeViewStage);
            FileTreeViewController controller2 = loader2.getController();
            controller2.setMainApp(this);
            controller2.init();

            FXMLLoader loader3 = new FXMLLoader();
            loader3.setLocation(getClass().getResource("DirectionView.fxml"));
            AnchorPane showDirectionViewStage = (AnchorPane) loader3.load();
            Tab changeToDirectionView  = new Tab("使用说明",showDirectionViewStage);
            DirectionViewController controller3 = loader3.getController();
            controller3.setMainApp(this);

            root.getTabs().add(changeToProcessView);
            root.getTabs().add(changeToTreeView);
            root.getTabs().add(changeToDirectionView);

        }catch(IOException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
