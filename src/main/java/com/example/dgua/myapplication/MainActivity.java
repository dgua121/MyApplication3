package com.example.dgua.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Build.VERSION_CODES.*;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public Handler handler;
    public String sss = "12321";
    Socket client;
    private static String state11 = "h";
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private static int i = 0;
    public int card = 1;
    private static boolean k = false, j = false, ll = false, lll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler() {
            @RequiresApi(api = VERSION_CODES.LOLLIPOP)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };
        //初始化
        init();
        //注册运动感应
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        if (mSensorManager != null) {
            //获取加速度传感器
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }


    }

    void init() {

        //初始化服务器
        new Thread() {
            @Override
            public void run() {
                super.run();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                       /// aaa();
                    }
                }.start();

                try {
                    ServerSocket serverSocket = new ServerSocket(12345);

                    while (true) {
                        // 一旦有堵塞, 则表示服务器与客户端获得了连接
                        client = serverSocket.accept();
                        client.setSoTimeout(30000);
                        System.out.println("连接。。。。。");
                        //new HandlerThread(client);
                        new HandlerThread3(client);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        j = false;
        ll = true;
        lll = false;
        state11 = "over";
    }

    @Override
    protected void onStop() {
        super.onStop();

        j = true;
        ll = false;
        k = false;
        state11 = "ALERTING";
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(22000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!ll) {
                   // rejectCall();
                    lll = true;//超过20妙不接通电话失效
                }
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        j = false;
        lll = false;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    //检测判定电话接通事件
    public void tim() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    i++;
                    System.out.println(i);
                    System.out.println(k + "000");
                    System.out.println(j);
                    System.out.println(ll);
                    System.out.println(lll);
                    sleep(200);

                    if (i >= 3 && !k && j && !ll && !lll) {
                        k = true;//表示超过2次之后就不继续产生事件
                        i = 0;
                        System.out.println("接通---震动");
                        ll = true;//表示区别挂断--震动
                        state11 = "ACTIVE";
                        card = 1;
                    }
                    i = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

// SensorEventListener回调方法

    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            if (x >= 0.10 && y >= 0.05 && z >= 9.5) {
                tim();
                System.out.println(x);
                System.out.println(y);
                System.out.println(z);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    //fasong线程
    public class HandlerThread3 implements Runnable {
        private Socket socket;

        public HandlerThread3(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {

            while (true) {

                try {
                    PrintWriter pw1 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    if (!state11.equals("h")) {
                        pw1.println(state11);
                        pw1.flush();
                        System.out.println("发送状态，，，，，，，，，，，");
                        state11 = "h";
                    } else {
                        pw1.println("h");
                        pw1.flush();
                        state11 = "h";
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = br.readLine();
                    if (str != null) {
                        if (str.equals("over")) {
                            Message message = new Message();
                            message.what = 122;
                            handler.sendMessage(message);
                        } else if (str.equals("b")) {
                            state11 = "h";
                        } else if (str.contains("Q1")) {
                            if (str.length() >= 12) {
                                sendSMS(str.substring(1, 11), str.substring(12, str.length()));
                            }
                        } else if (!(str.equals("h") || str.equals("over") || str.contains("Q1") || str.equals("b"))) {

                            Message message = new Message();
                            message.what = 121;
                            message.obj = str;
                            handler.sendMessage(message);
                        }
                        System.out.println("shoudao-----" + str);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    protected void handleMsg(Message msg) {

        if (msg.what == 121) {
            Click(msg.obj.toString());
        }
        if (msg.what == 122) {
            rejectCall();
            System.out.println("挂断电话");
        }
        if (msg.what == 111) {
            sss = msg.obj.toString();
        }
    }

    //挂断电话
    public void rejectCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            // Log.d(TAG, "", e);
        } catch (ClassNotFoundException e) {
            //Log.d(TAG, "", e);
        } catch (Exception e) {
        }
    }

    //拨打电话
    private void CallPhone(String a) {
        String number = a;
        if (TextUtils.isEmpty(number)) {
            // 提醒用户
            // 注意：在这个匿名内部类中如果用this则表示是View.OnClickListener类的对象，
            // 所以必须用MainActivity.this来指定上下文环境。
            Toast.makeText(MainActivity.this, "号码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(); // 意图对象：动作 + 数据
            intent.setAction(Intent.ACTION_CALL); // 设置动作
            Uri data = Uri.parse("tel:" + number); // 设置数据
            intent.setData(data);
            startActivity(intent); // 激活Activity组件
        }
    }

   /* @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public static boolean isMultiSim(Context context) {
        boolean result = false;
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null) {
            @SuppressLint({"MissingPermission", "NewApi", "LocalSuppress"}) List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            result = phoneAccountHandleList.size() >= 2;
        }
        return result;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public void call(Context context, int id, String telNum) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        if (telecomManager != null) {
            @SuppressLint("MissingPermission") List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + telNum));
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandleList.get(id));
            context.startActivity(intent);
        }
    }
*/

    public void Click(String s) {
        // 检查是否获得了权限（Android6.0运行时权限）
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // 没有获得授权，申请授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)) {
                // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                // 弹窗需要解释为何需要该权限，再次请求授权
                Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();

                // 帮跳转到该应用的设置界面，让用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                // 不需要解释为何需要该权限，直接请求授权
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            // 已经获得授权，可以打电话
            CallPhone(s);
            //call(this, card, s);

        }
    }

    public void aaa() {
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
                    String state = matcher.group(2); //提取通话状态
                    System.out.println(state);
                    if (state.equals("ACTIVE")) {
                        if (j) {

                            state11 = "ACTIVE";
                            System.out.println(state);

                        }

                    }

                }

            }
            pw.close();
            br.close();
            process.destroy();
        } catch (Exception e) {

        }
    }


    /**
     * 调用短信接口发短信
     */
    public void sendSMS(String phoneNumber, String message) {
        System.out.println("99" + phoneNumber);
        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                .getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null,
                    null);
        }
        System.out.println("88899" + phoneNumber);
    }

}
