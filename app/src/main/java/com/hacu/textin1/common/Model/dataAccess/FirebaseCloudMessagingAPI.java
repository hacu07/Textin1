package com.hacu.textin1.common.Model.dataAccess;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hacu.textin1.common.utils.UtilsCommon;

//  Importante para notificaciones
public class FirebaseCloudMessagingAPI {
    private FirebaseMessaging mFirebaseMessaging;

    private static class SingletonHolder{
        private static final FirebaseCloudMessagingAPI INSTANCE = new FirebaseCloudMessagingAPI();
    }

    public static FirebaseCloudMessagingAPI getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public FirebaseCloudMessagingAPI() {
        this.mFirebaseMessaging = FirebaseMessaging.getInstance();
    }

    //Methods

    //subscribe para recibir notificaciones en login
    public void subscribeToMyTopic(String myEmail){
        mFirebaseMessaging.subscribeToTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //usuario sigue usando la aplicacion
                        //Si existe un error, se notifica al usuario y reintentarlo (programacion interna para el reenvio)
                        if (!task.isSuccessful()){

                        }
                    }
                });
    }

    public void unsubscribeToMyTopic(String myEmail){
        mFirebaseMessaging.unsubscribeFromTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            //reintentar
                            //esta cerrando una sesion
                        }
                    }
                });
    }
}
