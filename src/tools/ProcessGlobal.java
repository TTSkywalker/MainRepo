package tools;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import model.PCB;

import java.util.Observable;
import java.util.Timer;

/*
阻塞测试指令
!A 6
!A 6
x=10
x++
x++
x++
x++
x++
 */

public class ProcessGlobal extends Observable {
    public static final int FF = 0;
    public static final int BF = 1;
    public static final int WF = 2;
    public static final int FCFS = 0;
    public static final int SPF = 1;
    public static final int RR = 2;
    public static final int TIME_PIECE_SIZE = 1;
    final static int DF_PLUS = 0;
    final static int DF_SUB = 1;
    final static int IF_YES = 1;
    final static int IF_NO = 0;
    final static int TF_YES = 1;
    final static int TF_NO = 0;
    public final static int DEVICE_NUM = 8;
    final static int MAX_PROCESS_NUM = 10;
    public static final double BLOCKED_RATE_INCREASED_BY = 0.3;//调高设备占用指令出现的概率(0~1)
    public static final String WRONG_INPUT = "z";
    public static final int MAX_PCB_NUM = 10;

    public static AnchorPane showProcessViewStage;
    public static Timer timer;
    public static Timer fileTimer;
    public static PCB mypcb[] = new PCB[MAX_PROCESS_NUM];
    public static int[] devices = new int[DEVICE_NUM];//设备正在被哪个进程使用
    private static int PSW_DF = DF_PLUS;
    private static int PSW_IF = IF_YES;
    private static int PSW_TP = TF_NO;
    private static int ramsize = 1024;//内存总大小，单位：Byte
    private static int period = 20;//Timer
    private static boolean creating = false;
    private static boolean running = false;
    private static int ramAllocateMode = 0;
    private static int processChoosingMode = 0;
    private static int periodnum = 0;
    private static int systemtime = 0;
    private static String ir;
    private static int x = 0;
    private static int systemPC = 0;
    private static int runningindex = -1;//正在运行的进程的索引
    private static int pcbid = 0;//全局序列号
    private static int pcbnum = 0;//PCB数组中索引
    private static double pbirth = 0;
    private static int freeramnum = 0;//空闲内存块数
    private static FreeRam[] fram = new FreeRam[MAX_PROCESS_NUM + 1];
    private static int chosenram = -1;
    private static String notice;
    private static boolean isNoticed = false;
    private static int[] deviceTimeRemaining = new int[DEVICE_NUM];
    private static int nextRRPID = -1;
    private static boolean autoCreatingMode = false;
    private static boolean autoRunningMode = false;
    private static boolean fileRunningMode = false;
    private static boolean opening = false;//是否处于“打开了可执行文件”的状态
    private static int openingInsNum = -1;//总共有多少个指令
    private static int settingInsNum = -1;//已经设置到第几个指令了
    private static int[][] openingIns = new int[999][3];//临时存放读取的指令,[][0]放指令,[][1]放操作数,[][2]放设备时间

    public static int getDeviceTimeRemaining(int deviceID) {
        return deviceTimeRemaining[deviceID];
    }

    public static boolean getOpening() {
        return opening;
    }

    public static void setOpening(int insNum) {
        opening = true;
        openingInsNum = insNum;
        settingInsNum = 0;
    }

    public static int getOpeningInsNum() {
        return openingInsNum;
    }

    public static int getRamAllocateMode() {
        return ramAllocateMode;
    }

    public static void setRamAllocateMode(int i) {
        ramAllocateMode = i;
    }

    public static void setDeviceTimeRemaining(int deviceID, int time) {
        deviceTimeRemaining[deviceID] = time;
    }

    public static void useDeviceTimeRemaining() {
        for(int i = 0;i<DEVICE_NUM;i++){
            if(deviceTimeRemaining[i]>0)
                --deviceTimeRemaining[i];
            else
                devices[i]=-1;
        }
    }

    public static void setNotOpening() {
        opening = false;
        openingInsNum = -1;
        settingInsNum = -1;
    }

