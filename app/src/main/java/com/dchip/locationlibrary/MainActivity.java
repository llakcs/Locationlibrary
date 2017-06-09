package com.dchip.locationlibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dchip.locationlib.LocationUtils;

/**
 * Created by llakcs on 2017/6/8.
 */

public class MainActivity extends AppCompatActivity{
    private Button clean_btn;
    private MainActivity Instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        setContentView(R.layout.activity_main);
        LocationUtils.getIns().onCreate(Instance,MainActivity.this,true,true);
        LocationUtils.getIns().setUserID("FFFFF");
        clean_btn = (Button)findViewById(R.id.clean_btn);
        clean_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.getIns().cleaninfo();
            }
        });
    }

    @Override
    protected void onDestroy() {
        LocationUtils.getIns().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        LocationUtils.getIns().stopUpload();
        super.onStop();
    }
}
