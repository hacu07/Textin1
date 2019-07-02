package com.hacu.textin1.profileModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.profileModule.events.ProfileEvent;
import com.hacu.textin1.profileModule.model.ProfileInteractor;
import com.hacu.textin1.profileModule.model.ProfileInteractorClass;
import com.hacu.textin1.profileModule.view.ProfileActivity;
import com.hacu.textin1.profileModule.view.ProfileView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ProfilePresenterClass implements ProfilePresenter {
    private ProfileView mView;
    private ProfileInteractor mInteractor;

    private boolean isEdit = false;
    private User mUser;

    public ProfilePresenterClass(ProfileView mView) {
        this.mView = mView;
        this.mInteractor = new ProfileInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    /*
    * Setea la variable mUser
    * permite gestioanr la infor del usuario segun lo que en pantlla
    * y lo que devuelte a la actividad principal para que se actualiza la barra de estado
    * */
    @Override
    public void setupUser(String username, String email, String photoUrl) {
        mUser = new User();
        mUser.setUsername(username);
        mUser.setEmail(email);
        mUser.setPhotoUrl(photoUrl);

        mView.showUserData(username, email, photoUrl);
    }

    @Override
    public void checkMode() {
        if (isEdit){
            //Si dan clic sobre la imagen de perfil
            mView.launchGallery();
        }
    }

    @Override
    public void updateUserName(String username) {
        if (isEdit){
            if (setProgress()){
                //progress del nombre del usuario
                mView.showProgress();
                mInteractor.updateUserName(username);
                mUser.setUsername(username);
            }
        }else{
            isEdit = true;
            //configura el icono para guardar
            mView.menuEditMode();

            mView.enableUIElements();
        }
    }



    @Override
    public void updateImage(Activity activity, Uri uri) {
        if (setProgress()){
            mView.showProgressImage();
            mInteractor.updateImage(activity, uri, mUser.getPhotoUrl());
        }
    }

    //Si se selecciono una foto de la galeria
    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case  ProfileActivity.RC_PHOTO_PICKER:
                    //Muestra un dialogo con el previw de la imagen seleccionada
                    mView.openDialogPreview(data);
                    break;
            }
        }
    }

    @Subscribe
    @Override
    public void onEventListener(ProfileEvent event) {
        if (mView != null){
            mView.hideProgress();

            switch (event.getTypeEvent()){
                case ProfileEvent.ERROR_USERNAME:
                    mView.enableUIElements();
                    mView.onError(event.getResMsg());
                    break;
                case ProfileEvent.ERROR_PROFILE:
                case ProfileEvent.ERROR_SERVER:
                case ProfileEvent.ERROR_IMAGE:
                    mView.enableUIElements();
                    mView.onErrorUpload(event.getResMsg());
                    break;
                case ProfileEvent.SAVE_USERNAME:
                    mView.saveUsernameSuccess();
                    saveSuccess();
                    break;
                case ProfileEvent.UPLOAD_IMAGE:
                    mView.updateImageSuccess(event.getPhotoUrl());
                    mUser.setPhotoUrl(event.getPhotoUrl());
                    saveSuccess();
                    break;
            }
        }
    }

    private void saveSuccess() {
        mView.menuNormalMode();
        mView.setResultOk(mUser.getUsername(), mUser.getPhotoUrl());
        isEdit = false;
    }

    private boolean setProgress() {
        if (mView != null){
            mView.DisableUIElements();
            return true;
        }
        return false;
    }
}
