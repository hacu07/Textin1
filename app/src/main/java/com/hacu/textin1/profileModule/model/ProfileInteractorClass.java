package com.hacu.textin1.profileModule.model;

import android.app.Activity;
import android.net.Uri;

import com.hacu.textin1.common.Model.EventErrorTypeListener;
import com.hacu.textin1.common.Model.StorageUploadImageCallback;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.profileModule.events.ProfileEvent;
import com.hacu.textin1.profileModule.model.dataAccess.Authentication;
import com.hacu.textin1.profileModule.model.dataAccess.RealtimeDatabase;
import com.hacu.textin1.profileModule.model.dataAccess.Storage;
import com.hacu.textin1.profileModule.model.dataAccess.UpdateUserListener;

import org.greenrobot.eventbus.EventBus;

public class ProfileInteractorClass implements ProfileInteractor {
    private Authentication mAuthentication;
    private RealtimeDatabase mDatabase;
    private Storage mStorage;
    private User mMyUser;

    public ProfileInteractorClass() {
        mAuthentication = new Authentication();
        mDatabase = new RealtimeDatabase();
        mStorage = new Storage();
    }

    private User getCurrentUser(){
        if (mMyUser == null){
            mMyUser = mAuthentication.getmAuthenticationAPI().getAuthUser();
        }
        return mMyUser;
    }

    @Override
    public void updateUserName(String username) {
        //cambia el nombre del usuario en RealtimeDatabase y en el perfil de Firebase
        final User myUser = getCurrentUser();
        myUser.setUsername(username);

        mDatabase.changeUsername(myUser, new UpdateUserListener() {
            @Override
            public void onSuccess() {
                mAuthentication.updateUsernameFirebaseProfile(myUser, new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        //Si hay error se postea
                        post(typeEvent, null, resMsg);
                    }
                });
            }

            @Override
            public void onNotifyContacts() {
                postUsernameSuccess();
            }

            @Override
            public void onError(int resMsg) {
                post(ProfileEvent.ERROR_USERNAME, null, resMsg);
            }
        });
    }

    //Cambia imagen de perfil
    @Override
    public void updateImage(Activity activity, Uri uri, final String oldPhotoUrl) {
        //Se sube a storage
        mStorage.uploadImageProfile(activity, uri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri uri) {
                //La uri se utiliza para database
                // se inserta dentro del mismo usuario y a su vez se actualiza la uri en los contactos
                mDatabase.updatePhotoUrl(uri, getCurrentUser().getUid(), new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {

                        post(ProfileEvent.UPLOAD_IMAGE, newUri.toString(),0);
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ProfileEvent.ERROR_SERVER, resMsg);
                    }
                });

                // Actualiza en mAuthentication - cambia las propiedades del usuario que se creo
                // cuando se loggeo con firebase
                mAuthentication.updateImageFirebaseProfile(uri, new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        mStorage.deleteOldImage(oldPhotoUrl, newUri.toString());
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ProfileEvent.ERROR_PROFILE, resMsg);
                    }
                });

            }

            @Override
            public void onError(int resMsg) {
                post(ProfileEvent.ERROR_IMAGE, resMsg);
            }
        });

    }

    private void post(int typeEvent, int resMsg) {
        post(typeEvent, null, resMsg);
    }

    private void postUsernameSuccess() {
        post(ProfileEvent.SAVE_USERNAME, null, 0);
    }

    private void post(int typeEvent, String photoUrl, int resMsg) {
        ProfileEvent event = new ProfileEvent();
        event.setPhotoUrl(photoUrl);
        event.setResMsg(resMsg);
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }


}
