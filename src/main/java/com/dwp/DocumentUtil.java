package com.dwp;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * 使用dom4j操作xml
 *
 * @author denngweiping
 * 2020-04-01
 */
public class DocumentUtil {

    public static void setConfigDirPath(String configPath, String dirPath) throws Exception {
        //读取XML文件，获得document对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(configPath));
        document.setXMLEncoding("UTF-8");
        // 获取根节点
        Element root = document.getRootElement();
        Element input = (Element) root.elements("input").get(0);
        Element jar = (Element) input.elements("dir").get(0);
        Attribute in = jar.attribute("in");
        in.setValue(dirPath);
        Attribute out = jar.attribute("out");
        out.setValue(dirPath);
        //格式化输出流，同时指定编码格式。也可以在FileOutputStream中指定。
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(configPath), format);
        writer.write(document);
        writer.close();
    }

    /**
     * 向配置文件中添加需要忽略的类
     *
     * @param path          源文件路径
     * @param classPathList 需要忽略的类集合
     * @throws Exception
     */
    private static void addIgnore(String path, Set<String> classPathList) throws Exception {
        //读取XML文件，获得document对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        document.setXMLEncoding("UTF-8");
        // 获取根节点
        Element root = document.getRootElement();
        Element keepNames = (Element) root.elements("keep-names").get(0);
        for (String classPath : classPathList) {
            keepNames.addElement("class").addAttribute("template", "class " + classPath);
        }
        for (int i = 0; i < keepNames.elements("class").size(); i++) {
            Element ignore = (Element) keepNames.elements("class").get(i);
            ignore.addElement("field").addAttribute("access", "private+");
            ignore.addElement("method").addAttribute("access", "private+");
        }
        //格式化输出流，同时指定编码格式。也可以在FileOutputStream中指定。
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
        writer.write(document);
        writer.close();
    }

    /**
     * 创建配置文件
     *
     * @param classPath    class文件所在路径
     * @param resourcePath 指定加密类列表的文件所在目录
     * @throws Exception
     */
    public static void createConfigFile(String classPath, String resourcePath) throws Exception {
        //读取用户配置文件并修改config内容
        //读取配置文件

        //全包扫描，扫描所有class文件，并将全类名写入ignoreList
        Set<String> ignoreList = JarFileLoader.scanClassPathList(classPath, new HashSet<String>());
        if (ignoreList == null || ignoreList.isEmpty()) {
            System.out.println("error: " + classPath + " 在指定目录下未扫描到class文件!");
            return;
        }

        File file = new File(resourcePath);
        if (!file.exists()) {
            // 文件不存在
            System.out.println("warm: 未指定需要混淆的class类！");
            //System.out.println(" warning: 未指定需要混淆的class类！");
        } else {
            InputStream in = new BufferedInputStream(new FileInputStream(resourcePath));
            Set<String> encryptList = new HashSet<String>();
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);     ///加载属性列表
                Iterator<String> it = prop.stringPropertyNames().iterator();
                System.out.println("以下类将被混淆：");
                //System.out.println("info: 以下类将被混淆");
                while (it.hasNext()) {
                    String key = it.next();
                    if (key != null && key.trim().length() > 0) {
                        System.out.println(key);
                        //System.out.println(key);
                        encryptList.add(key);
                    }
                }
            }
            in.close();
            //移除需要加密的类
            ignoreList.removeAll(encryptList);
        }
        addIgnore(classPath + "/config.xml", ignoreList);
    }

}
