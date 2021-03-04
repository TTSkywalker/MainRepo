package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import model.*;
import tools.DiskModel;
import tools.ProcessGlobal;

import java.io.*;
import java.util.*;

public class FileTreeViewController {
    final static String FILE_RANDOM_RUN = "随机打开";
    final static String FILE_RANDOM_STOP = "停止打开";
    final static String NO_EFFECTIVE_INS = "文件不含有效指令，创建失败\n";
    @FXML
    public Label Name;
    @FXML
    public Button importButton;//导入按钮
    @FXML
    public Button outButton;//导入按钮
    @FXML
    public Button backButton;//后退按钮
    @FXML
    public Button gotoButton;//跳转按钮
    @FXML
    public Label curFolder;
    @FXML
    public TextField locField;
    @FXML
    public TreeView<String> fileTree;//左侧的目录树
    @FXML
    public FlowPane flowPane;//中心桌面
    @FXML
    public TableView<DiskBlock> blockTable;//磁盘块分配表
    @FXML
    public TableView<FileModel> openedTable;//已打开文件表
    @FXML
    public HBox locBox;
    @FXML
    public TableColumn noCol;
    @FXML
    public TableColumn indexCol;
    @FXML
    public TableColumn typeCol;
    @FXML
    public TableColumn objCol;
    @FXML
    public TableColumn nameCol;
    @FXML
    public TableColumn flagCol;
    @FXML
    public TableColumn diskCol;
    @FXML
    public TableColumn suffixCol;
    @FXML
    public TableColumn pathCol;
    @FXML
    public TableColumn lengthCol;
    @FXML
    public PieChart pieChart;//磁盘分配饼图
    @FXML
    public Label useMsg;
    public Label[] icons;
    private MainAppController mainApp;
    @FXML
    private Button randomRunButton;
    private ObservableList<DiskBlock> dataBlock;
    private ObservableList<FileModel> dataOpened;
    private ContextMenu contextMenu, contextMenu2; //在桌面右键时的显示的菜单栏
    private MenuItem createTxTFileItem, createEFileItem, createFolderItem, openItem, renameItem, delItem, propItem, runItem, copyFile;
    private FAT fat = new FAT();
    private int index;
    private List<DiskBlock> blockList;
    ;
    private String recentPath = "C:";
    private TreeItem<String> rootNode, recentNode;
    private Map<Path, TreeItem<String>> pathMap = new HashMap<Path, TreeItem<String>>();

