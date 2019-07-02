package com.hacu.textin1.addModule.model.dataAccess;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.hacu.textin1.common.Model.BasicEventsCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.common.utils.UtilsCommon;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        this.mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void addFriend(String email, User myUser, final BasicEventsCallback callback){
        //Propiedades necesarias para enviar la solicitud
        //Estas propiedades la ve la persona que recibe la solicitud para identificar el emisor
        Map<String,Object> myUserMap =  new HashMap<>();
        myUserMap.put(User.USERNAME, myUser.getUsername());
        myUserMap.put(User.EMAIL, myUser.getEmail());
        myUserMap.put(User.PHOTO_URL, myUser.getPhotoUrl());

        //Codifica email del amigo ya que es la rama para agregar las solicitudes
        final String emailEncoded = UtilsCommon.getEmailEncoded(email);
        //Se ubica en las solicitudes pendientes del usuario a agrear
        DatabaseReference userReference = mDatabaseAPI.getRequestReference(emailEncoded);
        //Agrega la solicitud con los datos del usuario que envia la solcitud (Usuario actual)
        userReference.child(myUser.getUid()).updateChildren(myUserMap).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Si agrego la solicitud
                        callback.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onError();
            }
        });
    }
}
