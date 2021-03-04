package view;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.PCB;
import tools.ProcessGlobal;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/*
指令：0赋值，1加一，2减一，3设备，4结束

往图形界面添加东西：使用ProcessGlobal.addThing(Node n)
往图形界面删除东西：使用ProcessGlobal.delThing(Node n)

可执行文件对应图形初始位置设定：ProcessGlobal.setExBirth(x,y)，初始位置已设为“可执行文件”标题下方
要求生成的图标点击即可读取指令生成进程createProcess(1)
最好是指定目录下定期扫描有无可执行文件，有就自动添加到界面
 */

public class ProcessViewController extends Observable {

    //按钮上的字
    final String RANDOM_CREATE_BUTTON_TEXT = "开始随机创建进程";
    final String RANDOM_CREATE_STOP_BUTTON_TEXT = "停止随机创建进程";
    final String STOP_BUTTON_TEXT = "停止处理进程";
    final String RUN_BUTTON_TEXT = "开始处理进程";

    //显示在控制台的字
    final String RANDOM_CREATE_CONSOLE_TEXT = "开始随机创建进程\n";
    final String RANDOM_CREATE_STOP_CONSOLE_TEXT = "现已不再随机创建进程\n";
    final String STOP_CONSOLE_TEXT = "暂停处理进程~\n";
    final String RUN_CONSOLE_TEXT = "开始处理进程~\n";
    final String DEAD_EMBRACED_SOLVING = "阻塞可能影响系统正常运行，正开始处理...\n";
    final String PROCESS_DONE = "当前所有进程已处理完毕\n";
    final String DEAD_EMBRACED_SOLVING_FAILURE = "尝试处理失败，请手动删除阻塞进程后继续\n";
    final String PROCESS_CREATE_SUCCESS = "进程创建成功~\n";
    final String PROCESS_CREATE_FAILURE = "创建失败！\n内存不足或进程已满10个\n";
    final String INDEX_INPUT_ERROR = "未输入正确的index哦\n";

    //界面的字
    final String INIT_STATUS_NOTHING = "无";

    @FXML
    private Text runningPID;
    @FXML
    private Text blocked0;
    @FXML
    private Text blocked1;
    @FXML
    private Text blocked2;
    @FXML
    private Text blocked3;
    @FXML
    private Text blocked4;
    @FXML
    private Text blockedReason0;
    @FXML
    private Text blockedReason1;
    @FXML
    private Text blockedReason2;
    @FXML
    private Text blockedReason3;
    @FXML
    private Text blockedReason4;
    @FXML
    private Text pid0;
    @FXML
    private Text pid1;
    @FXML
    private Text pid2;
    @FXML
    private Text pid3;
    @FXML
    private Text pid4;
    @FXML
    private Text pid5;
    @FXML
    private Text pid6;
    @FXML
    private Text pid7;
    @FXML
    private Text pid8;
    @FXML
    private Text pid9;
    @FXML
    private Text timePieceUI;
    @FXML
    private CheckBox autocreate;
    @FXML
    private CheckBox autoprocess;
    @FXML
    private RadioButton wayfcfs;
    @FXML
    private RadioButton wayspf;
    /*@FXML
    private RadioButton wayrr;*/
    @FXML
    private Text propro0;
    @FXML
    private Text propro1;
    @FXML
    private Text propro2;
    @FXML
    private Text propro3;
    @FXML
    private Text propro4;
    @FXML
    private Text propro5;
    @FXML
    private Text propro6;
    @FXML
    private Text propro7;
    @FXML
    private Text propro8;
    @FXML
    private Text propro9;
    @FXML
    private Text prosta0;
    @FXML
    private Text prosta1;
    @FXML
    private Text prosta2;
    @FXML
    private Text prosta3;
    @FXML
    private Text prosta4;
    @FXML
    private Text prosta5;
    @FXML
    private Text prosta6;
    @FXML
    private Text prosta7;
    @FXML
    private Text prosta8;
    @FXML
    private Text prosta9;
    @FXML
    private TextField delindex;
    @FXML
    private TextArea console;
    @FXML
    private Text instructionname;
    @FXML
    private Text xvar;
    @FXML
    private Rectangle ramarea;
    @FXML
    private RadioButton wayff;
    @FXML
    private RadioButton waybf;
    /*@FXML
    private RadioButton waywf;*/
    @FXML
    private Text systime;
    @FXML
    private Button randomgo;
    @FXML
    private Button runprocess;
    @FXML
    private Text statusa0;
    @FXML
    private Text statusa1;
    @FXML
    private Text statusb0;
    @FXML
    private Text statusb1;
    @FXML
    private Text statusb2;
    @FXML
    private Text statusc0;
    @FXML
    private Text statusc1;
    @FXML
    private Text statusc2;

