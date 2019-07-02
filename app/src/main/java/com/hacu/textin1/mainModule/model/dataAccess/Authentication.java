package com.hacu.textin1.mainModule.model.dataAccess;

import com.hacu.textin1.common.Model.dataAccess.FirebaseAuthenticationAPI;

public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAuthenticationAPI;
    }

    public void signOff(){
        mAuthenticationAPI.getmFirebaseAuth().signOut();
    }
}
