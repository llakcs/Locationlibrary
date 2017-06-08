package com.dchip.locationlib;

import android.content.Context;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.dchip.locationlib.Mode.LMode;

/**
 * Created by llakcs on 2017/6/6.
 */

public class LocationUtils implements RadarUploadInfoCallback, BDLocationListener {


    // 定位相关
    LocationClient mLocClient;
    private LatLng positon = null;
    private String userComment = "";
    private String userID = "";
    private String tag="LocationUtils";
    private Context mContext;
    private boolean islocation=false;
    public static final LocationUtils utils = new LocationUtils();
    private LMode lmode;
    public static LocationUtils getIns() {
        return utils;
    }

    /**
     * 初始化
     * @param context
     * @param uploadType  true代表连续自动上传位置信息 ,false代表上传一次
     * @return true 上传成功,false上传失败
     */
    public boolean onCreate(Context context, final boolean uploadType) {
        this.mContext = context;
        lmode = new LMode();
        RadarSearchManager.getInstance().setUserID(userID);
        initlocation();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while (positon == null){

                    if(i ==3){
                        islocation =false;
                        Log.e(tag,"###location is timeout,please check BDLocationListener and postion");
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    i++;
                }
                if(uploadType){
                    uploadLocation();
                }else{
                    uploadOnce();
                }
                islocation = true;

            }
        }).start();
        return islocation;
    }

    private void initlocation() {
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }




    /**
     * 设置用户标识码
     * @param id
     */
    public void setUserID(String id){
        this.userID = id;
        RadarSearchManager.getInstance().setUserID(userID);
    }

    /**
     * 设置备注信息，例如街道名称，地址
     * @param userComment
     */
    public void setuserComment(String userComment){
        this.userComment = userComment;
    }


    /**
     * 上传一次位置
     *
     * @param
     */
    private void uploadOnce(){
        if (positon == null) {
            return;
        }
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = userComment;
        info.pt = positon;
        RadarSearchManager.getInstance().uploadInfoRequest(info);
    }


    /**
     * 自动连续上传位置
     * @return  返回true 成功上传位置信息 返回false失败
     */
    private void uploadLocation(){
        if(positon == null){
          return;
        }
        RadarSearchManager.getInstance().startUploadAuto(utils, 5000);
    }

    /**
     * 停止上传位置信息
     */
    public void stopUpload(){
        RadarSearchManager.getInstance().stopUploadAuto();
    }

    /**
     * 清除位置信息
     */
    public void cleaninfo(){
        RadarSearchManager.getInstance().clearUserInfo();
    }

    /**
     *
     * @return  返回地址信息
     */
    public String GetAddrstr(){
      return lmode.getAddrStr();
    }

    /**
     *
     * @return 返回最近定位时间
     */
    public String GetTime(){
        return lmode.getTime();
    }

    /**
     *
     * @return 返回城市代码
     */
    public String GetCitycode(){
        return lmode.getCitycode();
    }


    /**
     *
     * @return 返回位置语义
     */
    public String GetLocationDescribe(){
        return lmode.getLocationDescribe();
    }



    /**
     * 退出时释放资源
     */
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 释放周边雷达相关
        RadarSearchManager.getInstance().clearUserInfo();
        RadarSearchManager.getInstance().destroy();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null) {
            return;
        }
        positon = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        lmode.setAddrStr(bdLocation.getAddrStr());
        lmode.setCitycode(bdLocation.getCityCode());
        lmode.setLocationDescribe(bdLocation.getLocationDescribe());
        lmode.setTime(bdLocation.getTime());
        setuserComment(bdLocation.getAddrStr()+bdLocation.getLocationDescribe());

    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public RadarUploadInfo onUploadInfoCallback() {
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = userComment;
        info.pt = positon;
        Log.e("hjtest", "OnUploadInfoCallback");
        return info;
    }
}
