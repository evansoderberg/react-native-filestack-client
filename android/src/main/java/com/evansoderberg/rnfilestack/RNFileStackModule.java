//  Created by react-native-create-bridge

package com.evansoderberg.rnfilestack;

import android.support.annotation.Nullable;
import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;
import android.net.Uri;
import android.database.Cursor;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.filestack.Client;
import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.Progress;

import io.reactivex.functions.Consumer;
import io.reactivex.Flowable;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class RNFileStackModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "RNFileStack";
    private static ReactApplicationContext reactContext = null;

    public RNFileStackModule(ReactApplicationContext context) {
        // Pass in the context to the constructor and save it so you can emit events
        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
        super(context);

        reactContext = context;
    }

    @Override
    public String getName() {
        // Tell React the name of the module
        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getConstants() {
        // Export any constants to be used in your native module
        // https://facebook.github.io/react-native/docs/native-modules-android.html#the-toast-module
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        // Context mContext = MainApplication.getAppContext();
        Context mContext = reactContext.getApplicationContext();
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void emitProgress(Double progress) {
        final WritableMap progressEvent = Arguments.createMap();
        progressEvent.putDouble("progress", progress);
        emitDeviceEvent("onProgress", progressEvent);
    }

    private void emitFinished(String fileName, String fileRef) {
        emitProgress(1.0);
        final WritableMap finishEvent = Arguments.createMap();
        finishEvent.putString("fileName", fileName);
        finishEvent.putString("fileRef", fileRef);
        emitDeviceEvent("onFinish", finishEvent);
    }

    private void emitError(String error) {
        final WritableMap errorEvent = Arguments.createMap();
        errorEvent.putString("error", error);
        emitDeviceEvent("onProgress", errorEvent);
    }

    @ReactMethod
    public void upload (String apiKey, String fileURL) {
        Config config = new Config(apiKey);
        Client client = new Client(config);

        Uri fileURI = Uri.parse(fileURL);
        String realURI = getRealPathFromURI(fileURI);

        File f = new File(realURI);
        final String fileName = f.getName();

        try {
            Flowable<Progress<FileLink>> fsUpload = client.uploadAsync(realURI, true);
            fsUpload.subscribe(new Consumer<Progress<FileLink>>() {
                @Override
                public void accept(Progress<FileLink> progress) throws Exception {
                    if (progress.getPercent() < 1.0) {
                        emitProgress(progress.getPercent());
                    }
                    if (progress.getData() != null) {
                        FileLink file = progress.getData();
                        String handle = file.getHandle();
                        emitFinished(fileName, handle);
                    }
                }
            });
        } catch (Exception e) {
            emitError(e.toString());
        }

    }

    private static void emitDeviceEvent(String eventName, @Nullable WritableMap eventData) {
        // A method for emitting from the native side to JS
        // https://facebook.github.io/react-native/docs/native-modules-android.html#sending-events-to-javascript
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
    }
}
