package com.m4rk3t.libcopy2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LibCopyActivity extends Activity {

    private final static String TAG = "LibCopyActivity";
    private final boolean DBG = false;
    private final String targetName = "com.archos.mediacenter.video";
    private final File targetFolder = new File(Environment.getExternalStorageDirectory(),"/Android/data/"+targetName+"/files/plugins/11/");
    private final String cpuABI;
    AssetManager am;
    private String Prefix[] = {"11/", "16/", "332/"};
    private int Version[] = {0, 420, 24900};
    InputStream in;
    FileOutputStream out;
    TextView tv;
    ProgressBar pb;

    public LibCopyActivity() {
        String abi = Build.CPU_ABI;
        if( "arm64-v8a".equals(abi) ) {
            abi = "armeabi-v7a";
        }
	if ( "x86_64".equals(abi) ) {
            abi = "x86";
        }
        cpuABI = abi;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView)findViewById(R.id.progress);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        am = getAssets();
        targetFolder.mkdirs();
    }

    @Override
    protected void onStart(){
        super.onStart();
        boolean res = true;
        int appversion = version(getApplicationContext());
        if (DBG) Log.d(TAG, "appversion " + appversion);
        String prefix = Prefix[0];
        for (int i = 0; i < Prefix.length; i++) {
            if (appversion > Version[i]) {
                prefix = Prefix[i];
            }
        }
        try {
            String[] list = am.list(prefix+cpuABI);
            for (String List : list) res &= filecopy(List, prefix);
        } catch (IOException e) {
            res = false;
        }
        pb.setVisibility(View.GONE);
        if (res) {
            tv.setText(R.string.installation_done);
            String pname = pName(getApplicationContext());
            if (! pname.isEmpty()) {
                if (DBG) Log.d(TAG, "send intent to " + pname);
                final Intent intent=new Intent();
                intent.setAction("com.archos.mediacenter.NEW_PLUGINS");
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setComponent(new ComponentName(pname,"com.archos.mediacenter.LibAvosReceiver"));
                sendBroadcast(intent);
            }
        }
    }

    private boolean filecopy(String name, String prefix){
        boolean ret = false;
        in = null;
        out = null;
        byte[] buff = new byte[512];
        int read = 0;

        try {
            File target = new File(targetFolder, name);
            in = am.open(prefix+cpuABI+"/"+name);
            out = new FileOutputStream(target);
            if (DBG) Log.d(TAG, "in  " + prefix+cpuABI+"/"+name);
            if (DBG) Log.d(TAG, "out " + target);
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            ret = true;
        } catch (IOException e) {
            tv.setText(R.string.installation_done);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    private String pName(Context ctx) {
        String pname = "";
        for (String s : Arrays.asList("", "rk", "mtk", "qc", "aw", "free", "community")) {
            try {
                PackageInfo pInfo = ctx.getPackageManager().getPackageInfo("com.archos.mediacenter.video" + s, 0);
                return pInfo.packageName;
            } catch (NameNotFoundException ignored) {}
        }
        return pname;
    }

    private int version(Context ctx) {
        int version = -1;
        for (String s : Arrays.asList("", "rk", "mtk", "qc", "aw", "free", "community")) {
            try {
                PackageInfo pInfo = ctx.getPackageManager().getPackageInfo("com.archos.mediacenter.video" + s, 0);
                return pInfo.versionCode % 100000;
            } catch (NameNotFoundException ignored) {}
        }
        return version;
    }

}
