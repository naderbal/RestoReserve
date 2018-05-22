package com.example.restoreserve.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.example.restoreserve.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class FirebaseStorageProvider {

    /**
     * Uploads an image to firebase storage.
     * @param bitmap the image to be uploaded.
     */
    public static UploadTask uploadPicture(Context context, Bitmap bitmap) {
        // get a reference to store file at folder photos/filename
        String timestamp = String.valueOf(System.currentTimeMillis());
        final StorageReference photoRef = FirebaseStorage.getInstance("gs://restoreserve-8ad01.appspot.com")
                .getReference()
                .child("images")
                .child(timestamp);
        // get byte array instance
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // compress bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // upload image to firebase
        return photoRef.putBytes(baos.toByteArray());
    }
}
