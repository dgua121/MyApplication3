package com.example.dgua.myapplication;

import android.os.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class mysocket implements Runnable {
    public static final int PORT = 12345;//监听的端口号
    public String state1 = "";
    public static Socket client;
    public static boolean kg = false;

    public void mn() {
        System.out.println("服务器启动...\n");
        new Thread(this).start();
    }

    public void get() {
        kg = true;
    }

    public void init() {

        //new HandlerThread2();
    }

    @Override
    public void run() {
        mysocket server = new mysocket();
        server.init();
    }

    private class HandlerThread2 implements Runnable {


        public HandlerThread2() {

            new Thread(this).start();
        }

        public void run() {
            try {
                //正则表达式，匹配通话状态
                Pattern ptn = Pattern.compile("(\\d{2}\\-\\d{2}\\s\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}).*?GET_CURRENT_CALLS.*?,(\\w+),");
                //Pattern ptn = Pattern.compile("(\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}).*?qcril_qmi_voice_all_call_status_ind_hdlr:.call.state.(\\d),");

//使用Root权限，执行logcat命令
                Process process = null;
                try {
                    process = Runtime.getRuntime().exec("su");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PrintWriter pw = new PrintWriter(process.getOutputStream());
                pw.println("logcat -v time -b radio"); //logcat命令, -v 详细时间; -b radio 通信相关日志缓冲区
                pw.flush();
//循环读取通话日志
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String strLine;

                while ((strLine = br.readLine()) != null) {
                    Matcher matcher = ptn.matcher(strLine);
                    if (matcher.find()) {// 匹配结果
                        String time = matcher.group(1);  //提取通话时间
                        final String state = matcher.group(2); //提取通话状态

                        if (!state1.equals(state)) {
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = state;
                            //MainActivity.handler.sendMessage(msg);
                            System.out.println("000" + state);
                        }
                        state1 = state;
                    }
                }
                pw.close();
                br.close();
                process.destroy();
            } catch (Exception e) {
            }

        }
    }

}