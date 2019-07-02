package com.hacu.textin1.loginModule.View;

import android.content.Intent;

public interface LoginView  {
    //Metodos convencionales
    void showProgress();
    void hideProgress();

    void openMainActivity();
    void openUILogin();
    //Respuesta que obtiene el onActivityResult desps de usar FirebaseUI
    void showLoginSuccessfully(Intent data);
    void showMessageStarting(); //Mensage de loggeo y carga de informacion
    void showError(int resMsg);
}
