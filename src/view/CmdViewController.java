package view;


import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CmdViewController{
    private Stage dialogStage;
    private MainAppController mainApp;
    @FXML
    public TextArea textArea;

    FileTreeViewController fileTreeViewController;

    Scanner input = new Scanner(System.in);
    private int i = 0;

    public  void setMainApp(MainAppController mainApp){
        this.mainApp=mainApp;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


   public void cmdinit(FileTreeViewController fileTreeViewController1){

       fileTreeViewController = fileTreeViewController1;

        textArea.appendText(">> ");

        textArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");

        // After TextArea ready, highlighting a text.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.selectRange(6, 9);
            }
        });
        //监听回车
        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (event.isShiftDown()) {
                    textArea.appendText(System.getProperty("line.separator"));
                } else {
                        String[] str = textArea.getText().split("\n");
                        String string = str[str.length - 1];
                        if (!textArea.getText().isEmpty() && string != "收到！" && string != null) {
                            // sendFunction();
                            String[] strs = string.split("\\s+");
                           /* if (strs[1].equals("createTxt")) {
                                //新建文件
                                String[] filetypes = strs[2].split("\\.");
                                if (filetypes[filetypes.length - 1].equals("txt")) {
                                    // textArea.appendText("\n是txt");
                                    //  textArea.appendText("\n"+strs[2]);
                                    fileTreeViewController.cmdToCreateTxt(strs[2]);
                                } else if (filetypes[filetypes.length - 1].equals("e")) {
                                    fileTreeViewController.cmdToCreateE(strs[2]);
                                }
                                // textArea.appendText("\n收到create！");
                            }*/
                            if (strs.length >= 2) {
                                if (strs[1].equals("createTxt")) {
                                    fileTreeViewController.cmdToCreateTxt(strs[2]);
                                } else if (strs[1].equals("createE")) {
                                    fileTreeViewController.cmdToCreateE(strs[2]);
                                } else if (strs[1].equals("delete")) {
                                    //textArea.appendText("\n收到delete！");
                                    fileTreeViewController.deleteFile(strs[2]);
                                    //textArea.appendText("\n收到路径："+strs[2]);
                                }
                       /* else if(strs[1].equals("type")){
                            textArea.appendText("\n收到type！");//显示
                        }*/
                                else if (strs[1].equals("copy")) {
                                    //textArea.appendText("\n收到copy！");
                                    fileTreeViewController.cmdToCopyFile(strs[2]);
                                }
                                else if (strs[1].equals("mkdir")) {
                                    //textArea.appendText("\n收到mkdir！");//建立目录
                                    fileTreeViewController.createFolder(strs[2]);
                                }
                                else{
                                    textArea.appendText("\n输入有误，请重新输入。");
                                }
                                textArea.appendText("\n>> ");
                        /*else if(strs[1].equals("rmdir")){
                            textArea.appendText("\n收到rmdir！");//删除空目录
                        }*/
                                // textArea.appendText("\n收到！\n>> ");
                            }
                        }
                }
            }
        });

   }

}