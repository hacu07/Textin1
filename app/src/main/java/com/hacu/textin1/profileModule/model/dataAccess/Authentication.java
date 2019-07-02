package com.hacu.textin1.profileModule.model.dataAccess;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.hacu.textin1.R;
import com.hacu.textin1.common.Model.EventErrorTypeListener;
import com.hacu.textin1.common.Model.StorageUploadImageCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseAuthenticationAPI;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.profileModule.events.ProfileEvent;

public class Authentication {
    private FirebaseAuthenticationAPI mAutheticationAPI;

    public Authentication() {
        this.mAutheticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAutheticationAPI;
    }

    public void updateUsernameFirebaseProfile(User myUser, final EventErrorTypeListener listener){
        FirebaseUser user = mAutheticationAPI.getCurrentUser();

        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(myUser.getUsername())
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){//hubo error
                        listener.onError(ProfileEvent.ERROR_PROFILE, R.string.profile_error_userUpdated);
                    }
                }
            });
        }
    }

    public void updateImageFirebaseProfile(final Uri downloadUri, final StorageUploadImageCallback callback){
        FirebaseUser user = mAutheticationAPI.getCurrentUser();

        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){//hubo error
                        callback.onSuccess(downloadUri);
                    }else{
                        callback.onError(R.string.profile_error_imageUpdated);
                    }
                }
            });
        }
    }
}
