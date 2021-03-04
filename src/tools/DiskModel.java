package tools;

import model.FileModel;
import model.Folder;

import java.util.List;

public class DiskModel {
    //图片
    public static final String ICO = "file:picture/ico.png";
    public static final String FOLDER_IMG = "file:picture/folder.png";
    public static final String FILE_IMG = "file:picture/file.png";
    public static final String DISK_IMG = "file:picture/disk.png";
    public static final String TREE_NODE_IMG = "file:picture/node.png";
    public static final String FORWARD_IMG = "file:picture/forward.png";
    public static final String BACK_IMG = "file:picture/back.png";
    public static final String SAVE_IMG = "file:picture/save.png";
    public static final String CLOSE_IMG = "file:picture/close.png";
    public static final String BACKGROUND ="file:picture/background.png";
    public static final String TXTFILE_IMG ="file:picture/txtFile.png";
    public static final String EXEFILE_IMG ="file:picture/exeFile.png";

    //名称
    public static final String DISK = "磁盘";
    public static final String FOLDER = "文件夹";
    public static final String FILE = "文件";
    public static final String EMPTY = "空";

    //属性
    public static final int FLAGREAD = 0;
    public static final int FLAGWRITE = 1;
    public static final int END = 256;
    public static final int ERROR = -1;
    public static final int FREE = 0;

    public static int blocksCount(int length) {
        if (length <= 64) {
            return 1;
        } else {
            int n = 0;
            if (length % 64 == 0) {
                n = length / 64;
            } else {
                n = length / 64;
                n++;
            }
            return n;
        }
    }

    public static double getSize(int length) {
        return Double.parseDouble((String.format("%.2f", length / 1024.0)));
    }

    public static double getFolderSize(Folder folder) {
        List<Object> children = folder.getChildren();
        double size = 0;
        for (Object child : children) {
            if (child instanceof FileModel) {
                size += ((FileModel) child).getSize();
            } else {
                size += getFolderSize((Folder) child);
            }
        }
        return Double.parseDouble((String.format("%.2f", size)));

    }
}
