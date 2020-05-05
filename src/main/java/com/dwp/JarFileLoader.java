package com.dwp;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar包工具类
 *
 * @author denngweiping
 * 2020-04-01
 */
public class JarFileLoader {

    /**
     * 扫描指定文件夹，返回所有calss文件
     *
     * @param path
     * @param resultList
     * @return
     */
    public static Set<String> scanClassPathList(String path, Set<String> resultList) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return null;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        scanClassPathList(file2.getAbsolutePath(), resultList);
                    } else {
                        String filePath = file2.getAbsolutePath();
                        if (filePath.endsWith(".class")) {
                            String classPath = filePath.substring(filePath.lastIndexOf("classes") + 8, filePath.lastIndexOf(".class"));
                            resultList.add(classPath.replace("\\", "."));
                        }
                    }
                }
                return resultList;
            }
        } else {
            throw new RuntimeException("文件不存在!");
        }
    }

    /**
     * copyFile
     *
     * @param url
     * @param targetPath
     * @throws Exception
     */
    public synchronized static void copyFile(URL url, String targetPath) throws Exception {
        String protocol = url.getProtocol();
        int startIndex = url.getPath().indexOf("file:");
        int endIndex = url.getPath().lastIndexOf("!/com/dwp");
        String path = url.getPath().substring(startIndex + 5, endIndex);
        if ("jar".equals(protocol)) {
            JarFile jarFile = new JarFile(new File(path));
            // 遍历Jar包
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                loadJar(jarEntry, path, targetPath);
            }
        } else if ("file".equals(protocol)) {
            File file = new File(path);
            loadFile(file, targetPath);
        }
    }

    private static void loadFile(File file, String targetPath) throws IOException {
        if (null == file) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    loadFile(f, targetPath);
                }
            }
        } else {
            if (file.getName() != null && (file.getName().endsWith(".jar") || file.getName().indexOf("config.xml") != -1)) {
                File allatoriTarget = new File(targetPath + "/" + file.getName());
                FileUtil.copyFile(file, allatoriTarget);
            }
        }
    }

    private static void loadJar(JarEntry jarEntry, String path, String targetPath) throws Exception {
        String entityName = jarEntry.getName();
        String fileName = entityName.substring(entityName.lastIndexOf("/") + 1);
        if (!fileName.endsWith("jar") && fileName.indexOf("config.xml") == -1) {
            return;
        }

        File tempFile = new File(targetPath + "/" + fileName);
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }
        // 如果缓存文件存在，则删除
        if (tempFile.exists()) {
            tempFile.delete();
        }
        InputStream in = null;

        BufferedInputStream reader = null;
        FileOutputStream writer = null;
        in = JarFileLoader.class.getResourceAsStream(entityName);
        if (in == null) {
            in = JarFileLoader.class.getResourceAsStream("/" + entityName);
            if (null == in) {
                return;
            }
        }
        reader = new BufferedInputStream(in);
        writer = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, len);
            buffer = new byte[1024];
        }
        if (in != null) {
            in.close();
        }
        if (writer != null) {
            writer.close();
        }
    }

}
