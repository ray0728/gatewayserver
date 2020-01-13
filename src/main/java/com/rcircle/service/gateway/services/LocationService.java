package com.rcircle.service.gateway.services;

import com.rcircle.service.gateway.model.LocationDevice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class LocationService {
    private List<LocationDevice> mDevices;
    ScheduledExecutorService mHeartBeatChecker = Executors.newSingleThreadScheduledExecutor();
    private ReentrantLock mLock = new ReentrantLock();

    public void removeInvalidDevice() {
        mLock.lock();
        if (mDevices != null) {
            for (LocationDevice device : mDevices) {
                device.lostHeartBeat();
            }
            for (int i = mDevices.size() - 1; i >= 0; i--) {
                if (!mDevices.get(i).hasHeartBeat()) {
                    mDevices.remove(i);
                }
            }
        }
        mLock.unlock();
    }

    public void updateLocation(String name, String info, String lat, String lon) {
        mLock.lock();
        LocationDevice device = null;
        if (mDevices == null) {
            mDevices = new ArrayList<>();
            mHeartBeatChecker.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    removeInvalidDevice();
                }
            }, 1, 5, TimeUnit.SECONDS);
        }
        for (LocationDevice dev : mDevices) {
            if (dev.isSame(name)) {
                device = dev;
                device.restoreHeartBeat();
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
        mLock.unlock();
    }

    public List<LocationDevice> getDevices() {
        return mDevices;
    }

    public LocationDevice getDeviceByName(String name) {
        if (mDevices == null) {
            return null;
        }
        for (LocationDevice dev : mDevices) {
            if (dev.getName().equals(name)) {
                return dev.clone();
            }
        }
        return null;
    }
}
