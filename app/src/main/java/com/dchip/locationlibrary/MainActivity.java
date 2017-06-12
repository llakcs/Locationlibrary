package com.dchip.locationlibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.dchip.locationlib.LocationUtils;

/**
 * Created by llakcs on 2017/6/8.
 */

public class MainActivity extends AppCompatActivity{
    private Button clean_btn;
    private MainActivity Instance;

    // 地图相关
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button search_btn;
    BitmapDescriptor ff3 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        setContentView(R.layout.activity_main);
        initUi();
        LocationUtils.getIns().onCreate(Instance,MainActivity.this,true,true);
        LocationUtils.getIns().setUserID("FFFFF");
    }
    private void initUi() {
        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        LocationUtils.getIns().enaleMap(mBaiduMap);
        LocationUtils.getIns().setLocationdrawable(ff3);
        clean_btn = (Button) findViewById(R.id.clean_btn);
        clean_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.getIns().cleaninfo();
            }
        });
        search_btn = (Button) findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.getIns().search();
            }
        });
        //监听周边雷达状态
        LocationUtils.getIns().SetNearbyInfoListListner(new LocationUtils.NearbyInfoListListner() {
            @Override
            public void GetNearbyInfo(boolean statue) {
                if(statue){
                    Toast.makeText(MainActivity.this,"查询周边成功!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this,"查询周边失败!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        LocationUtils.getIns().onDestroy();
        // 释放地图
        ff3.recycle();
        mMapView.onDestroy();
        mBaiduMap = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        LocationUtils.getIns().stopUpload();
        super.onStop();
    }
}
