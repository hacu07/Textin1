package com.hacu.textin1.loginModule.Model.dataAccess;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hacu.textin1.loginModule.events.LoginEvent;
import com.hacu.textin1.R;
import com.hacu.textin1.common.Model.EventErrorTypeListener;
import com.hacu.textin1.common.Model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.hacu.textin1.common.pojo.User;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase(){
         mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    //Registra usuario
    public void registerUser(User user){
        Map<String,Object > values = new HashMap<>();
        values.put(user.USERNAME, user.getUsername());
        values.put(user.EMAIL, user.getEmail());
        values.put(user.PHOTO_URL, user.getPhotoUrl());

        mDatabaseAPI.getUserReferenceByUid(user.getUid()).updateChildren(values);
    }

    //Verifica si el usuario existe o no
    public void checkUserExist(String uid, final EventErrorTypeListener listener){
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.EMAIL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){ //Si no existe
                            listener.onError(LoginEvent.USER_NOT_EXIST, R.string.login_error_user_exist);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Error al iniciar sesion
                        listener.onError(LoginEvent.ERROR_SERVER, R.string.login_message_error);
                    }
                });
    }
}
