package com.example.jiao.demo.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

/**
 * Created by jiaojian on 2017/8/7.
 */

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    MyBinder myBinder = new MyBinder();
    private Location location;
    private boolean started;

    private ContentObserver myContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public boolean deliverSelfNotifications() {
            Log.i("info", "deliverSelfNotifications");
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            registLocationListener();
            Log.i("info", "onChange =" + selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.i("info", "onChange =" + selfChange + "Uri = " + uri.toString());
        }
    };

    public interface OnLocationChangeListener {
        void locationChange(Location location);
    }

    public class MyBinder extends Binder {

        private Location location;
        private WeakReference<OnLocationChangeListener> listener;
        int flag = 0;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
            if (listener != null) {
                OnLocationChangeListener onLocationChangeListener = listener.get();
                if (onLocationChangeListener != null)
                    onLocationChangeListener.locationChange(location);
            }
        }

        public void startLocation() {
            registLocationListener();
        }

        public void stopLocation() {
            unRegistLocationListener();
        }

        public boolean isStarted() {
            return started;
        }

        public void setLocationChangeListener(OnLocationChangeListener listener) {
            this.listener = new WeakReference<>(listener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        getContentResolver()
                .registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED),
                        false, myContentObserver);
        if (location != null && myBinder != null) {
            myBinder.setLocation(new Location(getBetterLocation(location)));
        }
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("service onCreate");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.i("service onUnbind");
        getContentResolver().unregisterContentObserver(myContentObserver);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("service onDestroy");
    }

    public void registLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Logger.i("service registLocationListener");
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, this);
            }
        } catch (Exception e) {
        }
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, this);
            }
        } catch (Exception e) {
        }
//        final Timer timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                count ++;
//                Log.i("location","location create");
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, LocationService.this);
//                if(count >= 5){
//                    timer.cancel();
//                    if(timerTask != null)
//                        timerTask.cancel();
//                }
//            }
//        };
//        timer.schedule(timerTask,200,200);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//设置经纬度的精准度
//        criteria.setAltitudeRequired(false);//设置是否需要获取海拔数据
//        criteria.setBearingRequired(false);//设置是否需要获得方向信息
//        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setCostAllowed(true); //设置是否允许定位过程中产生资费，比如流量等
//        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);//获取水平方向经纬度的精准度
//        criteria.setSpeedRequired(false);//设置速度的精确度
//        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);//设置垂直距离的海拔高度
//        criteria.setPowerRequirement(Criteria.POWER_LOW);//设置耗电量的级别
//        locationManager.requestLocationUpdates(1000,2,criteria,this,null);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null && myBinder != null) {
//            location.setLatitude(28.21577);
//            location.setLongitude(113.21038);
            myBinder.setLocation(new Location(getBetterLocation(location)));
        }
        started = true;
    }


    public void unRegistLocationListener() {
        locationManager.removeUpdates(this);
        started = false;
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.i("location","location change"+location);
//        location.setLatitude(28.21577);
//        location.setLongitude(113.21038);
        myBinder.setLocation(new Location(getBetterLocation(location)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static Location currentBestLocation;

    public static Location getBetterLocation(Location location) {
        if (isBetterLocation(location, currentBestLocation)) {
            currentBestLocation = location;
            return location;
        } else {
            return currentBestLocation;
        }
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

//        if(location.getAccuracy() > 100){
//            return false;
//        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        String provider = location.getProvider();
        String bastProvider = currentBestLocation.getProvider();
        if (bastProvider.equals(LocationManager.GPS_PROVIDER) && !provider.equals(LocationManager.GPS_PROVIDER)) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
