package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tools.DiskModel;

public class FAT implements Serializable{
    private DiskBlock[] diskBlocks;
    private transient ObservableList<FileModel> openedFiles;
    private Folder c;
    private Path rootPath = new Path("C:", null);
    private List<Path> paths;

    public FAT() {
        c = new Folder("C:", "root", 0, null);
        diskBlocks = new DiskBlock[256];
        diskBlocks[0] = new DiskBlock(0, DiskModel.END, DiskModel.DISK, c);
        diskBlocks[0].setBegin(true);
        diskBlocks[1] = new DiskBlock(1, DiskModel.END, DiskModel.DISK, c);
        for (int i = 2; i < 256; i++) {
            diskBlocks[i] = new DiskBlock(i, DiskModel.FREE, DiskModel.EMPTY, null);
        }
        openedFiles = FXCollections.observableArrayList(new ArrayList<FileModel>());
        paths = new ArrayList<Path>();
        paths.add(rootPath);
        c.setPath(rootPath);
    }

    public void addOpenedFile(DiskBlock block) {
        FileModel thisFile = (FileModel) block.getObject();
        openedFiles.add(thisFile);
        thisFile.setOpened(true);
    }

    public void removeOpenedFile(DiskBlock block) {
        FileModel thisFile = (FileModel) block.getObject();
        for (int i = 0; i < openedFiles.size(); i++) {
            if (openedFiles.get(i) == thisFile) {
                openedFiles.remove(i);
                thisFile.setOpened(false);
                break;
            }
        }
    }

    /**
     * 判断指定盘块中的文件是否已打开
     * @param block
     * @return
     */
    public boolean isOpenedFile(DiskBlock block) {
        if (block.getObject() instanceof Folder) {
            return false;
        }
        return ((FileModel) block.getObject()).isOpened();
    }