    private MainAppController mainApp;

    public void setMainApp(MainAppController mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        ProcessGlobal.timer = new Timer();
        tTimerTask tmt = new tTimerTask();
        ProcessGlobal.timer.schedule(tmt, 0, ProcessGlobal.getPeriod());
        ProcessGlobal.initFreeram();
    }

    private void refreshNotice() {
        if (ProcessGlobal.hasNotice()) {
            console.appendText(ProcessGlobal.getNotice());
        }
    }

    private void refresh() {
        xvar.setText(ProcessGlobal.getX());
        instructionname.setText(ProcessGlobal.getIr());
        if (ProcessGlobal.getPcbnum() > 0 && ProcessGlobal.mypcb[0] != null) {
            propro0.setText(ProcessGlobal.mypcb[0].getSimProgress());
            propro0.setVisible(true);
            prosta0.setText(ProcessGlobal.mypcb[0].getStatusString());
            prosta0.setVisible(true);
            pid0.setText(String.valueOf(ProcessGlobal.mypcb[0].getPID()));
            pid0.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[0].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro0.setFill(Color.RED);
                prosta0.setFill(Color.RED);
                pid0.setFill(Color.RED);
            } else {
                propro0.setFill(Color.BLACK);
                prosta0.setFill(Color.BLACK);
                pid0.setFill(Color.BLACK);
            }
        } else {
            propro0.setVisible(false);
            prosta0.setVisible(false);
            pid0.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 1 && ProcessGlobal.mypcb[1] != null) {
            propro1.setText(ProcessGlobal.mypcb[1].getSimProgress());
            propro1.setVisible(true);
            prosta1.setText(ProcessGlobal.mypcb[1].getStatusString());
            prosta1.setVisible(true);
            pid1.setText(String.valueOf(ProcessGlobal.mypcb[1].getPID()));
            pid1.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[1].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro1.setFill(Color.RED);
                prosta1.setFill(Color.RED);
                pid1.setFill(Color.RED);
            } else {
                propro1.setFill(Color.BLACK);
                prosta1.setFill(Color.BLACK);
                pid1.setFill(Color.BLACK);
            }
        } else {
            propro1.setVisible(false);
            prosta1.setVisible(false);
            pid1.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 2 && ProcessGlobal.mypcb[2] != null) {
            propro2.setText(ProcessGlobal.mypcb[2].getSimProgress());
            propro2.setVisible(true);
            prosta2.setText(ProcessGlobal.mypcb[2].getStatusString());
            prosta2.setVisible(true);
            pid2.setText(String.valueOf(ProcessGlobal.mypcb[2].getPID()));
            pid2.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[2].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro2.setFill(Color.RED);
                prosta2.setFill(Color.RED);
                pid2.setFill(Color.RED);
            } else {
                propro2.setFill(Color.BLACK);
                prosta2.setFill(Color.BLACK);
                pid2.setFill(Color.BLACK);
            }
        } else {
            propro2.setVisible(false);
            prosta2.setVisible(false);
            pid2.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 3 && ProcessGlobal.mypcb[3] != null) {
            propro3.setText(ProcessGlobal.mypcb[3].getSimProgress());
            propro3.setVisible(true);
            prosta3.setText(ProcessGlobal.mypcb[3].getStatusString());
            prosta3.setVisible(true);
            pid3.setText(String.valueOf(ProcessGlobal.mypcb[3].getPID()));
            pid3.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[3].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro3.setFill(Color.RED);
                prosta3.setFill(Color.RED);
                pid3.setFill(Color.RED);
            } else {
                propro3.setFill(Color.BLACK);
                prosta3.setFill(Color.BLACK);
                pid3.setFill(Color.BLACK);
            }
        } else {
            propro3.setVisible(false);
            prosta3.setVisible(false);
            pid3.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 4 && ProcessGlobal.mypcb[4] != null) {
            propro4.setText(ProcessGlobal.mypcb[4].getSimProgress());
            propro4.setVisible(true);
            prosta4.setText(ProcessGlobal.mypcb[4].getStatusString());
            prosta4.setVisible(true);
            pid4.setText(String.valueOf(ProcessGlobal.mypcb[4].getPID()));
            pid4.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[4].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro4.setFill(Color.RED);
                prosta4.setFill(Color.RED);
                pid4.setFill(Color.RED);
            } else {
                propro4.setFill(Color.BLACK);
                prosta4.setFill(Color.BLACK);
                pid4.setFill(Color.BLACK);
            }
        } else {
            propro4.setVisible(false);
            prosta4.setVisible(false);
            pid4.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 5 && ProcessGlobal.mypcb[5] != null) {
            propro5.setText(ProcessGlobal.mypcb[5].getSimProgress());
            propro5.setVisible(true);
            prosta5.setText(ProcessGlobal.mypcb[5].getStatusString());
            prosta5.setVisible(true);
            pid5.setText(String.valueOf(ProcessGlobal.mypcb[5].getPID()));
            pid5.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[5].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro5.setFill(Color.RED);
                prosta5.setFill(Color.RED);
                pid5.setFill(Color.RED);
            } else {
                propro5.setFill(Color.BLACK);
                prosta5.setFill(Color.BLACK);
                pid5.setFill(Color.BLACK);
            }
        } else {
            propro5.setVisible(false);
            prosta5.setVisible(false);
            pid5.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 6 && ProcessGlobal.mypcb[6] != null) {
            propro6.setText(ProcessGlobal.mypcb[6].getSimProgress());
            propro6.setVisible(true);
            prosta6.setText(ProcessGlobal.mypcb[6].getStatusString());
            prosta6.setVisible(true);
            pid6.setText(String.valueOf(ProcessGlobal.mypcb[6].getPID()));
            pid6.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[6].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro6.setFill(Color.RED);
                prosta6.setFill(Color.RED);
                pid6.setFill(Color.RED);
            } else {
                propro6.setFill(Color.BLACK);
                prosta6.setFill(Color.BLACK);
                pid6.setFill(Color.BLACK);
            }
        } else {
            propro6.setVisible(false);
            prosta6.setVisible(false);
            pid6.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 7 && ProcessGlobal.mypcb[7] != null) {
            propro7.setText(ProcessGlobal.mypcb[7].getSimProgress());
            propro7.setVisible(true);
            prosta7.setText(ProcessGlobal.mypcb[7].getStatusString());
            prosta7.setVisible(true);
            pid7.setText(String.valueOf(ProcessGlobal.mypcb[7].getPID()));
            pid7.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[7].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro7.setFill(Color.RED);
                prosta7.setFill(Color.RED);
                pid7.setFill(Color.RED);
            } else {
                propro7.setFill(Color.BLACK);
                prosta7.setFill(Color.BLACK);
                pid7.setFill(Color.BLACK);
            }
        } else {
            propro7.setVisible(false);
            prosta7.setVisible(false);
            pid7.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 8 && ProcessGlobal.mypcb[8] != null) {
            propro8.setText(ProcessGlobal.mypcb[8].getSimProgress());
            propro8.setVisible(true);
            prosta8.setText(ProcessGlobal.mypcb[8].getStatusString());
            prosta8.setVisible(true);
            pid8.setText(String.valueOf(ProcessGlobal.mypcb[8].getPID()));
            pid8.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[8].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro8.setFill(Color.RED);
                prosta8.setFill(Color.RED);
                pid8.setFill(Color.RED);
            } else {
                propro8.setFill(Color.BLACK);
                prosta8.setFill(Color.BLACK);
                pid8.setFill(Color.BLACK);
            }
        } else {
            propro8.setVisible(false);
            prosta8.setVisible(false);
            pid8.setVisible(false);
        }
        if (ProcessGlobal.getPcbnum() > 9 && ProcessGlobal.mypcb[9] != null) {
            propro9.setText(ProcessGlobal.mypcb[9].getSimProgress());
            propro9.setVisible(true);
            prosta9.setText(ProcessGlobal.mypcb[9].getStatusString());
            prosta9.setVisible(true);
            pid9.setText(String.valueOf(ProcessGlobal.mypcb[9].getPID()));
            pid9.setVisible(true);
            if (ProcessGlobal.getRunningPCB() != null && ProcessGlobal.mypcb[9].getPID() == ProcessGlobal.getRunningPCB().getPID()) {
                propro9.setFill(Color.RED);
                prosta9.setFill(Color.RED);
                pid9.setFill(Color.RED);
            } else {
                propro9.setFill(Color.BLACK);
                prosta9.setFill(Color.BLACK);
                pid9.setFill(Color.BLACK);
            }
        } else {
            propro9.setVisible(false);
            prosta9.setVisible(false);
            pid9.setVisible(false);
        }
        refreshDevice();
    }

    private void refreshDevice() {
        if (ProcessGlobal.devices[0] > 0)
            statusa0.setText("#" + ProcessGlobal.devices[0] + " using(" + ProcessGlobal.getDeviceTimeRemaining(0) + ")");
        else statusa0.setText("free");
        if (ProcessGlobal.devices[1] > 0)
            statusa1.setText("#" + ProcessGlobal.devices[1] + " using(" + ProcessGlobal.getDeviceTimeRemaining(1) + ")");
        else statusa1.setText("free");
        if (ProcessGlobal.devices[2] > 0)
            statusb0.setText("#" + ProcessGlobal.devices[2] + " using(" + ProcessGlobal.getDeviceTimeRemaining(2) + ")");
        else statusb0.setText("free");
        if (ProcessGlobal.devices[3] > 0)
            statusb1.setText("#" + ProcessGlobal.devices[3] + " using(" + ProcessGlobal.getDeviceTimeRemaining(3) + ")");
        else statusb1.setText("free");
        if (ProcessGlobal.devices[4] > 0)
            statusb2.setText("#" + ProcessGlobal.devices[4] + " using(" + ProcessGlobal.getDeviceTimeRemaining(4) + ")");
        else statusb2.setText("free");
        if (ProcessGlobal.devices[5] > 0)
            statusc0.setText("#" + ProcessGlobal.devices[5] + " using(" + ProcessGlobal.getDeviceTimeRemaining(5) + ")");
        else statusc0.setText("free");
        if (ProcessGlobal.devices[6] > 0)
            statusc1.setText("#" + ProcessGlobal.devices[6] + " using(" + ProcessGlobal.getDeviceTimeRemaining(6) + ")");
        else statusc1.setText("free");
        if (ProcessGlobal.devices[7] > 0)
            statusc2.setText("#" + ProcessGlobal.devices[7] + " using(" + ProcessGlobal.getDeviceTimeRemaining(7) + ")");
        else statusc2.setText("free");
    }

    private void refreshBlocked() {
        int[][] blockedPIDAndDevice = new int[10][5];
        int blockedNum = 0;
        for (PCB p : ProcessGlobal.mypcb)
            if (p != null && p.getStatus() == PCB.BLOCKED_STATUS) {
                blockedPIDAndDevice[blockedNum][0] = p.getPID();
                blockedPIDAndDevice[blockedNum][1] = p.getBlockedReasonDevice();
                blockedNum++;
            }
        if (blockedNum > 0) {
            blocked0.setText(String.valueOf(blockedPIDAndDevice[0][0]));
            blockedReason0.setText(ProcessGlobal.explainDevice(blockedPIDAndDevice[0][1]));
            blocked0.setVisible(true);
            blockedReason0.setVisible(true);
        } else {
            blocked0.setVisible(false);
            blockedReason0.setVisible(false);
        }
        if (blockedNum > 1) {
            blocked1.setText(String.valueOf(blockedPIDAndDevice[1][0]));
            blockedReason1.setText(ProcessGlobal.explainDevice(blockedPIDAndDevice[1][1]));
            blocked1.setVisible(true);
            blockedReason1.setVisible(true);
        } else {
            blocked1.setVisible(false);
            blockedReason1.setVisible(false);
        }
        if (blockedNum > 2) {
            blocked2.setText(String.valueOf(blockedPIDAndDevice[2][0]));
            blockedReason2.setText(ProcessGlobal.explainDevice(blockedPIDAndDevice[2][1]));
            blocked2.setVisible(true);
            blockedReason2.setVisible(true);
        } else {
            blocked2.setVisible(false);
            blockedReason2.setVisible(false);
        }
        if (blockedNum > 3) {
            blocked3.setText(String.valueOf(blockedPIDAndDevice[3][0]));
            blockedReason3.setText(ProcessGlobal.explainDevice(blockedPIDAndDevice[3][1]));
            blocked3.setVisible(true);
            blockedReason3.setVisible(true);
        } else {
            blocked3.setVisible(false);
            blockedReason3.setVisible(false);
        }
        if (blockedNum > 4) {
            blocked4.setText(String.valueOf(blockedPIDAndDevice[4][0]));
            blockedReason4.setText(ProcessGlobal.explainDevice(blockedPIDAndDevice[4][1]));
            blocked4.setVisible(true);
            blockedReason4.setVisible(true);
        } else {
            blocked4.setVisible(false);
            blockedReason4.setVisible(false);
        }
    }

    private void refreshTime() {
        ProcessGlobal.addSystemtime();
        systime.setText(String.valueOf(ProcessGlobal.getSystemtime()));
    }

    private void refreshTimePiece() {
        if (ProcessGlobal.getProcessChoosingMode() == ProcessGlobal.RR)
            timePieceUI.setText(String.valueOf(ProcessGlobal.TIME_PIECE_SIZE));
        else {
            PCB myRunning = ProcessGlobal.getRunningPCB();
            if (myRunning != null)
                timePieceUI.setText(String.valueOf(myRunning.getRemainingTime() + 1));
            else
                timePieceUI.setText(INIT_STATUS_NOTHING);
        }
    }

    private void refreshRunningProcess() {
        PCB myRunning = ProcessGlobal.getRunningPCB();
        if (myRunning != null)
            runningPID.setText(String.valueOf(myRunning.getPID()));
        else
            runningPID.setText(INIT_STATUS_NOTHING);
    }

    @FXML
    private void randomCreateAction() {
        ProcessGlobal.setAutoCreatingMode(false);
        ProcessGlobal.changeCreating();
        if (ProcessGlobal.getCreating()) {
            randomgo.setText(RANDOM_CREATE_STOP_BUTTON_TEXT);
            console.appendText(RANDOM_CREATE_CONSOLE_TEXT);
        } else {
            randomgo.setText(RANDOM_CREATE_BUTTON_TEXT);
            if (!ProcessGlobal.isAutoCreatingMode()) console.appendText(RANDOM_CREATE_STOP_CONSOLE_TEXT);
        }
    }

    @FXML
    private void runProcessAction() {
        ProcessGlobal.setAutoRunningMode(false);
        ProcessGlobal.changeRunning();
        if (ProcessGlobal.getRunning()) {
            runprocess.setText(STOP_BUTTON_TEXT);
            console.appendText(RUN_CONSOLE_TEXT);
        } else {
            runprocess.setText(RUN_BUTTON_TEXT);
            console.appendText(STOP_CONSOLE_TEXT);
        }
    }

    @FXML
    private void delAction() {
        try {
            ProcessGlobal.destroyProcess(Integer.parseInt(delindex.getText()));
        } catch (Exception e) {
            console.appendText(INDEX_INPUT_ERROR);
        }
    }

    private void autoCreate() {
        ProcessGlobal.setAutoCreatingMode(true);
        if (!ProcessGlobal.getCreating()) {
            ProcessGlobal.changeCreating();
        }
    }

    private void autoProcess() {
        ProcessGlobal.setAutoRunningMode(true);
        if (!ProcessGlobal.getRunning() && ProcessGlobal.getPcbnum() > 0) {
            ProcessGlobal.changeRunning();
        }
    }


    public void chooseRA() {//内存分配策略
        if (wayff.isSelected())
            ProcessGlobal.setRamAllocateMode(ProcessGlobal.FF);
        else if (waybf.isSelected())
            ProcessGlobal.setRamAllocateMode(ProcessGlobal.BF);
        else ProcessGlobal.setRamAllocateMode(ProcessGlobal.WF);
    }

    public void choosePS() {//进程调度策略
        if (wayfcfs.isSelected())
            ProcessGlobal.setProcessChoosingMode(ProcessGlobal.FCFS);
        else if (wayspf.isSelected())
            ProcessGlobal.setProcessChoosingMode(ProcessGlobal.SPF);
        else ProcessGlobal.setProcessChoosingMode(ProcessGlobal.RR);
    }

    public void createProcess(int mode) {//只需从可执行文件读出instructionsnum（指令数）和指令即可
        double x = ramarea.getLayoutX();
        double y = ramarea.getLayoutY();
        chooseRA();
        DoubleProperty widp = ramarea.widthProperty();
        int instructionsnum;
        if (mode == 0) {
            instructionsnum = (int) (Math.random() * 5 + 1);
        } else
            instructionsnum = ProcessGlobal.getOpeningInsNum();
        int creating = ProcessGlobal.createProgressBar(x, y, instructionsnum, widp);//返回新创建进程的index
        if (creating >= 0) {
            console.appendText(PROCESS_CREATE_SUCCESS);
            if (mode == 0) {
                randomLoad(creating);
            } else if (mode == 1) {
                ProcessGlobal.loadToPCB(creating);
                ProcessGlobal.setNotOpening();
            }
        } else {
            if (!ProcessGlobal.isAutoCreatingMode())
                console.appendText(PROCESS_CREATE_FAILURE);
            randomCreateAction();
        }
        refresh();
    }

    private void randomLoad(int index) {
        int insnum = ProcessGlobal.mypcb[index].getInstructionsnum();
        for (int i = 0; i < insnum; i++) {
            int kind;
            double deviceOrOther = Math.random();
            if (deviceOrOther >= ProcessGlobal.BLOCKED_RATE_INCREASED_BY) {
                deviceOrOther = 1;
            } else deviceOrOther = 0;
            if (deviceOrOther == 1) {
                kind = (int) (Math.random() * 4);
            } else {
                kind = PCB.DEVICE_OCCUPATION_INSTRUCTION;
            }
            if (kind == PCB.ASSIGNMENT_INSTRUCTION) {
                int op = (int) (Math.random() * 255);
                ProcessGlobal.mypcb[index].setInstructions(kind, op);
            } else if (kind == PCB.DEVICE_OCCUPATION_INSTRUCTION) {
                int time = (int) (Math.random() * insnum + 1);
                ProcessGlobal.mypcb[index].setInstructions(3, index % 3, time);
            } else
                ProcessGlobal.mypcb[index].setInstructions(kind);
        }
    }

    private void executeIns() {
        this.deleteObservers();
        for (int i = 0; i < ProcessGlobal.getPcbnum(); i++)
            this.addObserver(ProcessGlobal.mypcb[i]);
        choosePS();
        ProcessGlobal.processChoosing();
        refreshTimePiece();
        refreshRunningProcess();
    }

    class tTimerTask extends TimerTask {
        @Override
        public void run() {
            setChanged();
            notifyObservers();
            if (ProcessGlobal.getOpening()) {
                Platform.runLater(() -> createProcess(1));
            }
            Platform.runLater(() -> {
                if (ProcessGlobal.checkDeadEmbraced()) {
                    console.setText(DEAD_EMBRACED_SOLVING);
                    int deadPID = ProcessGlobal.solveDeadEmbraced();
                    if (deadPID != 999999999)
                        console.appendText("#" + deadPID + "已撤销\n");
                    else
                        console.appendText(DEAD_EMBRACED_SOLVING_FAILURE);
                }
            });
            if (ProcessGlobal.addPeriodnum() == (1000 / ProcessGlobal.getPeriod()) - 1) {//无论怎么改动间隔，if内内容都是1s一次
                refreshTime();
                Platform.runLater(() -> {
                    ProcessGlobal.useDeviceTimeRemaining();
                    if (ProcessGlobal.getCreating())
                        createProcess(0);
                    if (ProcessGlobal.getRunning()) executeIns();
                    refreshNotice();
                    if (ProcessGlobal.getPcbnum() <= 0 && ProcessGlobal.getRunning()) {
                        console.appendText(PROCESS_DONE);
                        runProcessAction();
                    }

                });
                if (autocreate.isSelected()) autoCreate();
                if (autoprocess.isSelected()) autoProcess();

            }
            refresh();
            refreshBlocked();
        }
    }
}
