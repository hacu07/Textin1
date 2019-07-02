package com.hacu.textin1.chatModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.hacu.textin1.chatModule.events.ChatEvent;

public interface ChatPresenter {
    void onCreate();
    void onDestroy();

    void onPause();
    void onResume();

    //Variables vitales para la suscripcion y dessuscripcion
    void setupFriend(String uid, String email);

    void sendMessage(String msg);
    void sendImage(Activity activity, Uri imageUri);

    //Cuando selecciona una imagen de la galeria
    void result(int requestCode, int resultCode, Intent data);

    void onEventListener(ChatEvent event);
}
