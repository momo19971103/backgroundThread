package com.example.backgroundthread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button startbut;
    private Button stopbut;
    private Handler mainHandler = new Handler();
    private volatile boolean stopThread = false;
    TextView mytest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startbut = (Button) findViewById(R.id.start);
        stopbut = (Button) findViewById(R.id.stop);
        mytest = (TextView) findViewById(R.id.mytest);
        startbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startThread(v);
            }
        });
        stopbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopThread(v);
            }
        });
    }

    public void startThread(View view) {
        stopThread = false;
        ExampleRunnable runnable = new ExampleRunnable(10);
        new Thread(runnable).start();
        new GoodTask().execute("https://data.ntpc.gov.tw/od/data/api/28AB4122-60E1-4065-98E5-ABCCB69AACA6?$format=json");
         /*
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

          */
    }

    public void stopThread(View view) {
        stopThread = true;
    }

    class ExampleThread extends Thread {
        int seconds;

        ExampleThread(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++) {
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ExampleRunnable implements Runnable {
        int seconds;

        ExampleRunnable(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++) {
                if (stopThread) {
                    return;
                }
                if (i == 5) {
                    /*
                    Handler threadHandler = new Handler(Looper.getMainLooper());
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startbut.setText("50%");
                        }
                    });
                     */
                    /*
                    startbut.post(new Runnable() {
                        @Override
                        public void run() {
                            startbut.setText("50%");
                        }
                    });
                     */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startbut.setText("50%");
                        }
                    });
                }
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GoodTask extends AsyncTask<String, Integer, String> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        private static final int TIME_OUT = 1000;

        String jsonString1 = "";

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... countTo) {
            // TODO Auto-generated method stub
            // 再背景中處理的耗時工作
            try {
                HttpURLConnection conn = null;
                URL url = new URL(countTo[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();
                // 讀取資料
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                jsonString1 = reader.readLine();
                reader.close();

                if (Thread.interrupted()) {
                    throw new InterruptedException();

                }
                if (jsonString1.equals("")) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "網路中斷" + e;
            }

            return jsonString1;
        }
        public void onPostExecute(String result )
        { super.onPreExecute();
            // 背景工作處理完"後"需作的事
            mytest.setText("JSON:\r\n"+ result);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            // 背景工作處理"中"更新的事

        }
    }
}
