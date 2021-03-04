package model;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import tools.ProcessGlobal;

public class PCB implements Observer, Serializable {
    final public static int BUILT_STATUS = 0;
    final public static int READY_STATUS = 1;
    final public static int RUNNING_STATUS = 2;
    final public static int BLOCKED_STATUS = 3;
    final public static int FINISHED_STATUS = 4;
    final public static String BUILT_STATUS_STRING = "built";
    final public static String READY_STATUS_STRING = "ready";
    final public static String RUNNING_STATUS_STRING = "running";
    final public static String BLOCKED_STATUS_STRING = "blocked";
    final public static String FINISHED_STATUS_STRING = "finished";
    final public static String NOTHING_STATUS_STRING = "nothing";
    final public static int A_DEVICE = 0;
    final public static int B_DEVICE = 1;
    final public static int C_DEVICE = 2;
    final public static boolean SUCCESS_RUN = true;
    final public static boolean FAILURE_RUN = false;
    final public static int ASSIGNMENT_INSTRUCTION = 0;
    final public static int INCREASEMENT_INSTRUCTION = 1;
    final public static int DECREASEMENT_INSTRUCTION = 2;
    final public static int DEVICE_OCCUPATION_INSTRUCTION = 3;
    final public static int END_INSTRUCTION = 4;
    final public static int SIZE_PER_INSTRUCTION = 20;

    private int PID;
    private int status;
    private int instructionsnum;//指令数
    private int size;//此进程进度条的高度(GUI)
    private int pc;//当前进程运行到第几条
    private int pcloading;
    private ProgressBar bar;
    private Label label;
    private int[] instructions;
    private int[] operands;
    private int[] usingdevice;//正在用几号设备
    private int deviceNum;//共占用多少设备
    private int[] deviceTimePref;//占用x号设备多少时间
    private int blockedReasonDevice;
    private int remainingTime;
    private double simprogress;//大进度+细分进度
    private int simRunning;//下一次之前细分为1000/period次
    final private int runningmax = 1000 / ProcessGlobal.getPeriod();
    private int blockedTime;

    public PCB(double x, double y, int instructionsnum,DoubleProperty widp, double pbirth) {
        PID = ProcessGlobal.getPID();
        setStatus(BUILT_STATUS);
        this.instructionsnum = instructionsnum;
        size = instructionsnum*SIZE_PER_INSTRUCTION;
        pc = 0;
        pcloading = 0;
        bar = new ProgressBar(0);
        bar.setLayoutX(x);
        bar.setLayoutY(y + pbirth);
        bar.prefWidthProperty().bind(widp);
        bar.setPrefHeight(size);

        label = new Label("#" + PID);
        label.setLayoutX(x);
        label.setLayoutY(y + pbirth);
        ProcessGlobal.addThing(bar);
        ProcessGlobal.addThing(label);

        instructions = new int[instructionsnum];
        operands = new int[instructionsnum];
        deviceTimePref = new int[instructionsnum];
        for (int i = 0; i < instructionsnum; i++) {
            instructions[i] = 0;
            operands[i] = 0;
            deviceTimePref[i] = 0;
        }
        usingdevice = new int[8];
        for (int i = 0; i < 8; i++) {
            usingdevice[i] = 0;
        }
        deviceNum = 0;

        simprogress = 0;
        simRunning = -1;
        remainingTime = instructionsnum;
        blockedTime=0;
    }

    public int getPID() {
        return PID;
    }

    public int getPC(){
        return pc;
    }

    public int getDeviceNum(){
        return deviceNum;
    }

    public int[] getInstructions(){
        return instructions;
    }

    public int[] getOperands(){
        return operands;
    }

