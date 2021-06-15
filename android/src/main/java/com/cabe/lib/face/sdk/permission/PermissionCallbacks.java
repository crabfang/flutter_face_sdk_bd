package com.cabe.lib.face.sdk.permission;

import java.util.List;

/**
 * Created by Xiamin on 2017/1/24.
 */

public interface PermissionCallbacks {
    /** request successful list */
    void onPermissionsGranted(int requestCode, List<String> perms);

    /** request denied list */
    void onPermissionsDenied(int requestCode, List<String> perms);
}