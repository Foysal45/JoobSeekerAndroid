package com.bdjobs.app.Utilities.camera;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;

import com.otaliastudios.cameraview.CameraView;

import timber.log.Timber;


public class CameraFactory {

    public static CameraProvider getProvider(Context context, CameraView cameraView,
                                             PreviewView previewView, LifecycleOwner lifecycleOwner, CameraProvider.OutputCallBack callBack) {

        if (Build.VERSION.SDK_INT > 23) {
            Timber.e("CameraXProvider");
            CameraXprovider prox   = new CameraXprovider(lifecycleOwner, previewView, context);
            prox.setCallback(callBack);
            return prox;

        } else {
            Camera2Provider provider = new Camera2Provider(lifecycleOwner, cameraView, context);
            provider.setCallback(callBack);
            Timber.e("CameraXProvider");
            return provider;
        }
    }



}
