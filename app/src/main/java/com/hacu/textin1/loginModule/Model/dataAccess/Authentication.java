package com.hacu.textin1.loginModule.Model.dataAccess;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hacu.textin1.common.Model.dataAccess.FirebaseAuthenticationAPI;
import com.hacu.textin1.common.pojo.User;

/*
    Uso similar a la clase RealTime Database en singleton del proyecto inventario
 */
public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

     public Authentication(){
         mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
     }

     public void onResume(){
         mAuthenticationAPI.getmFirebaseAuth().addAuthStateListener(mAuthStateListener);
     }

     public void onPause(){
         if(mAuthStateListener != null){
            mAuthenticationAPI.getmFirebaseAuth().removeAuthStateListener(mAuthStateListener);
         }
     }


     public void getStatusAuth(final StatusAuthCallback callback){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){ //Hay sesion activa
                    callback.onGetUser(user);
                }else{
                    callback.onLaunchUILogin();
                }
            }
        };
     }

     public User getCurrentUser(){
        return mAuthenticationAPI.getAuthUser();
     }
}
