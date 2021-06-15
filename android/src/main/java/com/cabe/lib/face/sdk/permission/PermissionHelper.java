package com.cabe.lib.face.sdk.permission;

import android.support.annotation.NonNull;

public class PermissionHelper {
    private static PermissionTools permissionTools;
    public static void setPermission(PermissionTools permissionTools) {
        PermissionHelper.permissionTools = permissionTools;
    }
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(permissionTools != null) permissionTools.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
