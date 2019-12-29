package com.rcircle.service.gateway.model;

import com.rcircle.service.gateway.utils.Base64;

public class LocationDevice {
    private String name;
    private String info;
    private long[] location;

    public String getName() {
        return Base64.decode(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long[] getLocation() {
        return location;
    }

    public void setLocation(String lat, String lon) {
        if(location == null){
            location = new long[2];
        }
        location[0] = Long.parseLong(lat);
        location[1] = Long.parseLong(lon);
    }

    private void setLocation(long lat, long lon){
        if(location == null){
            location = new long[2];
        }
        location[0] = lat;
        location[1] = lon;
    }

    public LocationDevice clone(){
        LocationDevice dev = new LocationDevice();
        dev.setName(name);
        dev.setInfo(info);
        dev.setLocation(location[0],location[1]);
        return dev;
    }
}
