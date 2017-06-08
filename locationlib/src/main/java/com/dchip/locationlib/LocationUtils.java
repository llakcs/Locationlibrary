package com.dchip.locationlib;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.dchip.locationlib.Mode.LMode;

/**
 * Created by llakcs on 2017/6/6.
 */

public class LocationUtils implements RadarUploadInfoCallback, BDLocationListener,RadarSearchListener{

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
    private static final int AUTOUPLOAD = 0x05;
    private static final int UPLOAD = 0x03;
    public static LocationUtils getIns() {
        return utils;
    }
    //回调接口
    UploadstateListner mUpload;
    ClearInfoStateListner mClearinfo;
    NearbyInfoListListner mNearby;

    public interface UploadstateListner{
        void Getuploadstate(RadarSearchError error);
    }

    public interface ClearInfoStateListner{
        void Getclearstate(RadarSearchError error);
    }

    public interface  NearbyInfoListListner{
        void GetNearbyInfo(RadarNearbyResult result, RadarSearchError error);
    }

    /**
     * 设置监听单次上传状态
     * @param upload
     */
    public void SetUploadsateListner(UploadstateListner upload){

        this.mUpload = upload;
    }

    /**
     * 设置监听清除位置信息状态
     * @param Clearinfo
     */
    public void SetClearInfoStateListner(ClearInfoStateListner Clearinfo){
        this.mClearinfo = Clearinfo;
    }

    /**
     * 设置监听周边雷达消息
     * @param Nearby
     */
    public void SetNearbyInfoListListner(NearbyInfoListListner Nearby){
        this.mNearby = Nearby;
    }

   Handler lhandler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           switch(msg.what){
               case UPLOAD:
                   Log.e(tag,"#########uploadType = once----------!");
                   uploadOnce();
                   break;
               case AUTOUPLOAD:
                   Log.e(tag,"#########uploadType = auto----------!");
                   uploadLocation();
                   break;
           }
           super.handleMessage(msg);
       }
   };


    /**
     * 初始化
     * @param context
     * @param enable  是否开启上传位置功能,
     * @param uploadType  true代表连续自动上传位置信息 ,false代表上传一次
     *
     */
    public boolean onCreate(Context context,final boolean enable,final boolean uploadType) {
        this.mContext = context;
        lmode = new LMode();
        lhandler = new Handler();
        RadarSearchManager.getInstance().setUserID(userID);
        initlocation();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while(!islocation){
                    Log.e(tag,"########等待百度地图postion--------!");
                    if(i ==3){
                        Log.e(tag,"########超时--------!");
                        islocation = false;
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){

                    }
                    i++;
                }
                if(enable){
                    if(uploadType){
                        Log.e(tag,"#########uploadType = true----------!");
                        lhandler.sendEmptyMessage(AUTOUPLOAD);
                    }else{
                        Log.e(tag,"#########uploadType = false----------!");
                        lhandler.sendEmptyMessage(UPLOAD);
                    }
                }
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
     *
     * @return 返回当前latlng位置
     */
    public LatLng GetPostion(){
        return positon;
    }

    /**
     * 查找周边的人或设备
     */
    public void search(){
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(positon).pageNum(0).radius(2000).pageCapacity(11);
        RadarSearchManager.getInstance().nearbyInfoRequest(option);
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


    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
        if(mNearby != null){
            mNearby.GetNearbyInfo(radarNearbyResult,radarSearchError);
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {
        if(mUpload != null) {
            mUpload.Getuploadstate(radarSearchError);
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

        if(mClearinfo != null){
            mClearinfo.Getclearstate(radarSearchError);
        }
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
        islocation = true;
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
