package tools;

import java.io.*;

public class ObjectDatUtil {

    private static ObjectDatUtil objectDatUtil = null;

    private ObjectDatUtil() {

    }

    public ObjectDatUtil getInstance() {
        if (objectDatUtil == null) {
            objectDatUtil = new ObjectDatUtil();
        }
        return objectDatUtil;
    }

    /**
     * 将对象转换为DAT文件存储
     *
     * @param object
     *            要存储的对象
     * @param fileName
     *            带完全的保存路径的文件名
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
    public static void object2Dat(Object object, String fileName)
            throws FileNotFoundException, IOException, Exception {
        // 创建输出文件
        File fo = new File(fileName);
        // 文件不存在,就创建该文件
        if (!fo.exists()) {
            // 先创建文件的目录
            String path = fileName.substring(0, fileName.lastIndexOf('.'));
            File pFile = new File(path);
            pFile.mkdirs();
        }
        FileOutputStream fs = new FileOutputStream(fileName);
        ObjectOutputStream oo = new ObjectOutputStream(fs);
        oo.writeObject(object);
        oo.flush();
        oo.close();
        fs.close();
    }

    /**
     * 将DAT文件转换为object对象
     *
     * @param objSource
     *            DAT文件路径
     * @return 返回对象
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static <T> T dat2Object(String objSource, Class<T> class1) {
        T object = null;
        FileInputStream fi = null;
        ObjectInputStream oi = null;
        try {
            fi = new FileInputStream(objSource);
            oi = new ObjectInputStream(fi);
            object = (T)oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            object = null;
        } finally {
            try {
                oi.close();
                fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}







