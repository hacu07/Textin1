package com.hacu.textin1.common.Model.dataAccess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hacu.textin1.common.pojo.User;

public class FirebaseAuthenticationAPI {
    private FirebaseAuth mFirebaseAuth;



    private static class SingletonHolder{
        private static final FirebaseAuthenticationAPI INSTANCE = new FirebaseAuthenticationAPI();
    }


    public static FirebaseAuthenticationAPI getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private FirebaseAuthenticationAPI() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getmFirebaseAuth() {
        return this.mFirebaseAuth;
    }

    public User getAuthUser() {
        User user = new User();
        if(mFirebaseAuth!=null && mFirebaseAuth.getCurrentUser() != null){
            user.setUid(mFirebaseAuth.getCurrentUser().getUid());
            user.setUsername(mFirebaseAuth.getCurrentUser().getDisplayName());
            user.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
            user.setUri(mFirebaseAuth.getCurrentUser().getPhotoUrl());
        }
        return user;
    }

    //Retorna los datos del usuario autenticado
    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }
}
