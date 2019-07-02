package com.hacu.textin1.profileModule.view;

import android.content.Intent;

public interface ProfileView {
    void enableUIElements();
    void DisableUIElements();
    void showProgress();
    void hideProgress();
    //Edicion de imagen
    void showProgressImage();
    void hideProgressImage();

    void showUserData(String username, String email, String photoUrl);
    void launchGallery();
    void openDialogPreview(Intent data);

    void menuEditMode();
    void menuNormalMode();

    //Eventos de carga exitosa de imagen, nombre usuario nuevo, etc.
    void saveUsernameSuccess();
    void updateImageSuccess(String photoUrl);
    void setResultOk(String username, String photoUrl);

    void onErrorUpload(int resMgs);
    void onError(int resMsg);
}
