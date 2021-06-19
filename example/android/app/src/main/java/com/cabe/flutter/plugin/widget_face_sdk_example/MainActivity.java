package com.cabe.flutter.plugin.widget_face_sdk_example;

import androidx.annotation.NonNull;
import com.cabe.lib.face.sdk.permission.PermissionHelper;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