    public static void loadIns(int ins) {
        if (settingInsNum < openingInsNum && settingInsNum != -1)
            openingIns[settingInsNum++][0] = ins;
        else
            settingInsNum = -1;
    }

    public static void loadIns(int ins, int op) {
        if (settingInsNum < openingInsNum && settingInsNum != -1) {
            openingIns[settingInsNum][0] = ins;
            openingIns[settingInsNum++][1] = op;
        } else
            settingInsNum = -1;
    }

    public static void loadIns(int ins, int op, int time){
        if (settingInsNum < openingInsNum && settingInsNum != -1) {
            openingIns[settingInsNum][0] = ins;
            openingIns[settingInsNum][1] = op;
            openingIns[settingInsNum++][2] = time;
        } else
            settingInsNum = -1;
    }

    public static void loadToPCB(int index) {
        for (int i = 0; i < openingInsNum; i++) {
            if (openingIns[i][0] == PCB.ASSIGNMENT_INSTRUCTION)
                mypcb[index].setInstructions(openingIns[i][0], openingIns[i][1]);
            else if(openingIns[i][0]==PCB.DEVICE_OCCUPATION_INSTRUCTION)
                mypcb[index].setInstructions(openingIns[i][0],openingIns[i][1],openingIns[i][2]);
            else
                mypcb[index].setInstructions(openingIns[i][0]);
        }
    }
    //文件对接↑

    public static int getPID() {
        int i = pcbid;
        ++pcbid;//开机以来的PCB流水号
        ++pcbnum;//当前系统内存在的PCB数
        return i;
    }


    public static int getPeriod() {
        return period;
    }

    public static int getPcbnum() {
        return pcbnum;
    }

    public static int getSystemtime() {
        return systemtime;
    }

    public static boolean getCreating() {
        return creating;
    }

    public static boolean getRunning() {
        return running;
    }

    public static String getX() {
        return String.valueOf(x);
    }

    public static void setX(int op) {
        x = op;
    }

    public static String getIr() {
        return ir;
    }

    public static boolean hasNotice(){
        return isNoticed;
    }

    public static String getNotice() {
        String giving = notice;
        notice = null;
        isNoticed = false;
        return giving;
    }

    public static void setNotice(String notice){
        ProcessGlobal.notice = notice;
        isNoticed = true;
    }

    public static PCB getRunningPCB() {
        if (runningindex >= 0) return mypcb[runningindex];
        return null;
    }

    public static int addPeriodnum() {
        periodnum = (periodnum + 1) % (1000 / period);
        return periodnum;
    }

    public static void addX() {
        ++x;
    }

    public static void subX() {
        --x;
    }

    public static void setIR(String irname) {
        ir = irname;
    }

    public static void setRunningindex(int i) {
        if (runningindex > 0 && mypcb[runningindex].getProgressNum() != 1) {
            mypcb[runningindex].setStatus(3);
        }
        runningindex = i;
    }

    public static void changeCreating() {
        creating = !creating;
    }

    public static void changeRunning() {
        running = !running;
    }

    public static void addSystemtime() {
        ++systemtime;
    }

    public static void initFreeram() {
        FreeRam nfr = new FreeRam(ramsize, 0);
        for (int i = 0; i < 8; i++)
            devices[i] = -1;
    }

    public static void clearFreeram() {
        int reduced = 0;
        for (int i = 0; i < freeramnum; i++) {
            if (fram[i].size <= 0) {
                for (int j = i; j < freeramnum; j++)
                    fram[j] = fram[j + 1];
                ++reduced;
            }
            for (int j = i + 1; j < freeramnum; j++) {
                if (fram[i].place > fram[j].place && fram[i].size > 0) {
                    fram[freeramnum] = fram[i];
                    fram[i] = fram[j];
                    fram[j] = fram[freeramnum];
                }
            }
        }
        freeramnum -= reduced;
    }

