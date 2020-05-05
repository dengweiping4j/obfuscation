package com.dwp;

import java.io.*;

/**
 * Java执行shell脚本工具类
 *
 * @author denngweiping
 * 2020-04-01
 */
public class ShellExcutor {

    /**
     * 运行bat脚本
     *
     * @param path 文件全路径
     */
    public static void runBat(String path) {
        // TODO Auto-generated method stub
        File batFile = new File(path);
        boolean batFileExist = batFile.exists();
        if (batFileExist) {
            callCmd(path);
        }
    }

    private static void callCmd(String locationCmd) {
        StringBuilder sb = new StringBuilder();
        try {
            Process child = Runtime.getRuntime().exec(locationCmd);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                System.out.println("error: 代码混淆时出现异常");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Java执行shell脚本入口
     *
     * @param fileName 文件名
     * @param path     文件所在目录
     * @throws Exception
     */
    public static void runShell(String fileName, String path) throws Exception {
        try {
            //拼接完整的脚本目录
            String shellPath = path + "/" + fileName;

            //执行脚本
            callScript(shellPath);

        } catch (Exception e) {
            System.out.println("error: 混淆时出现异常");
            //System.out.println("混淆时出现异常");
            throw e;
        }
    }

    /**
     * 脚本文件具体执行及脚本执行过程探测
     *
     * @param script 脚本文件绝对路径
     * @throws Exception
     */
    private static void callScript(String script) throws Exception {
        try {
            String cmd = "sh " + script;

            //启动独立线程等待process执行完成
            CommandWaitForThread commandThread = new CommandWaitForThread(cmd);
            commandThread.start();

            while (!commandThread.isFinish()) {
                System.out.println("代码混淆中...");
                //System.out.println(" 代码混淆中...");
                Thread.sleep(10000);
            }

            //检查脚本执行结果状态码
            if (commandThread.getExitValue() != 0) {
                throw new Exception("混淆失败，系统异常");
            }
        } catch (Exception e) {
            throw new Exception("混淆时出现异常");
        }
    }

    /**
     * 脚本函数执行线程
     */
    public static class CommandWaitForThread extends Thread {

        private String cmd;
        private boolean finish = false;
        private int exitValue = -1;

        public CommandWaitForThread(String cmd) {
            this.cmd = cmd;
        }

        public void run() {
            try {
                //执行脚本并等待脚本执行完成
                Process process = Runtime.getRuntime().exec(cmd);
                //阻塞执行线程直至脚本执行完成后返回
                this.exitValue = process.waitFor();
            } catch (Throwable e) {
                System.out.println("error: 执行混淆时出现异常:" + cmd);
                //System.out.println("脚本命令执行异常：" + cmd);
                exitValue = 110;
            } finally {
                finish = true;
            }
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public int getExitValue() {
            return exitValue;
        }
    }

    public static void createAndRunShell(String targetPath) throws Exception {
        //将脚本写到要加密的jar所在目录下
        //判断当前项目所在环境 Windows Mac Linux
        if (OSUtil.isMac() || OSUtil.isLinux()) {
            //Mac Linux
            System.out.println("OS: Linux");
            String runAllatoriTarget = targetPath + "/run.sh";
            FileUtil.createFile(runAllatoriTarget, "java -Xms128m -Xmx512m -jar " + targetPath + "/obfuscation-main.jar " + targetPath + "/config.xml");
            //执行脚本
            runShell("run.sh", targetPath);
            System.out.println("********代码混淆完成*********");
        } else if (OSUtil.isWindows()) {
            //Windows
            System.out.println("OS: Windows");
            String runAllatoriTarget = targetPath + "/run.bat";
            FileUtil.createFile(runAllatoriTarget, "java -Xms128m -Xmx512m -jar " + targetPath + "/obfuscation-main.jar " + targetPath + "/config.xml");
            //执行脚本
            runBat(runAllatoriTarget);
        }
    }
}
