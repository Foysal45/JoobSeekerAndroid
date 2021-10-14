package com.bdjobs.app.Utilities.camera;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;

import com.otaliastudios.cameraview.CameraView;


public class CameraFactory {

    public static CameraProvider getProvider(Context context, CameraView cameraView,
                                             PreviewView previewView, LifecycleOwner lifecycleOwner, CameraProvider.OutputCallBack callBack) {

        if (Build.VERSION.SDK_INT > 23) {
            Log.e("factory", "CameraXprovider");
            CameraXprovider prox   = new CameraXprovider(lifecycleOwner, previewView, context);
            prox.setCallback(callBack);
            return prox;

        } else {
            Camera2Provider provider = new Camera2Provider(lifecycleOwner, cameraView, context);
            provider.setCallback(callBack);
            Log.e("factory", "CameraXprovider");
            return provider;
        }
    }



}
