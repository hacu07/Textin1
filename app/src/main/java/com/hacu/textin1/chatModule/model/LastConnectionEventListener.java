package com.hacu.textin1.chatModule.model;

//Auxiliar para obtener el estado del amigo
//si esta en linea, ultima conexion, con quien esta conectado (util para notificaciones)
public interface  LastConnectionEventListener {
    void onSuccess(boolean online, long lastConnection, String uidConnectedFriend);
}