    /**
     * 在指定路径下创建文件夹
     * @param path
     * @return
     */
    public int createFolder(String path) {
        String folderName = null;
        boolean canName = true;
        int index = 1;
        // 得到文件夹名
        do {
            folderName = "文件夹";
            canName = true;
            folderName += index;
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {
                    if (diskBlocks[i].getType().equals(DiskModel.FOLDER)) {
                        Folder folder = (Folder) diskBlocks[i].getObject();
                        if (path.equals(folder.getLocation())) {
                            if (folderName.equals(folder.getFolderName())) {
                                canName = false;
                            }
                        }
                    }
                }
            }
            index++;
        } while (!canName);
        int index2 = searchEmptyDiskBlock();
        if (index2 == DiskModel.ERROR) {
            return DiskModel.ERROR;
        } else {
            Folder parent = getFolder(path);
            Folder folder = new Folder(folderName, path, index2, parent);
            if (parent instanceof Folder) {
                parent.addChildren(folder);
            }
            diskBlocks[index2].allocBlock(DiskModel.END, DiskModel.FOLDER, folder, true);
            Path parentP = getPath(path);
            Path thisPath = new Path(path + "\\" + folderName, parentP);
            if (parentP != null) {
                parentP.addChildren(thisPath);
            }
            paths.add(thisPath);
            folder.setPath(thisPath);
        }
        return index2;
    }

    /**
     * 在指定路径下创建文件
     * @param path
     * @return
     */
    public int createTxtFile(String path) {
        String fileName = null;
        boolean canName = true;
        int index = 1;
        // 得到文件名
        do {
            fileName = "文件";
            canName = true;
            fileName += index;
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {
                    if (diskBlocks[i].getType().equals(DiskModel.FILE)) {
                        FileModel file = (FileModel) diskBlocks[i].getObject();
                        if (path.equals(file.getLocation())) {
                            if (fileName.equals(file.getFileName())) {
                                canName = false;
                            }
                        }
                    }
                }
            }
            index++;
        } while (!canName);
        int index2 = searchEmptyDiskBlock();
        if (index2 == DiskModel.ERROR) {
            return DiskModel.ERROR;
        } else {
            Folder parent = getFolder(path);
            FileModel file = new FileModel(fileName, path, index2, parent,"txt");
            file.setFlag(DiskModel.FLAGWRITE);
            if (parent instanceof Folder) {
                parent.addChildren(file);
            }
            diskBlocks[index2].allocBlock(DiskModel.END, DiskModel.FILE, file, true);
        }
        return index2;
    }

    public int createEFile(String path) {
        String fileName = null;
        boolean canName = true;
        int index = 1;
        // 得到文件名
        do {
            fileName = "文件";
            canName = true;
            fileName += index;
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {
                    if (diskBlocks[i].getType().equals(DiskModel.FILE)) {
                        FileModel file = (FileModel) diskBlocks[i].getObject();
                        if (path.equals(file.getLocation())) {
                            if (fileName.equals(file.getFileName())) {
                                canName = false;
                            }
                        }
                    }
                }
            }
            index++;
        } while (!canName);
        int index2 = searchEmptyDiskBlock();
        if (index2 == DiskModel.ERROR) {
            return DiskModel.ERROR;
        } else {
            Folder parent = getFolder(path);
            FileModel file = new FileModel(fileName, path, index2, parent,"e");
            file.setFlag(DiskModel.FLAGWRITE);
            if (parent instanceof Folder) {
                parent.addChildren(file);
            }
            diskBlocks[index2].allocBlock(DiskModel.END, DiskModel.FILE, file, true);
        }
        return index2;
    }

    public int createEFile(String path, FileModel sFile, FAT fat) {
        String fileName = null;
        boolean canName = true;
        int index = 1;
        // 得到文件名
        do {
            fileName = "文件";
            canName = true;
            fileName += index;
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {
                    if (diskBlocks[i].getType().equals(DiskModel.FILE)) {
                        FileModel file = (FileModel) diskBlocks[i].getObject();
                        if (path.equals(file.getLocation())) {
                            if (fileName.equals(file.getFileName())) {
                                canName = false;
                            }
                        }
                    }
                }
            }
            index++;
        } while (!canName);
        int index2 = searchEmptyDiskBlock();
        if (index2 == DiskModel.ERROR) {
            return DiskModel.ERROR;
        } else {
            Folder parent = getFolder(path);
            FileModel file = new FileModel(fileName, path, index2, parent,"e");

            file.setFlag(DiskModel.FLAGWRITE);
            if (parent instanceof Folder) {
                parent.addChildren(file);
            }
            diskBlocks[index2].allocBlock(DiskModel.END, DiskModel.FILE, file, true);

            int newLength = sFile.getContent().length();
            int blockCount = DiskModel.blocksCount(newLength);
            file.setLength(blockCount);
            file.setContent(sFile.getContent());
            file.setSize(DiskModel.getSize(newLength));
            if (file.hasParent()) {
                Folder fileParent = (Folder) file.getParent();
                fileParent.setSize(DiskModel.getFolderSize(fileParent));
                while (fileParent.hasParent()) {
                    fileParent = (Folder) fileParent.getParent();
                    fileParent.setSize(DiskModel.getFolderSize(fileParent));
                }
            }
            fat.reallocBlocks(blockCount, diskBlocks[index2]);
        }
        return index2;
    }
    public int createTxtFile(String path,FileModel sFile, FAT fat) {
        String fileName = null;
        boolean canName = true;
        int index = 1;
        // 得到文件名
        do {
            fileName = "文件";
            canName = true;
            fileName += index;
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {
                    if (diskBlocks[i].getType().equals(DiskModel.FILE)) {
                        FileModel file = (FileModel) diskBlocks[i].getObject();
                        if (path.equals(file.getLocation())) {
                            if (fileName.equals(file.getFileName())) {
                                canName = false;
                            }
                        }
                    }
                }
            }
            index++;
        } while (!canName);
        int index2 = searchEmptyDiskBlock();
        if (index2 == DiskModel.ERROR) {
            return DiskModel.ERROR;
        } else {
            Folder parent = getFolder(path);
            FileModel file = new FileModel(fileName, path, index2, parent,"txt");

            file.setFlag(DiskModel.FLAGWRITE);
            if (parent instanceof Folder) {
                parent.addChildren(file);
            }
            diskBlocks[index2].allocBlock(DiskModel.END, DiskModel.FILE, file, true);

            int newLength = sFile.getContent().length();
            System.out.println(newLength);
            int blockCount = DiskModel.blocksCount(newLength);
            System.out.println(blockCount);
            file.setLength(blockCount);
            file.setContent(sFile.getContent());
            file.setSize(DiskModel.getSize(newLength));
            if (file.hasParent()) {
                Folder fileParent = (Folder) file.getParent();
                fileParent.setSize(DiskModel.getFolderSize(fileParent));
                while (fileParent.hasParent()) {
                    fileParent = (Folder) fileParent.getParent();
                    fileParent.setSize(DiskModel.getFolderSize(fileParent));
                }
            }
            fat.reallocBlocks(blockCount, diskBlocks[index2]);
        }
        return index2;
    }
    /**
     * 返回第一个空闲盘块的盘块号
     * @return
     */
    public int searchEmptyDiskBlock() {
        for (int i = 2; i < diskBlocks.length; i++) {
            if (diskBlocks[i].isFree()) {
                return i;
            }
        }
        return DiskModel.ERROR;
    }

    /**
     * 计算已使用盘块数
     * @return
     */
    public int usedBlocksCount() {
        int n = 0;
        for (int i = 2; i < diskBlocks.length; i++) {
            if (!diskBlocks[i].isFree()) {
                n++;
            }
        }
        return n;
    }

    /**
     * 计算空闲盘块数
     * @return
     */
    public int freeBlocksCount() {
        int n = 0;
        for (int i = 2; i < diskBlocks.length; i++) {
            if (diskBlocks[i].isFree()) {
                n++;
            }
        }
        return n;
    }

    /**
     * 文件长度变更时重新分配盘块
     * @param num
     * @param block
     * @return
     */
    public boolean reallocBlocks(int num, DiskBlock block) {
        FileModel thisFile = (FileModel) block.getObject();
        int begin = thisFile.getDiskNum();
        System.out.println(begin);
        int index = diskBlocks[begin].getIndex();
        int oldNum = 1;
        while (index != DiskModel.END) {
            oldNum++;
            if (diskBlocks[index].getIndex() == DiskModel.END) {
                begin = index;
            }
            index = diskBlocks[index].getIndex();
        }

        if (num > oldNum) {
            // 增加磁盘块
            int n = num - oldNum;
            if (freeBlocksCount() < n) {
                // 超过磁盘容量
                return false;
            }
            int space = searchEmptyDiskBlock();
            diskBlocks[begin].setIndex(space);
            for (int i = 1; i <= n; i++) {
                space = searchEmptyDiskBlock();
                if (i == n) {
                    diskBlocks[space].allocBlock(DiskModel.END, DiskModel.FILE, thisFile, false);
                } else {
                    diskBlocks[space].allocBlock(DiskModel.END, DiskModel.FILE, thisFile, false);// 同一个文件的所有磁盘块拥有相同的对象
                    int space2 = searchEmptyDiskBlock();
                    diskBlocks[space].setIndex(space2);
                }
                System.out.println(thisFile);
            }
        } else if (num < oldNum) {
            // 减少磁盘块
            int end = thisFile.getDiskNum();
            while (num > 1) {
                end = diskBlocks[end].getIndex();
                num--;
            }
            int next = 0;
            for (int i = diskBlocks[end].getIndex(); i != DiskModel.END; i = next) {
                next = diskBlocks[i].getIndex();
                diskBlocks[i].clearBlock();
            }
            diskBlocks[end].setIndex(DiskModel.END);
        } else {
            // 不变
        }
        thisFile.setLength(num);
        return true;
    }

    /**
     * 返回指定路径下所有文件夹
     * @param path
     * @return
     */
    public List<Folder> getFolders(String path) {
        List<Folder> list = new ArrayList<Folder>();
        for (int i = 2; i < diskBlocks.length; i++) {
            if (!diskBlocks[i].isFree()) {
                if (diskBlocks[i].getObject() instanceof Folder) {
                    if (((Folder) (diskBlocks[i].getObject())).getLocation().equals(path)) {
                        list.add((Folder) diskBlocks[i].getObject());
                    }
                }
            }
        }
        return list;
    }

    /**
     * 返回所有文件夹和文件的起始盘块
     * @param path
     * @return
     */
    public List<DiskBlock> getBlockList(String path) {
        List<DiskBlock> bList = new ArrayList<DiskBlock>();
        for (int i = 2; i < diskBlocks.length; i++) {
            if (!diskBlocks[i].isFree()) {
                if (diskBlocks[i].getObject() instanceof Folder) {
                    if (((Folder) (diskBlocks[i].getObject())).getLocation().equals(path)
                            && diskBlocks[i].isBegin()) {
                        bList.add(diskBlocks[i]);
                    }
                }
            }
        }
        for (int i = 2; i < diskBlocks.length; i++) {
            if (!diskBlocks[i].isFree()) {
                if (diskBlocks[i].getObject() instanceof FileModel) {
                    if (((FileModel) (diskBlocks[i].getObject())).getLocation().equals(path)
                            && diskBlocks[i].isBegin()) {
                        bList.add(diskBlocks[i]);
                    }
                }
            }
        }
        return bList;
    }

    /**
     * 返回指定路径指向的文件夹
     * @param path
     * @return
     */
    public Folder getFolder(String path) {
        if (path.equals("C:")) {
            return c;
        }
        int split = path.lastIndexOf('\\');
        String location = path.substring(0, split);
        String folderName = path.substring(split + 1);
        List<Folder> folders = getFolders(location);
        for (Folder folder : folders) {
            if (folder.getFolderName().equals(folderName)) {
                return folder;
            }
        }
        return null;
    }

    /**
     * 给出路径名返回路径对象
     * @param path
     * @return
     */
    public Path getPath(String path) {
        for (Path p : paths) {
            if (p.getPathName().equals(path)) {
                return p;
            }
        }
        return null;
    }

    /**
     * 删除
     * @param block
     * @return
     */
    public int delete(DiskBlock block) {
        if (block.getObject() instanceof FileModel) {
            if (isOpenedFile(block)) {
                // 文件已打开，不能删除
                return 3;
            }
            FileModel thisFile = (FileModel) block.getObject();
            Folder parent = thisFile.getParent();
            if (parent instanceof Folder) {
                parent.removeChildren(thisFile);
                parent.setSize(DiskModel.getFolderSize(parent));
                while (parent.hasParent()) {
                    parent = parent.getParent();
                    parent.setSize(DiskModel.getFolderSize(parent));
                }
            }
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree() && diskBlocks[i].getObject() instanceof FileModel) {
                    System.out.println("yes");
                    if (((FileModel) diskBlocks[i].getObject()).equals(thisFile)) {// 同一个对象
                        System.out.println("yes2");
                        diskBlocks[i].clearBlock();
                    }
                }
            }
            return 1;
        } else {
            String folderPath = ((Folder) block.getObject()).getLocation() + "\\"
                    + ((Folder) block.getObject()).getFolderName();
            int index = 0;
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {

                    if (diskBlocks[i].getType().equals(DiskModel.FOLDER)) {
                        if (((Folder) diskBlocks[i].getObject()).equals(block.getObject())) {
                            index = i;
                        }
                    }
                }
            }
            Folder thisFolder = (Folder) block.getObject();
            Folder parent = thisFolder.getParent();
            if (parent instanceof Folder) {
                parent.removeChildren(thisFolder);
                parent.setSize(DiskModel.getFolderSize(parent));
            }
            for (int i = 2; i < diskBlocks.length; i++) {
                if (!diskBlocks[i].isFree()) {
                    if (diskBlocks[i].getType().equals(DiskModel.FILE)) {
                        if (((FileModel) diskBlocks[i].getObject()).getParent().getFolderName().equals(thisFolder.getFolderName())) {
                            diskBlocks[i].clearBlock();
                        }
                    }
                    if (diskBlocks[i].getType().equals(DiskModel.FOLDER)) {
                        if (((Folder) diskBlocks[i].getObject()).getParent().getFolderName().equals(thisFolder.getFolderName())) {
                            diskBlocks[i].clearBlock();
                        }
                    }
                }

            }
            paths.remove(getPath(folderPath));
            diskBlocks[index].clearBlock();
            return 0;
        }
    }

    public DiskBlock[] getDiskBlocks() {
        return diskBlocks;
    }

    public void setDiskBlocks(DiskBlock[] diskBlocks) {
        this.diskBlocks = diskBlocks;
    }

    /**
     * 按盘块号查找盘块
     * @param index
     * @return
     */
    public DiskBlock getBlock(int index) {
        return diskBlocks[index];
    }

    public ObservableList<FileModel> getOpenedFiles() {
        return openedFiles;
    }

    public void setOpenedFiles(ObservableList<FileModel> openFiles) {
        this.openedFiles = openFiles;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public void addPath(Path path) {
        paths.add(path);
    }

    public void removePath(Path path) {
        paths.remove(path);
        if (path.hasParent()) {
            path.getParent().removeChildren(path);
        }
    }

    public void replacePath(Path oldPath, String newName) {
        oldPath.setPathName(newName);
    }

    public boolean hasPath(Path path) {
        for (Path p : paths) {
            if (p.equals(path)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        FAT fat = new FAT();
        System.out.println(fat.getFolderByPath("C:\\文件1\\文件2\\文件3.txt"));
    }
    public String getFolderByPath(String path){
        if(path.equals("C:")){
            return path;
        }
        String [] strs = path.split("\\\\");
        String folder = "";
        int i = 0;
        for(i = 0; i < strs.length-1; i++){
            folder += strs[i];
            if(i != strs.length-2){
                folder += "\\";
            }
        }
        return folder;
    }
    public Object getFileByPath(String path){
        String [] strs = path.split("\\\\");
        String folder = "";
        int i = 0;
        for(i = 0; i < strs.length-1; i++){
            folder += strs[i];
            if(i != strs.length-2){
                folder += "\\";
            }
        }
        String file = strs[i];
        System.out.println("folder:"+folder);
        System.out.println("file:"+file);
        Folder thisFolder = getFolder(folder);
        for (Object child : thisFolder.getChildren()) {
            if(child instanceof Folder) continue;
            FileModel f= (FileModel) child;
            String filename = f.getFileName()+"."+f.getSuffix();
            System.out.println(filename);
            if (filename.equals(file)) {

                return child;
            }
        }
        return null;
    }

    /**
     * 判断指定路径下是否有同名文件夹或文件
     * @param path
     * @param name
     * @return
     */
    public boolean hasName(String path, String name) {
        Folder thisFolder = getFolder(path);
        for (Object child : thisFolder.getChildren()) {
            if (child.toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        openedFiles = FXCollections.observableArrayList(new ArrayList<FileModel>());
    }
}
