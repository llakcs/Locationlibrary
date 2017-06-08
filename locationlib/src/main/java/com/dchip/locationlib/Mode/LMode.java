package com.dchip.locationlib.Mode;

/**
 * Created by llakcs on 2017/6/8.
 */

public class LMode {

    private String Time;
    private String citycode;
    private String AddrStr;
    private String LocationDescribe;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAddrStr() {
        return AddrStr;
    }

    public void setAddrStr(String addrStr) {
        AddrStr = addrStr;
    }

    public String getLocationDescribe() {
        return LocationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        LocationDescribe = locationDescribe;
    }
}
