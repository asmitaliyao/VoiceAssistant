package com.example.voiceassistant.voiceassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class RequestPermission {

    public static final int REQUEST_CODE = 100000;
    private Context context;
    private String[] permissionArr;

    private static RequestPermission requestPermission = null;

    private RequestPermission(Context context, String[] permissionArr) {
        this.context = context;
        this.permissionArr = permissionArr;
    }

    public static RequestPermission getInstance(Context context, String[] permissionArr) {
        if (requestPermission == null) {
            requestPermission = new RequestPermission(context, permissionArr);
        }
        return requestPermission;
    }

    public boolean isAllGranted() {
        return checkPermissionAllGranted(permissionArr);
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions((Activity)context, permissionArr, REQUEST_CODE);
    }

}
