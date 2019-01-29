package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jidouauto.fast.app.R;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView tvTest;
    private TextView tvResult;
    private File[] plugins;
    private Button btnInstall;
    private Button btnTest;
    private int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTest = (TextView) findViewById(R.id.tv_test);
        tvResult = (TextView) findViewById(R.id.tv_result);
        btnTest = (Button) findViewById(R.id.btn_test);
        btnInstall = (Button) findViewById(R.id.btn_install);


        //安装apk
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String path = Environment.getExternalStorageDirectory() + "/plugin";

                File filed = new File(path);
                if (!filed.exists()) {
                    filed.mkdir();
                }


                CopyAssets(getApplicationContext(), "marketlib.apk", path + "/marketlib.apk");

                //获取插件
                File file = new File(path + "/marketlib.apk");
                if (!file.exists()) {
                    return;
                }

                try {
                    tvTest.setText(file.getPath());
                    a = PluginManager.getInstance().installPackage(file.getPath(), PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


//                plugins = file.listFiles();
//                //没有插件
//                if (plugins == null || plugins.length == 0) {
//                    Log.d("sss", "插件安装失败==没有找到插件");
//                    return;
//                }
                //1.先卸载apk
//                try {
//                    PluginManager.getInstance().deletePackage("com.pugin", 0);
//                } catch (RemoteException e) {
//                    Log.d("sss", "插件卸载失败==" + e.getMessage().toString());
//                    e.printStackTrace();
//                }
//
//                for (File apk : plugins) {
//                    if (!apk.getAbsolutePath().contains("apk")) {
//                        Log.d("sss", "不是apk文件啊==" + apk.getName());
//                        continue;
//                    }
//                    try {
//                        tvTest.setText(apk.getAbsolutePath());
//                        Log.d("sss", "即将安装的apk==" + apk.getAbsolutePath());
//                        //a = PluginManager.getInstance().installPackage(plugins[0].getAbsolutePath(), 0);//安装第一个插件
//                        a = PluginManager.getInstance().installPackage(apk.getAbsolutePath(), PackageManagerCompat.INSTALL_REPLACE_EXISTING);
//
//                        getResult(a);
//                    } catch (RemoteException e) {
//                        Log.d("sss", "插件安装失败==" + e.getMessage().toString());
//                        e.printStackTrace();
//
//                    }
//                }


            }
        });

        //启动APK插件
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction("module.http.jidouauto.com.instantsample.MainActivity");
//                startActivity(intent);
                try {

                    startActivity(MainActivity.this, "module.http.jidouauto.com.instantsample");

//                    PackageManager pm = getPackageManager();
//
//                    Intent intent = pm.getLaunchIntentForPackage("module.http.jidouauto.com.instantsample");
//
//                    if (intent == null) {
//                        Log.d("sss", "intent是空的，没法使用啊");
//                        return;
//                    }
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sss", "插件启动失败==" + e.toString());
                }
            }
        });
    }

    void startActivity(Activity activity, String packageName) {
        PackageManager pm = activity.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }


    private void getResult(int a) {
        switch (a) {
            case -1:
                tvResult.setText("安装或卸载失败");
                break;
            case 1:
                tvResult.setText("安装或卸载成功");
                break;
            case -110:
                tvResult.setText("安装程序内部错误");
                break;
            case -2:
                tvResult.setText("无效的Apk");
                break;
            case 0x00000002:
                tvResult.setText("安装更新");
                break;
            case -3:
                tvResult.setText("不支持的ABI");
                break;

            default:
                tvResult.setText("老天都不知道这是咋了,a==" + a);
                break;
        }

    }

    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
