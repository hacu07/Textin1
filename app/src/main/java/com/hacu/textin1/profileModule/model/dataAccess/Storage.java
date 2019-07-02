package com.hacu.textin1.profileModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hacu.textin1.R;
import com.hacu.textin1.common.Model.StorageUploadImageCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseStorageAPI;

public class Storage {
    private static final String PATH_PROFILE = "profile";
    private FirebaseStorageAPI mStorageAPI;

    public Storage(){
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    public void uploadImageProfile(Activity activity, Uri imageUri, String email, final StorageUploadImageCallback callback){
        if (imageUri.getLastPathSegment() != null){
            final StorageReference photoRef = mStorageAPI.getPhotosReferenceByEmail(email)
                    .child(PATH_PROFILE).child(imageUri.getLastPathSegment());
            //Agrega la foto
            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Si salio exitoso obtiene la url de descarga
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //Obtiene la uri
                                            if (uri != null){
                                                callback.onSuccess(uri);
                                            }else{
                                                callback.onError(R.string.profile_error_imageUpdated);
                                            }
                                        }
                                    });
                        }
                    });
        }else{
            callback.onError(R.string.profile_error_invalid_image);
        }
    }

    //Elimina imagen de perfil anterior en caso de actualizacion para no llenar el servidor
    public void deleteOldImage(String oldPhotoUrl,String downloadUrl){
        if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()){
            StorageReference storageReference = mStorageAPI.getmFirebaseStorage()
                    .getReferenceFromUrl(downloadUrl);
            StorageReference oldStorageReference =null;

            try {
                oldStorageReference = mStorageAPI.getmFirebaseStorage()
                        .getReferenceFromUrl(oldPhotoUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Valida si la nueva imagen es diferente de la antigua
            if (oldStorageReference != null &&
                    !oldStorageReference.getPath().equals(storageReference.getPath())){
                // si falla en la eliminacion se hace un reintento de borrado en storage
                 oldStorageReference.delete().addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         //codigo para realizar nuevo intento
                     }
                 });
            }
        }
    }
    /*
    public void deleteOldImage(String oldPhotoUrl,String downloadUrl){
        if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()){
            StorageReference storageReference = mStorageAPI.getmFirebaseStorage()
                    .getReferenceFromUrl(downloadUrl);
            StorageReference oldStorageReference = mStorageAPI.getmFirebaseStorage()
                    .getReferenceFromUrl(oldPhotoUrl);
            //Valida si la nueva imagen es diferente de la antigua
            if (!oldStorageReference.getPath().equals(storageReference.getPath())){
                // si falla en la eliminacion se hace un reintento de borrado en storage
                oldStorageReference.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //codigo para realizar nuevo intento
                    }
                });
            }
        }
    }
    */
}
