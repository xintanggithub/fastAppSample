package com.jidouauto.fast.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;
import com.morgoo.helper.compat.PackageManagerCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.MainActivity.CopyAssets;

public class FastMainActivity extends AppCompatActivity {

    Button button, button2;
    List<String> list = new ArrayList<>();
    private int resultCode;
    private ListView lv;
    String app1package = "module.http.jidouauto.com.instantsample";
    String app1package2 = "module.http.jidouauto.com.sample2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_main);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        lv = (ListView) findViewById(R.id.lv);

        initView();
    }

    private void initView() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFiles();
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
                if (position == 0) {
                    startActivity(FastMainActivity.this, app1package);
                } else {
                    startActivity(FastMainActivity.this, app1package2);
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


    @SuppressLint("StaticFieldLeak")
    private void copyFile() {
        button2.setText("复制中...");
//        button2.setEnabled(false);
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
                        try {
                            resultCode = PluginManager.getInstance()
                                    .installPackage(file1.getPath()
                                            , PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                            if (resultCode == 1) {
                                Log.e("jd", "init success!!");
                            } else {
                                Log.e("jd", "init error!!");
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
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

    private void getFiles() {
        String path = Environment.getExternalStorageDirectory() + "/plugin/JDO";
        File filed = new File(path);
        if (filed.exists()) {
            File[] files = filed.listFiles();
            list.clear();
            for (File file : files) {
                list.add(file.getPath());
            }
            initList();
        }
    }

    private void initList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FastMainActivity.this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

}
