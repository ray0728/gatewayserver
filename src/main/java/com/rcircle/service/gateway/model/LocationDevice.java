package com.rcircle.service.gateway.model;

import com.rcircle.service.gateway.utils.Base64;

public class LocationDevice {
    private static final int MAX_HEARTBEAT = 10;
    private String name;
    private String info;
    private int heartbeat = MAX_HEARTBEAT;
    private double[] location;

    public String getName() {
        return Base64.decode(name);
    }

    public String getLabel(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return Base64.decode(info);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(String lat, String lon) {
        if (location == null) {
            location = new double[2];
        }
        location[0] = Double.parseDouble(lat);
        location[1] = Double.parseDouble(lon);
    }

    private void updateLocation(double lat, double lon) {
        if (location == null) {
            location = new double[2];
        }
        location[0] = lat;
        location[1] = lon;
    }

    public LocationDevice clone() {
        LocationDevice dev = new LocationDevice();
        dev.setName(name);
        dev.setInfo(info);
        dev.updateLocation(location[0], location[1]);
        return dev;
    }

    public void restoreHeartBeat() {
        heartbeat = MAX_HEARTBEAT;
    }

    public boolean hasHeartBeat() {
        return heartbeat > 0 ? true : false;
    }

    public void lostHeartBeat() {
        if (heartbeat > 0) {
            heartbeat--;
        }
    }
    
    public boolean isSame(String dst){
        return name.equals(dst);
    }
}
