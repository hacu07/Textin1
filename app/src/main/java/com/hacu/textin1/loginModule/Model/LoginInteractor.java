package com.hacu.textin1.loginModule.Model;

public interface LoginInteractor {
    //Buena practica
    //Añade y remueve el listener para la autentificacion
    void onResume();
    void onPause();

    void getStatusAuth();
}
