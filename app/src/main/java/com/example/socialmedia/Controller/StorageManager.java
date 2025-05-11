package com.example.socialmedia.Controller;

import android.net.Uri;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.StorageDatabase.StorageFirebase;

//this class manager the StorageFirebase
public class StorageManager {
    //the TAG is for debug in logcat
    private final String TAG="TAG: StorageManager";
    private StorageFirebase storageFirebase;
    public StorageManager() {
        storageFirebase = new StorageFirebase();
    }

    public void uploadImage(Uri imageUri, StorageFirebase.UploadCallback callback) {
        storageFirebase.uploadImage(imageUri, new StorageFirebase.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                //upload image successfully
                Log.d(TAG,"Image Uploaded Successfully");
                callback.onSuccess(downloadUrl);
            }

            @Override
            public void onFailure(Exception e) {
                //upload image failed
                Log.d(TAG,"Image Upload Failed "+e.getMessage());
            }
        });
    }

    public void uploadVideo(Uri videoUri, StorageFirebase.UploadCallback callback) {
        storageFirebase.uploadVideo(videoUri, new StorageFirebase.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                //upload video successfully
                Log.d(TAG,"video Uploaded Successfully");
                callback.onSuccess(downloadUrl);
            }

            @Override
            public void onFailure(Exception e) {
                //upload video failed
                Log.d(TAG,"video Upload Failed "+e.getMessage());
            }
        });
    }
    public void Delete(String Uri){
        storageFirebase.Delete(Uri);
    }

}