    public static boolean findFreeram(int size) {
        chosenram = -1;
        clearFreeram();
        if (ramAllocateMode == FF) {
            for (int i = 0; i < freeramnum; i++) {
                if (fram[i].size >= size) {
                    chosenram = i;
                    break;
                }
            }
        } else if (ramAllocateMode == BF) {
            int smallest = ramsize + 1;
            for (int i = 0; i < freeramnum; i++) {
                if (fram[i].size > size && fram[i].size < smallest) {
                    smallest = fram[i].size;
                    chosenram = i;
                }
            }
        } else {
            int biggest = size;
            for (int i = 0; i < freeramnum; i++) {
                if (fram[i].size >= biggest) {
                    biggest = fram[i].size;
                    chosenram = i;
                }
            }
        }

        if (chosenram < 0) return false;
        else return true;
    }

    public static int createProgressBar(double x, double y, int instructionsnum, DoubleProperty widp) {
        if (pcbnum >= 10) return -1;
        int size = instructionsnum * PCB.SIZE_PER_INSTRUCTION;
        if (!findFreeram(size)) return -1;
        pbirth = fram[chosenram].place;
        int index = 0;//新进程即将在index号位置生成
        for (int i = 0; i < pcbnum; i++) {
            if (mypcb[i].getYb() == pbirth) {
                index = i + 1;
                break;
            }
        }
        for (int i = pcbnum; i > index; i--)
            mypcb[i] = mypcb[i - 1];

        PCB np = new PCB(x, y, instructionsnum, widp, pbirth);
        mypcb[index] = np;

        if (size < fram[chosenram].size) {
            fram[chosenram].size -= size;
            fram[chosenram].place += size;
        } else {
            for (int i = chosenram; i < freeramnum; i++)
                fram[i] = fram[i + 1];
            fram[freeramnum] = null;
            --freeramnum;
        }
        pbirth = fram[chosenram].place;

        return index;
    }

    public static void destroyProcess(int index) {
        if (index >= 0) {
            int freeramabove = -1;
            int freerambelow = -1;
            double pbarya = mypcb[index].getYa();
            double pbaryb = mypcb[index].getYb();
            for (int i = 0; i < freeramnum; i++) {
                double ramya = fram[i].place;
                double ramyb = ramya + fram[i].size;
                if (pbarya == ramyb)
                    freeramabove = i;

                if (pbaryb == ramya)
                    freerambelow = i;
            }
            if (freeramabove == -1 && freerambelow == -1) {
                FreeRam nfr = new FreeRam(mypcb[index].getSize(), pbarya);
            } else if (freeramabove >= 0 && freerambelow == -1) {
                fram[freeramabove].size += mypcb[index].getSize();
            } else if (freeramabove == -1 && freerambelow >= 0) {
                fram[freerambelow].place -= mypcb[index].getSize();
                fram[freerambelow].size += mypcb[index].getSize();
            } else {
                fram[freeramabove].size += mypcb[index].getSize();
                fram[freeramabove].size += fram[freerambelow].size;
                fram[freerambelow].size = 0;
            }
            mypcb[index].delMe();
            for (int i = index; i < pcbnum - 1; i++)
                mypcb[i] = mypcb[i + 1];
            mypcb[pcbnum - 1] = null;
            --pcbnum;
            clearFreeram();
        }
    }

    public static void addThing(Node n) {
        showProcessViewStage.getChildren().add(n);
    }

    public static void delThing(Node n) {
        showProcessViewStage.getChildren().remove(n);
    }

    public static void processChoosing() {
        checkFinished();
        if (pcbnum > 0) {
            if (processChoosingMode == FCFS) {
                modeFCFS();
            } else if (processChoosingMode == SPF) {
                modeSPF();
            } else if (processChoosingMode == RR) {//TODO 阻塞进程占用1时间片
                modeRR();
            }
            for (int i = 0; i < pcbnum; i++) {
                if (i != runningindex && mypcb[i].getProgressNum() < 1 && mypcb[i].getStatus() != 3) {
                    mypcb[i].setStatus(1);
                }
            }
            mypcb[runningindex].executeInstructions();
        }
    }

    private static void modeFCFS() {
        runningindex = 0;
        int early = mypcb[0].getPID();
        for (int i = 0; i < pcbnum; i++) {
            if (mypcb[i].getPID() < early) {
                early = mypcb[i].getPID();
                runningindex = i;
            }
        }
    }

