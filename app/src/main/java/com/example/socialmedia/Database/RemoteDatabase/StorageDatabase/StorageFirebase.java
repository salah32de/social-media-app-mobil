package com.example.socialmedia.Database.RemoteDatabase.StorageDatabase;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageFirebase {
    private FirebaseStorage storage;
    private static final String IMAGE_PATH = "images/";
    private static final String VIDEO_PATH = "videos/";
    StorageReference Ref;

    public StorageFirebase() {
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        Ref = storage.getReference();
    }


    //upload image to firebase storage and return the download url
    public void uploadImage(Uri imageUri, UploadCallback callback) {
        String imageName = "image-" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = Ref.child(IMAGE_PATH + imageName);

        imageRef.putFile(imageUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            callback.onSuccess(uri.toString());
                        });
                    } else {
                        callback.onFailure(task.getException());
                    }
                }
        );

    }
    //upload video to firebase storage and return the download url
    public void uploadVideo(Uri videoUri, UploadCallback callback) {
        String imageName = "video-" + System.currentTimeMillis() + ".mp4";
        StorageReference imageRef = Ref.child(VIDEO_PATH + imageName);

        imageRef.putFile(videoUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            callback.onSuccess(uri.toString());
                        });
                    } else {
                        callback.onFailure(task.getException());
                    }
                }
        );

    }

    public void delete(String Uri){
        StorageReference fileRef = storage.getReferenceFromUrl(Uri);
        fileRef.delete().addOnSuccessListener(unused -> {
            // File deleted successfully
            Log.d("TAG: StorageFirebase", "File deleted successfully");
        }).addOnFailureListener(e -> {
            // error deleting file
            Log.d("TAG: StorageFirebase", "File deleted failed "+e.getMessage());
        });

    }
    public void Delete(String downloadUrl){
        StorageReference fileRef;
        String id=extractImageId(downloadUrl);

        if(id.contains("image")){
            fileRef = storage.getReference(IMAGE_PATH);
        }else {
            fileRef=storage.getReference(VIDEO_PATH);
        }
        fileRef.child(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG: Storage Firebase","delete success url:"+downloadUrl);
            }
        });
    }

    public interface UploadCallback {
        void onSuccess(String downloadUrl);

        void onFailure(Exception e);
    }
    public static String extractImageId(String url) {
        String[] parts = url.split("%2F"); // تقسيم عند "%2F" الذي يمثل "/"
        if (parts.length > 1) {
            String filename = parts[1].split("\\?")[0]; // أخذ الجزء قبل '?'
            return filename;
        }
        return null; // إذا لم يكن الرابط صحيحًا
    }
}
