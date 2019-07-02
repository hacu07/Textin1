package com.hacu.textin1.loginModule;

import android.content.Intent;

import com.hacu.textin1.loginModule.events.LoginEvent;

public interface LoginPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroid();

    void result(int requestCode, int resultCode, Intent data); //Sabe que hacer cuando obtiene respuesta del onActivityResult
    //Consulta el estado de la actividad actual: Para decidir si lanzar FirebaseUI o mainActivity
    void getStatusAuth();

    void onEventListener(LoginEvent event);
}
