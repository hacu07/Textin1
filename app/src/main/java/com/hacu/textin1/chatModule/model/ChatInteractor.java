package com.hacu.textin1.chatModule.model;

import android.app.Activity;
import android.net.Uri;

public interface ChatInteractor {
    //suscribe al estado del amigo para saber si esta o no conectado
    void subscribeToFriend(String friendUID, String FriendEmail);
    void unsubscribeToFriend(String friendUID);

    void subscribeToMessage();
    void unsubscribeToMessage();

    void sendMessage(String msg);
    void sendImage(Activity activity, Uri imageUri);
}