    private static void modeSPF() {
        runningindex = 0;
        int shortest = mypcb[0].getSize();
        for (int i = 0; i < pcbnum; i++) {
            if (mypcb[i].getSize() < shortest) {
                shortest = mypcb[i].getSize();
                runningindex = i;
            }
        }
    }

    private static void modeRR() {
        if (nextRRPID > -1) {
            for (int i = 0; i < pcbnum; i++)
                if (mypcb[i].getPID() == nextRRPID) {
                    runningindex = i;
                    break;
                }
        } else
            runningindex = 0;
        nextRRPID = mypcb[(runningindex + 1) % pcbnum].getPID();
    }

    public static String explainDevice(int device) {
        if (device == PCB.A_DEVICE)
            return "A";
        else if (device == PCB.B_DEVICE)
            return "B";
        else if (device == PCB.C_DEVICE)
            return "C";
        else return WRONG_INPUT;
    }

    public static void checkFinished() {
        for (int i = 0; i < pcbnum; i++) {
            if (mypcb[i].getProgressNum() >= 1)
                destroyProcess(i);
        }
    }

    public static boolean checkDeadEmbraced() {
        for (int i = 0; i < pcbnum; i++) {
            if (mypcb[i].getBlockedTime() >= 3)
                return true;
        }
        return false;
    }

    public static int requestDevice(int kind, int PID) {
        switch (kind) {
            case 0:
                for (int i = 0; i < 2; i++)
                    if (devices[i] < 0) {
                        devices[i] = PID;
                        return i;
                    }
                break;
            case 1:
                for (int i = 2; i < 5; i++)
                    if (devices[i] < 0) {
                        devices[i] = PID;
                        return i;
                    }
                break;
            case 2:
                for (int i = 5; i < 8; i++)
                    if (devices[i] < 0) {
                        devices[i] = PID;
                        return i;
                    }
                break;
            default:
        }
        return -1;
    }

    public static int getProcessChoosingMode() {
        return processChoosingMode;
    }

    public static void setProcessChoosingMode(int processChoosingMode) {
        ProcessGlobal.processChoosingMode = processChoosingMode;
    }

    public static int getSystemPC() {
        return systemPC;
    }

    public static void setSystemPC(int systemPC) {
        ProcessGlobal.systemPC = systemPC;
    }

    public static int solveDeadEmbraced() {
        int index = findDeadMinPorcess();
        int deadPID;
        if (index != 999999999) {
            deadPID = mypcb[index].getPID();
            destroyProcess(index);
            resetBlockedTime();
        } else
            deadPID = index;
        return deadPID;
    }

    public static int findDeadMinPorcess() {
        int minDeviceNum = 9;
        int minDeviceIndex = 999999999;
        for (int i = 0; i < pcbnum; i++) {
            if (mypcb[i].getDeviceNum() < minDeviceNum) {
                minDeviceNum = mypcb[i].getDeviceNum();
                minDeviceIndex = i;
            }
        }
        return minDeviceIndex;
    }

    private static void resetBlockedTime() {
        for (int i = 0; i < pcbnum; i++)
            mypcb[i].resetBlockedTime();
    }

    public static boolean isAutoRunningMode() {
        return autoRunningMode;
    }

    public static void setAutoRunningMode(boolean autoRunningMode) {
        ProcessGlobal.autoRunningMode = autoRunningMode;
    }

    public static boolean isAutoCreatingMode() {
        return autoCreatingMode;
    }

    public static void setAutoCreatingMode(boolean autoCreatingMode) {
        ProcessGlobal.autoCreatingMode = autoCreatingMode;
    }

    public static boolean isFileRunningMode() {
        return fileRunningMode;
    }

    public static void changeFileRunningMode() {
        fileRunningMode = !fileRunningMode;
    }

    private static class FreeRam {
        int size;
        int place;

        private FreeRam(double size, double place) {
            this.size = (int) size;
            this.place = (int) place;
            fram[freeramnum++] = this;
        }
    }
}