    public int[] getDeviceTimePref(){
        return deviceTimePref;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusString() {
        switch (status) {
            case BUILT_STATUS:
                return BUILT_STATUS_STRING;
            case READY_STATUS:
                return READY_STATUS_STRING;
            case RUNNING_STATUS:
                return RUNNING_STATUS_STRING;
            case BLOCKED_STATUS:
                return BLOCKED_STATUS_STRING;
            case FINISHED_STATUS:
                return FINISHED_STATUS_STRING;
            default:
                return NOTHING_STATUS_STRING;
        }
    }

    public int getSize() {
        return size;
    }

    public double getYa() {
        return bar.getLayoutY();
    }

    public double getYb() {
        return bar.getLayoutY() + size;
    }

    public int getInstructionsnum() {
        return instructionsnum;
    }

    public String getProgress() {
        if (pc == 0) return String.valueOf(0) + "%";
        else return String.valueOf(pc * 100 / instructionsnum) + "%";
    }

    public String getSimProgress() {
        if (pc == 0) return String.valueOf(0) + "%";
        else return String.valueOf((int) Math.ceil(simprogress * 100)) + "%";
    }

    public int getSimProgressNum() {
        return (int) Math.ceil(simprogress * 100);
    }

    public double getProgressNum() {
        if (pc == 0) return 0;
        else return pc * 1.0 / instructionsnum;
    }

    public boolean getSimRunning() {
        return simRunning > 0;
    }

    public void delMe() {
        status = 4;
        size = 0;
        for (int i = 0; i < deviceNum; i++)
            ProcessGlobal.devices[usingdevice[i]] = -1;
        ProcessGlobal.delThing(bar);
        ProcessGlobal.delThing(label);
    }

    public void setStatus(int i) {
        status = i;
    }

    public void setInstructions(int ins) {
        if (pcloading < instructionsnum) {
            instructions[pcloading] = ins;
            ++pcloading;
        }
    }

    public void setInstructions(int ins, int op) {
        if (pcloading < instructionsnum) {
            instructions[pcloading] = ins;
            operands[pcloading] = op;
            ++pcloading;
        }
    }

    public void setInstructions(int ins, int op, int time) {
        if (pcloading < instructionsnum) {
            instructions[pcloading] = ins;
            operands[pcloading] = op;
            deviceTimePref[pcloading] = time;
            ++pcloading;
        }
    }

    public void executeInstructions() {
        simRunning = 0;
        if (pc < instructionsnum) {
            status = 2;
            switch (instructions[pc]) {
                case ASSIGNMENT_INSTRUCTION:
                    ProcessGlobal.setIR("X=" + operands[pc]);
                    ProcessGlobal.setX(operands[pc]);
                    break;
                case INCREASEMENT_INSTRUCTION:
                    ProcessGlobal.setIR("X++");
                    ProcessGlobal.addX();
                    break;
                case DECREASEMENT_INSTRUCTION:
                    ProcessGlobal.setIR("X--");
                    ProcessGlobal.subX();
                    break;
                case DEVICE_OCCUPATION_INSTRUCTION:
                    int deviceid = ProcessGlobal.requestDevice(operands[pc],PID);
                    if (deviceid >= 0) {
                        usingdevice[deviceNum++] = deviceid;
                        ProcessGlobal.setDeviceTimeRemaining(deviceid,deviceTimePref[pc]);
                    } else {
                        status = 3;
                        blockedReasonDevice = operands[pc];
                        blockedTime++;
                    }
                    ProcessGlobal.setIR("!" + ProcessGlobal.explainDevice(operands[pc]));
                    break;
                case END_INSTRUCTION:
                    ProcessGlobal.setIR("end");
                    pc = instructionsnum;
                    setStatus(PCB.FINISHED_STATUS);
                    break;
                default:
            }
            if (getStatus()==PCB.RUNNING_STATUS) {
                bar.setProgress((double) getProgressNum());
                simprogress = getProgressNum();
                simRunning++;
                ++pc;
                --remainingTime;
            }
        } else {
            setStatus(PCB.FINISHED_STATUS);
            bar.setProgress(1);
        }
    }

    public void animationProgressBar() {
        if (simRunning < runningmax && simRunning >= 0) {
            simprogress += (ProcessGlobal.getPeriod() / (1000.0 * instructionsnum));
            bar.setProgress(simprogress);
            if (++simRunning >= runningmax - 1) {
                simRunning = -1;
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (status == 2)
            animationProgressBar();
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getBlockedReasonDevice() {
        return blockedReasonDevice;
    }

    public int getBlockedTime() {
        return blockedTime;
    }

    public void resetBlockedTime() {
        blockedTime = 0;
    }
}
