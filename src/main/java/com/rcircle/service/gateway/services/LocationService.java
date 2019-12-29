package com.rcircle.service.gateway.services;

import com.rcircle.service.gateway.model.LocationDevice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
    private List<LocationDevice> mDevices;

    public void updateLocation(String name, String info, String lat, String lon) {
        LocationDevice device = null;
        if (mDevices == null) {
            mDevices = new ArrayList<>();
        }
        for (LocationDevice dev : mDevices) {
            if (dev.getName().equals(name)) {
                device = dev;
                break;
            }
        }
        if (device == null) {
            device = new LocationDevice();
            mDevices.add(device);
        }
        device.setName(name);
        device.setInfo(info);
        device.setLocation(lat, lon);
    }

    public List<LocationDevice> getDevices(){
        return mDevices;
    }

    public LocationDevice getDeviceByName(String name){
        if(mDevices == null){
            return null;
        }
        for(LocationDevice dev:mDevices){
            if(dev.getName().equals(name)){
                return dev.clone();
            }
        }
        return null;
    }
}
