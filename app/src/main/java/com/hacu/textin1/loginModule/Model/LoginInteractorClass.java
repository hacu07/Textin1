package com.hacu.textin1.loginModule.Model;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.hacu.textin1.common.Model.dataAccess.FirebaseCloudMessagingAPI;
import com.hacu.textin1.loginModule.Model.dataAccess.Authentication;
import com.hacu.textin1.loginModule.Model.dataAccess.RealtimeDatabase;
import com.hacu.textin1.loginModule.Model.dataAccess.StatusAuthCallback;
import com.hacu.textin1.loginModule.events.LoginEvent;
import com.hacu.textin1.common.Model.EventErrorTypeListener;
import com.hacu.textin1.common.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class LoginInteractorClass implements LoginInteractor {
    private Authentication mAuthetication;
    private RealtimeDatabase mDatabase;
    //Notificacion
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;

    public LoginInteractorClass() {
        this.mAuthetication = new Authentication();
        this.mDatabase = new RealtimeDatabase();
        //Notificacion
        this.mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void onResume() {
        //Asigna listener
        mAuthetication.onResume();
    }

    @Override
    public void onPause() {
        //Remueve el listener
        mAuthetication.onPause();
    }

    //Se registro correctamente e inicia sesion
    @Override
    public void getStatusAuth() {
        Log.i("flag", "getStatusAuth: ");
        mAuthetication.getStatusAuth(new StatusAuthCallback() {
            @Override
            public void onGetUser(FirebaseUser user) {
                //consulto el usuario correctamente
                post(LoginEvent.STATU_AUTH_SUCCESS, user);
                //Verifica si existe en database
                mDatabase.checkUserExist(mAuthetication.getCurrentUser().getUid(), new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        if (typeEvent == LoginEvent.USER_NOT_EXIST){ //No existe el usuario
                            //Registra en rama USER
                            registerUser();
                        }else{
                            post(typeEvent);
                        }
                    }
                });

                //Suscribe al topico para envio de notificaciones
                //se subscribe al propio correo
                //todos los demas que nos tengan agregados como contactos utilizan ese topic
                mCloudMessagingAPI.subscribeToMyTopic(user.getEmail());
            }

            @Override
            public void onLaunchUILogin() {
                // No hay sesion iniciada
                //Error-se lanza interfaz de firebase UI
                post(LoginEvent.STATUS_AUTH_ERROR);
            }
        });
    }

    private void registerUser() {
        User currentUser = mAuthetication.getCurrentUser();
        mDatabase.registerUser(currentUser);
    }

    private void post(int typeEvent) {
        post(typeEvent,null);
    }

    private void post(int typeEvent, FirebaseUser user) {
        LoginEvent event =  new LoginEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        EventBus.getDefault().post(event);
    }
}
