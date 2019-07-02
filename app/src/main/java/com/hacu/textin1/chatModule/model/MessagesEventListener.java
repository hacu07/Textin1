package com.hacu.textin1.chatModule.model;

import com.hacu.textin1.common.pojo.Message;

//Subscripcion a los mensajes
public interface MessagesEventListener {
    // solo a recibir mensajes
    void onMessageReceived(Message message);
    void onError(int resMsg);
}
