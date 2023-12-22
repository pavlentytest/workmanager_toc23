package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private OneTimeWorkRequest workRequest1, workRequest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data send_data = new Data.Builder()
                .putString("key","Hello from activity!")
                .build();

        workRequest1 = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(send_data)
                .build();
        workRequest2 = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();

        // параллельно!
        ArrayList<OneTimeWorkRequest> list = new ArrayList<>();
        list.add(workRequest1);
        list.add(workRequest2);
        // параллельно!!!
        WorkManager.getInstance(this).enqueue(list);

        // последовательно
        WorkManager.getInstance(this).beginWith(list).enqueue();
        WorkManager.getInstance(this).beginWith(workRequest1).then(workRequest2).enqueue();


        WorkManager.getInstance(this).enqueue(workRequest1);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest1.getId()).observe(
                this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d("RRR","Status="+workInfo.getState());
                        int x = workInfo.getOutputData().getInt("key2",0);
                        Log.d("RRR","x="+x);
                    }
                }
        );

    }
}