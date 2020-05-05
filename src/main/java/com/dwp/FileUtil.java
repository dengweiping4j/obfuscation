package com.dwp;

import org.codehaus.plexus.util.FileUtils;

import java.io.*;

/**
 * 文件操作工具类
 *
 * @author denngweiping
 * 2020-04-01
 */
public class FileUtil {

    /**
     * 创建文件
     *
     * @param path        文件名称
     * @param filecontent 文件内容
     * @return 是否创建成功，成功则返回true
     */
    public static boolean createFile(String path, String filecontent) {
        Boolean bool = false;
        File file = new File(path);
        try {
            //如果文件不存在，则创建新的文件
            if (!file.exists()) {
                file.createNewFile();
                bool = true;
                //创建文件成功后，写入内容到文件里
                writeFileContent(path, filecontent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bool;
    }

    /**
     * 向文件中写入内容
     *
     * @param filepath 文件路径与名称
     * @param fileIn   写入的内容
     * @return
     * @throws IOException
     */
    public static boolean writeFileContent(String filepath, String fileIn) throws IOException {
        Boolean bool = false;
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            //文件原有内容
            for (int i = 0; (temp = br.readLine()) != null; i++) {
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(fileIn);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return bool;
    }

    /**
     * 删除文件
     *
     * @param path 文件名称
     * @return
     */
    public static boolean delFile(String path) {
        Boolean bool = false;
        File file = new File(path);
        if (file.exists()) {
            bool = file.delete();
        } else {
            System.out.println("未找到指定文件：" + path);
        }
        return bool;
    }

    /**
     * 复制文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File source, File dest)
            throws IOException {
        FileUtils.copyFile(source, dest);
    }

    /**
     * 通过文件路径直接修改文件名
     *
     * @param filePath    需要修改的文件的完整路径
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    public static boolean FixFileName(String filePath, String newFileName) throws Exception {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            throw new RuntimeException("源文件不存在！");
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return false;
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        return f.renameTo(nf); // 修改文件名
    }

}