    //更新饼状图
    private void updataPieChart() {
        importButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("导入按钮：" +
                        "从data.dat文件中导入10个可执行文件，其中8个可执行文件为正确文件，第九、第十个文件内容为错误示例，仅供参考;");
            }
        });
        importButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
        outButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("导出按钮：" +
                        "将该目录下的全部文件导出到data.dat文件中保存，该操作不会保存目录;");
            }
        });
        outButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
        pieChart.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("磁盘分配饼图：直观的显示已分配磁盘块和未分配磁盘块的比例;");
            }
        });
        pieChart.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
        pieChart.setData(getChartData());
    }

    private ObservableList<PieChart.Data> getChartData() {
        ObservableList<PieChart.Data> answer = FXCollections.observableArrayList();
        answer.addAll(new PieChart.Data("已使用盘块数", fat.usedBlocksCount()),
                new PieChart.Data("待使用盘块数", 256 - fat.usedBlocksCount()));
        return answer;
    }

    public void setMainApp(MainAppController mainApp) {
        this.mainApp = mainApp;
    }

    //初始化右键时的桌面菜单
    private void initContextMenu() {
        createTxTFileItem = new MenuItem("新建txt文件");
        createEFileItem = new MenuItem("新建可执行文件");
        createFolderItem = new MenuItem("新建文件夹");

        openItem = new MenuItem("打开");
        runItem = new MenuItem("运行");
        delItem = new MenuItem("删除");
        renameItem = new MenuItem("重命名");
        propItem = new MenuItem("属性");
        copyFile = new MenuItem("复制");


        contextMenu = new ContextMenu(createTxTFileItem, createEFileItem, createFolderItem);
        contextMenu2 = new ContextMenu(openItem, runItem, delItem, renameItem, propItem, copyFile);
    }

    //新建TXT文件的方法
    public void createTxt(String inputPath) {
        String cteatePath;
        if (inputPath == null) {
            cteatePath = recentPath;
        } else {
            cteatePath = inputPath;
            Object obj = fat.getFolder(cteatePath);
            if (obj == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("输入路径错误！");
                alert.showAndWait();
            }
        }
        int no = fat.createTxtFile(cteatePath);
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(cteatePath), cteatePath);
            updataPieChart();
        }
    }

    //新建可执行文件的方法
    public void createE(String inputPath) {
        String cteatePath;
        if (inputPath == null) {
            cteatePath = recentPath;
        } else {
            cteatePath = inputPath;
            Object obj = fat.getFolder(cteatePath);
            if (obj == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("输入路径有误！");
                alert.showAndWait();
            }
        }
        int no = fat.createEFile(cteatePath);
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(cteatePath), cteatePath);
            updataPieChart();
        }
    }

    @FXML
    private void saveFile() throws IOException, ClassNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream("others/data.dat");
        blockList = fat.getBlockList("C:");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeInt(blockList.size());
            for (DiskBlock db : blockList) {
                if (db.getObject() instanceof Folder) continue;
                objectOutputStream.writeObject((FileModel) db.getObject());
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("导出成功！");
            alert.showAndWait();
        }
    }

    @FXML
    private void importFile() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("others/data.dat");

        try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            int trainCount = objectInputStream.readInt();
            for (int i = 0; i < trainCount; i++) {
                FileModel file = (FileModel) objectInputStream.readObject();
                int no;
                if (file.getSuffix().endsWith("txt")) {
                    no = fat.createTxtFile(fat.getFolderByPath("C:"), file, fat);
                } else {
                    no = fat.createEFile(fat.getFolderByPath("C:"), file, fat);
                }
                if (no == DiskModel.ERROR) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("磁盘容量已满，无法创建");
                    alert.showAndWait();
                } else {
                    flowPane.getChildren().removeAll(flowPane.getChildren());
                    addIcon(fat.getBlockList(fat.getFolderByPath("C:")), fat.getFolderByPath("C:"));
                    updataPieChart();
                }
            }
        }
    }

    public void createFolder(String inputPath) {
        String cteatePath;
        if (inputPath == null) {
            cteatePath = recentPath;
        } else {
            cteatePath = inputPath;
            Object obj = fat.getFolder(cteatePath);
            if (obj == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("输入路径错误！");
                alert.showAndWait();
            }
        }
        int no = fat.createFolder(cteatePath);
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            Folder newFolder = (Folder) fat.getBlock(no).getObject();
            Path newPath = newFolder.getPath();
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(cteatePath), cteatePath);
            addNode(pathMap.get(newFolder.getParent().getPath()), newPath);
            updataPieChart();
        }
    }

    public void deleteFile(String path){
        Object obj = fat.getFileByPath(path);
        if(obj== null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("文件不存在");
            alert.showAndWait();
        }else{
            FileModel file = (FileModel)obj;
            if(file.isOpened()){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("文件未关闭");
                alert.showAndWait();
            }else{
                delFileView(fat.getDiskBlocks()[file.getDiskNum()], fat, this);
                flowPane.getChildren().removeAll(flowPane.getChildren());
                addIcon(fat.getBlockList(fat.getFolderByPath(path)), fat.getFolderByPath(path));
                updataPieChart();
            }
        }
    }

    public void deleteIt() {
        DiskBlock thisBlock = blockList.get(index);
        if (fat.isOpenedFile(thisBlock)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("文件未关闭");
            alert.showAndWait();
        } else {
            delFileView(thisBlock, fat, this);
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(recentPath), recentPath);
            updataPieChart();
        }
    }

    public void copyFile(String path) {
        Object obj = fat.getFileByPath(path);
        if (obj == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("文件不存在");
            alert.showAndWait();
        } else {
            FileModel file = (FileModel) obj;
            int no;
            if (file.getSuffix().endsWith("txt")) {
                no = fat.createTxtFile(fat.getFolderByPath(path), file, fat);
            } else {
                no = fat.createEFile(fat.getFolderByPath(path), file, fat);
            }
            if (no == DiskModel.ERROR) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("磁盘容量已满，无法创建");
                alert.showAndWait();
            } else {
                flowPane.getChildren().removeAll(flowPane.getChildren());
                addIcon(fat.getBlockList(fat.getFolderByPath(path)), fat.getFolderByPath(path));
                updataPieChart();
            }
        }
    }

    //复制文件
    public void copyFile() {
        DiskBlock thisBlock = blockList.get(index);
        FileModel thisFile = (FileModel) thisBlock.getObject();
        int no;
        if (thisFile.getSuffix().endsWith("txt")) {
            no = fat.createTxtFile(recentPath, thisFile, fat);
        } else {
            no = fat.createEFile(recentPath, thisFile, fat);
        }
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(recentPath), recentPath);
            updataPieChart();
        }
    }

    //设置桌面的监听功能
    private void menuItemSetOnAction() {
        flowPane.setStyle("-fx-background-image: url('file:picture/background.png')");

        //右键桌面空白处显示新建文件和目录的菜单
        flowPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY && !contextMenu2.isShowing()) {
                contextMenu.show(flowPane, me.getScreenX(), me.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
        flowPane.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("模拟桌面：\n" +
                        "右键模拟桌面的空白部分，可以新建文件或者文件夹;\n" +
                        "右键新建的文件夹，可以执行打开、删除、重命名、属性对应的操作;\n" +
                        "右键新建的文件，可以执行打开、删除、重命名、属性、复制对应的操作，可执行文件还可以选择运行操作;\n");
            }
        });
        flowPane.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
        //复制文件
        copyFile.setOnAction((ActionEvent t) -> {
            copyFile();
        });
        //新建txt文件
        createTxTFileItem.setOnAction(ActionEvent -> {
            createTxt(null);
        });
        //新建可执行文件
        createEFileItem.setOnAction(ActionEvent -> {
            createE(null);
        });
        //创建文件夹
        createFolderItem.setOnAction(ActionEvent -> {
            createFolder(null);
        });
        //打开文件或者文件夹
        openItem.setOnAction(ActionEvent -> onOpen());
        //可执行文件独有的运行选项，onRun函数未实现
        runItem.setOnAction(ActionEvent -> onRun());
        //删除文件（夹）
        delItem.setOnAction(ActionEvent -> {
            deleteIt();
        });
        //文件（夹）重命名
        renameItem.setOnAction(ActionEvent -> {
            DiskBlock thisBlock = blockList.get(index);
            mainApp.showRenameView(thisBlock, fat, icons[index], pathMap);
        });
        //查看文件（夹）的各项属性
        propItem.setOnAction(ActionEvent -> {
            DiskBlock thisBlock = blockList.get(index);
            mainApp.showPropertyView(thisBlock, fat, icons[index], pathMap);
        });

    }

    //点击删除文件或者文件夹时弹出的对话框
    private void delFileView(DiskBlock block, FAT fat, FileTreeViewController mainView) {
        Alert mainAlert, okAlert, errAlert;
        String mesg = "";
        if (block.getObject() instanceof Folder) {
            Folder folder = (Folder) block.getObject();
            mesg = folder.getFolderName()
                    + "\n类型: " + folder.getType()
                    + "\n大小: " + folder.getSize()
                    + "\n创建时间: " + folder.getCreateTime();
        } else {
            FileModel file = (FileModel) block.getObject();
            mesg = file.getFileName()
                    + "\n类型: " + file.getType()
                    + "\n大小: " + file.getSize() + "KB"
                    + "\n创建时间: " + file.getCreateTime();
        }
        mainAlert = new Alert(AlertType.CONFIRMATION);
        mainAlert.setHeaderText("确认删除");
        mainAlert.setContentText(mesg);

        okAlert = new Alert(AlertType.INFORMATION);
        okAlert.setTitle("成功");
        okAlert.setHeaderText(null);

        errAlert = new Alert(AlertType.ERROR);
        errAlert.setHeaderText(null);

        Optional<ButtonType> result = mainAlert.showAndWait();
        Path thisPath = null;
        if (result.get() == ButtonType.OK) {
            if (block.getObject() instanceof Folder) {
                thisPath = ((Folder) block.getObject()).getPath();
            }
            int res = fat.delete(block);
            if (res == 0) {//删除文件夹成功
                mainView.removeNode(mainView.getRecentNode(), thisPath);
                okAlert.setContentText("删除文件夹成功");
                okAlert.show();
            } else if (res == 1) {
                okAlert.setContentText("删除文件成功");
                okAlert.show();
            } else {//文件未关闭
                errAlert.setHeaderText("文件未关闭");
                errAlert.show();
            }
        } else {

        }
    }

    //初始化目录树
    private void initFileTree() {
        rootNode = new TreeItem<>("C:", new ImageView(new Image(DiskModel.DISK_IMG)));
        rootNode.setExpanded(true);

        recentNode = rootNode;
        pathMap.put(fat.getPath("C:"), rootNode);

        fileTree.setRoot(rootNode);
        fileTree.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());
        fileTree.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");
        fileTree.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("目录树：点击树上的某个节点，进入对应的目录。");
            }
        });
        fileTree.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });

        for (Path path : fat.getPaths()) {
            System.out.println(path);
            if (path.hasParent() && path.getParent().getPathName().equals(rootNode.getValue())) {
                initTreeNode(path, rootNode);
            }
        }
        addIcon(fat.getBlockList(recentPath), recentPath);
    }

    //初始化最上层的文件路径显示及跳转
    private void initTopBox() {
        curFolder.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 16px");

        locField.setPrefWidth(400);
        //后退按钮，返回上层目录
        backButton.setOnAction(ActionEvent -> {
            Path backPath = fat.getPath(recentPath).getParent();
            if (backPath != null) {
                List<DiskBlock> blocks = fat.getBlockList(backPath.getPathName());
                flowPane.getChildren().removeAll(flowPane.getChildren());
                addIcon(blocks, backPath.getPathName());
                recentPath = backPath.getPathName();
                recentNode = pathMap.get(backPath);
                locField.setText(recentPath);
            }
        });
        backButton.setGraphic(new ImageView(DiskModel.BACK_IMG));
        backButton.setStyle("-fx-background-color: #ffffff;");
        backButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                backButton.setStyle("-fx-background-color: #1e90ff;");
            }
        });
        backButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                backButton.setStyle("-fx-background-color: #ffffff;");
            }
        });
        //前进按钮，检索textfield的内容，若合法则跳转之。
        gotoButton.setOnAction(ActionEvent -> {
            String textPath = locField.getText();
            Path gotoPath = fat.getPath(textPath);
            if (gotoPath != null) {
                List<DiskBlock> blocks = fat.getBlockList(textPath);
                flowPane.getChildren().removeAll(flowPane.getChildren());
                addIcon(blocks, textPath);
                recentPath = textPath;
                recentNode = pathMap.get(gotoPath);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("目录不存在");
                alert.setHeaderText(null);
                alert.show();
                locField.setText(recentPath);
            }
        });
        gotoButton.setGraphic(new ImageView(DiskModel.FORWARD_IMG));
        gotoButton.setStyle("-fx-background-color: #ffffff;");
        gotoButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                gotoButton.setStyle("-fx-background-color: #1e90ff;");
            }
        });
        gotoButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                gotoButton.setStyle("-fx-background-color: #ffffff;");
            }
        });

        locBox.setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");
        locBox.setSpacing(10);
        locBox.setPadding(new Insets(5, 5, 5, 5));
        locBox.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("快捷访问：\n" +
                        "左按钮为返回上级目录,右按钮为跳转到输入的指定目录;\n" +
                        "中间的输入框输入路径,输入的路径以某个已存在的目录名结尾;\n" +
                        "如果输入cmd，则打开命令行窗口;");
            }
        });
        locBox.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
    }

    //初始化两个表
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initTables() {

        blockTable
                .setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");
        blockTable.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("磁盘分配表：\n" +
                        "直观地显示每个磁盘块的分配状态，包括盘块号、值、类型、内容;\n" +
                        "值：0表示磁盘未被占用，256表示该磁盘为某个文件到此结束并独占该磁盘，其他数字表示该文件未结束并指向下一个盘块号;\n" +
                        "类型表示该文件被磁盘|目录|文件占用，内容显示该磁盘块存储的文件名;");
            }
        });
        blockTable.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
        openedTable
                .setStyle("-fx-background-color: #ffffff;" + "-fx-border-color: #d3d3d3;" + "-fx-border-width:0.5px;");
        openedTable.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("已打开文件表：直观地显示每一个已经打开的文件的各项属性;");
            }
        });
        openedTable.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                useMsg.setText("使用说明");
            }
        });
        dataBlock = FXCollections.observableArrayList(fat.getDiskBlocks());
        dataOpened = fat.getOpenedFiles();


        noCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("noP"));
        noCol.setSortable(false);
        noCol.setResizable(false);

        indexCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("indexP"));
        indexCol.setSortable(false);
        indexCol.setResizable(false);

        typeCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("typeP"));
        typeCol.setSortable(false);
        typeCol.setResizable(false);

        objCol.setCellValueFactory(new PropertyValueFactory<DiskBlock, String>("objectP"));
        objCol.setSortable(false);
        objCol.setResizable(false);

        nameCol.setCellValueFactory(new PropertyValueFactory<FileModel, String>("fileNameP"));
        nameCol.setSortable(false);
        nameCol.setResizable(false);

        flagCol.setCellValueFactory(new PropertyValueFactory<FileModel, String>("flagP"));
        flagCol.setSortable(false);
        flagCol.setResizable(false);

        diskCol.setCellValueFactory(new PropertyValueFactory<FileModel, String>("diskNumP"));
        diskCol.setSortable(false);
        diskCol.setResizable(false);

        suffixCol.setCellValueFactory(new PropertyValueFactory<FileModel, String>("suffixP"));
        suffixCol.setSortable(false);
        suffixCol.setResizable(false);

        pathCol.setCellValueFactory(new PropertyValueFactory<FileModel, String>("locationP"));
        pathCol.setSortable(false);
        pathCol.setResizable(false);

        lengthCol.setCellValueFactory(new PropertyValueFactory<FileModel, String>("lengthP"));
        lengthCol.setSortable(false);
        lengthCol.setResizable(false);

        blockTable.setItems(dataBlock);
        blockTable.setEditable(false);

        openedTable.setItems(dataOpened);
    }

    //根据路径初始化目录树的节点
    private void initTreeNode(Path newPath, TreeItem<String> parentNode) {
        TreeItem<String> newNode = addNode(parentNode, newPath);
        if (newPath.hasChild()) {
            for (Path child : newPath.getChildren()) {
                initTreeNode(child, newNode);
            }
        }
    }

    //添加节点
    private TreeItem<String> addNode(TreeItem<String> parentNode, Path newPath) {
        String pathName = newPath.getPathName();
        String value = pathName.substring(pathName.lastIndexOf('\\') + 1);
        TreeItem<String> newNode = new TreeItem<String>(value, new ImageView(DiskModel.TREE_NODE_IMG));
        newNode.setExpanded(true);
        pathMap.put(newPath, newNode);
        parentNode.getChildren().add(newNode);
        return newNode;
    }

    //将label作为桌面图标加入到桌面flowpane中
    private void addIcon(List<DiskBlock> bList, String path) {
        blockList = bList;
        int n = bList.size();
        icons = new Label[n];
        for (int i = 0; i < n; i++) {
            if (bList.get(i).getObject() instanceof Folder) {
                icons[i] = new Label(((Folder) bList.get(i).getObject()).getFolderName());
                icons[i].setGraphic(new ImageView(new Image(DiskModel.FOLDER_IMG)));
            } else {
                icons[i] = new Label(((FileModel) bList.get(i).getObject()).getFileName() + "." + ((FileModel) bList.get(i).getObject()).getSuffix());
                if (((FileModel) bList.get(i).getObject()).getSuffix().equals("txt")) {
                    icons[i].setGraphic(new ImageView(new Image(DiskModel.TXTFILE_IMG)));
                } else {
                    icons[i].setGraphic(new ImageView(new Image(DiskModel.EXEFILE_IMG)));

                }
            }
            icons[i].setContentDisplay(ContentDisplay.TOP);
            icons[i].setWrapText(true);
            flowPane.getChildren().add(icons[i]);
            icons[i].setOnMouseEntered(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    ((Label) event.getSource()).setStyle("-fx-background-color: #f0f8ff;");
                }
            });
            icons[i].setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    ((Label) event.getSource()).setStyle("-fx-background-color: #ffffff;");
                }
            });
            //右键文件时显示菜单栏
            icons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Label src = (Label) event.getSource();
                    for (int j = 0; j < n; j++) {
                        if (src == icons[j]) {
                            index = j;
                        }
                    }
                    if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                        if (src.getText().endsWith("txt")) {
                            runItem.setVisible(false);
                            copyFile.setVisible(true);
                        } else if (src.getText().endsWith("e")) {
                            copyFile.setVisible(true);
                            runItem.setVisible(true);
                        } else {
                            copyFile.setVisible(false);
                        }
                        contextMenu2.show(src, event.getScreenX(), event.getScreenY());
                    } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        onOpen();
                    } else {
                        contextMenu2.hide();
                    }
                }
            });
        }
    }

    //打开文件（夹）：三种情况，打开文件夹，打开txt文件，打开可执行文件。其中打开后两者为编辑的效果。
    private void onOpen() {
        DiskBlock thisBlock = blockList.get(index);

        if (thisBlock.getObject() instanceof FileModel) {
            if (fat.getOpenedFiles().size() < 5) {
                if (fat.isOpenedFile(thisBlock)) {
                    Alert duplicate = new Alert(AlertType.ERROR, "文件已打开");
                    duplicate.showAndWait();
                } else {
                    fat.addOpenedFile(thisBlock);
                    mainApp.showEditFileViewView((FileModel) thisBlock.getObject(), fat, thisBlock);
                }
            } else {
                Alert exceed = new Alert(AlertType.ERROR, "文件打开已到上限");
                exceed.showAndWait();
            }
        } else {
            Folder thisFolder = (Folder) thisBlock.getObject();
            String newPath = thisFolder.getLocation() + "\\" + thisFolder.getFolderName();
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(newPath), newPath);
            locField.setText(newPath);
            recentPath = newPath;
            recentNode = pathMap.get(thisFolder.getPath());
        }
    }

    public void removeNode(TreeItem<String> recentNode, Path remPath) {
        recentNode.getChildren().remove(pathMap.get(remPath));
        pathMap.remove(remPath);
    }

    public TreeItem<String> getRecentNode() {
        return recentNode;
    }

    public void setRecentNode(TreeItem<String> recentNode) {
        this.recentNode = recentNode;
    }

    public void listenCmd() {
        locField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // do what is to do
                if (locField.getText().equals("cmd"))
                    mainApp.showCmdView(this);
            }
        });
    }

    //cmd新建TXT文件的方法
    public void cmdToCreateTxt(String path) {
        int no = fat.createTxtFile(path);
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(recentPath), path);
            updataPieChart();
        }
    }

    //cmd新建可执行文件的方法
    public void cmdToCreateE(String path) {
        int no = fat.createEFile(path);
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(recentPath), path);
            updataPieChart();
        }
    }

    public void cmdToDeleteIt(String path) {//TODO
        DiskBlock thisBlock = blockList.get(index);
        if (fat.isOpenedFile(thisBlock)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("文件未关闭");
            alert.showAndWait();
        } else {
            delFileView(thisBlock, fat, this);
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(path), path);
        }
    }

    //复制文件
    public void cmdToCopyFile(String path) {
        DiskBlock thisBlock = blockList.get(index);
        FileModel thisFile = (FileModel) thisBlock.getObject();
        int no;
        if (thisFile.getSuffix().endsWith("txt")) {
            no = fat.createTxtFile(path, thisFile, fat);
        } else {
            no = fat.createEFile(path, thisFile, fat);
        }
        if (no == DiskModel.ERROR) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("磁盘容量已满，无法创建");
            alert.showAndWait();
        } else {
            flowPane.getChildren().removeAll(flowPane.getChildren());
            addIcon(fat.getBlockList(path), path);
            updataPieChart();
        }
    }

    //初始化函数，在MainApp类那边由controller调用
    public void init() {
        initFileTree();
        initContextMenu();
        menuItemSetOnAction();
        initTopBox();
        initTables();
        updataPieChart();
        listenCmd();
        try {
            importFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onRun() {
        if (ProcessGlobal.getPcbnum() < ProcessGlobal.MAX_PCB_NUM) {
            DiskBlock db = blockList.get(index);
            if (db.getObject() instanceof FileModel) {
                int ins = -1;
                int op = -1;
                int deviceTime = -1;
                int ill = 0;//非法行
                String[] cont = ((FileModel) db.getObject()).getContent().split("\n");
                for (int i = 0; i < cont.length; i++) {
                    if (!((cont[i].indexOf('=') - cont[i].indexOf('x') == 1) ||
                            (cont[i].equals("x++")) || (cont[i].equals("x--")) || (cont[i].indexOf('!') == 0) || (cont[i].equals("end"))))
                        ++ill;//检查非法行
                }
                if (cont.length > ill) {
                    ProcessGlobal.setOpening(cont.length - ill);
                    for (int i = 0; i < cont.length; i++) {
                        ins = -1;
                        op = -1;
                        deviceTime = -1;
                        if (cont[i].indexOf('=') - cont[i].indexOf('x') == 1) {
                            ins = 0;
                            op = getOP(cont[i]);
                        } else if (cont[i].equals("x++"))
                            ins = 1;
                        else if (cont[i].equals("x--"))
                            ins = 2;
                        else if (cont[i].indexOf('!') == 0) {
                            ins = 3;
                            op = getOP(cont[i]);
                            deviceTime = getDeviceTime(cont[i]);
                        } else if (cont[i].equals("end"))
                            ins = 4;

                        if (ins == -1 || (ins == 3 && op < 0) || (ins == 3 && op > 2))
                            continue;
                        else if (ins == 0)
                            ProcessGlobal.loadIns(ins, op);
                        else if (ins == 3)
                            ProcessGlobal.loadIns(ins, op, deviceTime);
                        else
                            ProcessGlobal.loadIns(ins);
                    }

                    if (!ProcessGlobal.getRunning())
                        ProcessGlobal.changeRunning();
                } else
                    ProcessGlobal.setNotice(NO_EFFECTIVE_INS);
            }
        }
    }

    private int getOP(String s) {
        if (s.charAt(1) == 'A' || s.charAt(1) == 'a')
            return PCB.A_DEVICE;
        else if (s.charAt(1) == 'B' || s.charAt(1) == 'b')
            return PCB.B_DEVICE;
        else if (s.charAt(1) == 'C' || s.charAt(1) == 'c')
            return PCB.C_DEVICE;
        //↑设备指令|赋值指令↓
        String is = "";
        for (int i = 2; i < s.length(); i++)
            is += s.charAt(i);
        return Integer.parseInt(is);
    }

    private int getDeviceTime(String s) {
        int startPlace = s.lastIndexOf(' ');
        if (startPlace < 0) startPlace = 1;
        String is = "";
        for (int i = startPlace + 1; i < s.length(); i++)
            is += s.charAt(i);
        return Integer.parseInt(is);
    }

    @FXML
    private void fileRandomAction() {
        ProcessGlobal.changeFileRunningMode();
        if (ProcessGlobal.isFileRunningMode()) {
            randomRunButton.setText(FILE_RANDOM_STOP);
            ProcessGlobal.fileTimer = new Timer();
            fTimerTask ftmt = new fTimerTask();
            ProcessGlobal.fileTimer.schedule(ftmt, 0, 1000);
        } else {
            randomRunButton.setText(FILE_RANDOM_RUN);
            ProcessGlobal.fileTimer.cancel();
            ProcessGlobal.fileTimer = null;
        }
    }

    public final class TextFieldTreeCellImpl extends TreeCell<String> {

        private TextField textField;

        public TextFieldTreeCellImpl() {

            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        if (getTreeItem() != null) {
                            String pathName = null;
                            for (Map.Entry<Path, TreeItem<String>> entry : pathMap.entrySet()) {
                                if (getTreeItem() == entry.getValue()) {
                                    pathName = entry.getKey().getPathName();
                                    break;
                                }
                            }
                            List<DiskBlock> fats = fat.getBlockList(pathName);
                            flowPane.getChildren().removeAll(flowPane.getChildren());
                            addIcon(fats, pathName);
                            recentPath = pathName;
                            recentNode = getTreeItem();
                            locField.setText(recentPath);
                        }
                    }
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }

    class fTimerTask extends TimerTask {
        @Override
        public void run() {
            if (ProcessGlobal.isFileRunningMode()) {
                int savedIndex = index;
                index = (int) (Math.random() * (blockList.size() - 1));
                onRun();
                index = savedIndex;
            }
        }
    }
}
