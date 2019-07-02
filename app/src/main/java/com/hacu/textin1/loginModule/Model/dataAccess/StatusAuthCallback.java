package com.hacu.textin1.loginModule.Model.dataAccess;

import com.google.firebase.auth.FirebaseUser;

public interface StatusAuthCallback {
    //Caso exitoso: hay una sesion inicia
    void onGetUser(FirebaseUser user);
    //No hay sesion iniciada
    //Si no se puede consultar usuario de lanza interfaz de login
    void onLaunchUILogin();
}
