package com.dwp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.net.URL;

/**
 * 混淆插件主类
 *
 * @author denngweiping
 * 2020-04-01
 */
@Mojo(name = "obfuscation", defaultPhase = LifecyclePhase.PACKAGE)
public class Obfuscation extends AbstractMojo {
    /**
     * 项目根目录
     */
    @Parameter
    private String basePath="D:/aaa/code/svc/aaa-licence-obfuscation";//不要一开始就让人把你看透
    /**
     * class文件所在目录
     */
    @Parameter
    private String classPath="D:/aaa/code/svc/aaa-licence-obfuscation/target/classes";//学会保留30%的神秘感

    public void execute() {
        try {
            //学会给予，你才能获得更多（复制工具jar包、配置文件到目标目录）
            URL url = this.getClass().getResource("");
            JarFileLoader.copyFile(url, classPath);
            //学会从外界获取自己想要的东西（获取传入的混淆类列表，创建并修改配置文件）
            String resourcePath = basePath + "/classNames.properties";
            DocumentUtil.createConfigFile(classPath, resourcePath);
            //人生需要一盏指路明灯（指定class类所在路径）
            DocumentUtil.setConfigDirPath(classPath + "/config.xml", classPath);
            //道理都懂得和实际去体会是不一样的（创建并运行脚本文件）
            ShellExcutor.createAndRunShell(classPath);
            //不带片履来到这人世间，走的时候也要干干净净的离去（删除多余文件，避免项目污染）
            FileUtil.delFile(resourcePath);
            FileUtil.delFile(classPath + "/obfuscation-main.jar");
            FileUtil.delFile(classPath + "/obfuscation-annotations.jar");
            if (OSUtil.isMac() || OSUtil.isLinux()) {
                FileUtil.delFile(classPath + "/run.sh");
            } else if (OSUtil.isWindows()) {
                FileUtil.delFile(classPath + "/run.bat");
            }
            FileUtil.delFile(classPath + "/config.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
