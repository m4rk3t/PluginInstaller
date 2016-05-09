package com.m4rk3t.libcopy2;

import android.app.Activity;
import android.content.Intent;
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

public class LibCopyActivity extends Activity {

    private final static String TAG = "LibCopyActivity";
    private final boolean DBG = false;
    private final String targetName = "com.archos.mediacenter.video";
    private final File targetFolder = new File(Environment.getExternalStorageDirectory(),"/Android/data/"+targetName+"/files/plugins/11/");
    private final String cpuABI;
    AssetManager am;
    String aPrefix = "11/";
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

    protected void onStart(){
        super.onStart();
        boolean res = true;
        try {
            String[] list = am.list(aPrefix+cpuABI);
            int size = list.length;
            for (int i = 0 ; i < size ; i++)
                res &= filecopy(list[i]);
        } catch (IOException e) {
            res = false;
        }
        pb.setVisibility(View.GONE);
        if (res) {
            tv.setText(R.string.installation_done);
            sendBroadcast(new Intent("com.archos.mediacenter.NEW_PLUGINS"));
        }
    }

    private boolean filecopy(String name){
        boolean ret = false;
        in = null;
        out = null;
        byte[] buff = new byte[512];
        int read = 0;

        try {
            File target = new File(targetFolder, name);
            in = am.open(aPrefix+cpuABI+"/"+name);
            out = new FileOutputStream(target);
            if (DBG) Log.d(TAG, "in  " + aPrefix+cpuABI+"/"+name);
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
}
