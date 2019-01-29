package com.jidouauto.refast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button button, button2;
    List<String> list = new ArrayList<>();
    List<PluginInfo> pluginInfoList = new ArrayList<>();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        lv = findViewById(R.id.lv);

        initView();
    }

    private void initView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getFiles();
                pluginInfoList.clear();
                pluginInfoList = RePlugin.getPluginInfoList();
                if (null != pluginInfoList) {
                    list.clear();
                    for (PluginInfo pluginInfo : pluginInfoList) {
                        list.add(pluginInfo.getPath());
                    }
                    initList();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFile();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RePlugin.startActivity(MainActivity.this,
                        RePlugin.createIntent(pluginInfoList.get(position).getName(),
                                pluginInfoList.get(position).getName() + ".MainActivity"));
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void copyFile() {
        button2.setText("复制中...");
        button2.setEnabled(false);
        new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... integers) {
                String path = Environment.getExternalStorageDirectory() + "/plugin/JDO";
                File filed = new File(path);
                if (!filed.exists()) {
                    boolean isSuccess = filed.mkdir();
                }
                CopyAssets(getApplicationContext(), "apk", path);
                File file = new File(path);
                if (file.exists()) {
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        RePlugin.install(file1.getPath());
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer o) {
                super.onPostExecute(o);
                button2.setText("复制完成");
            }
        }.execute();
    }

    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (null == fileNames) {
                Log.e("file error:", "fileNames is NUll");
                return;
            }
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

    private void initList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

}
