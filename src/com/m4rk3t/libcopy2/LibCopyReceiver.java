package com.m4rk3t.libcopy2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LibCopyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String targetName = intent.getDataString();
        if (targetName.startsWith("package:com.archos.mediacenter.video") ||
            targetName.startsWith("package:com.m4rk3t.libcopy2") ) {
            Intent i = new Intent();
            i.setClassName("com.m4rk3t.libcopy2", "com.m4rk3t.libcopy2.LibCopyActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
