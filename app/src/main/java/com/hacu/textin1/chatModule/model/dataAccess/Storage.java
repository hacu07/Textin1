package com.hacu.textin1.chatModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hacu.textin1.R;
import com.hacu.textin1.common.Model.StorageUploadImageCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseStorageAPI;

//sube una imagen en el chat
public class Storage {
    private static final String PATH_CHATS = "chats";

    private FirebaseStorageAPI mStorageAPI;

    public Storage() {
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    /**
     * Image Chat
     */
    public void uploadImageChat(Activity activity, final Uri imageUri, String myEmail,
                                final StorageUploadImageCallback callback){
        if (imageUri.getLastPathSegment() != null){
            //referencia correo/chats/nombreimg
            StorageReference photoReference = mStorageAPI.getPhotosReferenceByEmail(myEmail)
                    .child(PATH_CHATS).child(imageUri.getLastPathSegment());
            photoReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //obtiene url de descarga
                                    if (uri != null){
                                        callback.onSuccess(uri);
                                    }else{
                                        callback.onError(R.string.chat_error_imageUpload);
                                    }
                                }
                            });
                }
            });
        }
    }
}
