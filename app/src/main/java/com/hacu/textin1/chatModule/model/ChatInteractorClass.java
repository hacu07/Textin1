package com.hacu.textin1.chatModule.model;

import android.app.Activity;
import android.net.Uri;

import com.hacu.textin1.chatModule.events.ChatEvent;
import com.hacu.textin1.chatModule.model.dataAccess.NotificationRS;
import com.hacu.textin1.chatModule.model.dataAccess.RealtimeDatabase;
import com.hacu.textin1.chatModule.model.dataAccess.Storage;
import com.hacu.textin1.common.Constants;
import com.hacu.textin1.common.Model.EventErrorTypeListener;
import com.hacu.textin1.common.Model.StorageUploadImageCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseAuthenticationAPI;
import com.hacu.textin1.common.pojo.Message;
import com.hacu.textin1.common.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class ChatInteractorClass implements ChatInteractor {

    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAutenthicationAPI;
    private Storage mStorage;
    //Notificacion
    private NotificationRS mNotification;

    private User mMyUser;
    private String mFriendUid;
    private String mFriendEmail;

    private long mLastConnectionFriend;
    private String mUidConnectedFriend = "";

    public ChatInteractorClass() {
        this.mDatabase = new RealtimeDatabase();
        this.mAutenthicationAPI =  FirebaseAuthenticationAPI.getInstance();
        this.mStorage = new Storage();
        this.mNotification =  new NotificationRS();
    }

    private User getCurrentUser(){
        if (mMyUser == null){
            mMyUser = mAutenthicationAPI.getAuthUser();
        }
        return mMyUser;
    }

    //accede al status actual para saber si esta o no conectado con nosotros
    //y cuando fue su ultima conexion
    @Override
    public void subscribeToFriend(String friendUID, String friendEmail) {
        //Setea las variables globales
        this.mFriendEmail = friendEmail;
        this.mFriendUid = friendUID;
        mDatabase.subscribeToFriend(friendUID, new LastConnectionEventListener() {
            @Override
            public void onSuccess(boolean online, long lastConnection, String uidConnectedFriend) {
                //Respuesta con los valores del estado actual del amigo
                postStatusFriend(online, lastConnection);
                //actualiza con quien esta conectado
                mUidConnectedFriend = uidConnectedFriend;
                mLastConnectionFriend = lastConnection;
            }
        });

        //Avisa que ha leido los mensajes del usuario actual con el que se ha conectado
        //mensales sin leer = 0
        mDatabase.setMessagesRead(getCurrentUser().getUid(), friendUID);
    }

    @Override
    public void unsubscribeToFriend(String friendUID) {
        mDatabase.unsubscribeToFriend(friendUID);
    }

    @Override
    public void subscribeToMessage() {
        mDatabase.subscribeToMessage(getCurrentUser().getEmail(), mFriendEmail,
                new MessagesEventListener() {
                    @Override
                    public void onMessageReceived(Message message) {
                        //Una vez el mensaje a sido recibido
                        //quien envio el mensaje
                        String msgSender = message.getSender();
                        //Compara si coincide con mi correo para ver si se ha enviado por mi usuario
                        message.setSentByMe(msgSender.equals(getCurrentUser().getEmail()));
                        postMessage(message);
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ChatEvent.ERROR_SERVER,resMsg);
                    }
                });
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.ONLINE,mFriendUid,getCurrentUser().getUid());
    }

    @Override
    public void unsubscribeToMessage() {
        mDatabase.unsubscribeToMessage(getCurrentUser().getEmail(), mFriendEmail);
        //Actualiza  ultima conexion - offline
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.OFFLINE, getCurrentUser().getUid());
    }

    //Envia mensaje: texto o imagen
    @Override
    public void sendMessage(String msg) {
        sendMessage(msg, null);
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        //sube la imagen
        mStorage.uploadImageChat(activity, imageUri, getCurrentUser().getEmail(),
                new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        //Obtiene la uri que es la url de descarga de la imagen subida a storage
                        sendMessage(null, newUri.toString());
                        postUploadSucces();
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ChatEvent.IMAGE_UPLOAD_FAIL, resMsg);
                    }
                });
    }

    //****************************
    private void sendMessage(final String msg, String photoUrl){
        mDatabase.sendMessage(msg, photoUrl, mFriendEmail, getCurrentUser(),
                new SendMessageListener() {
                    @Override
                    public void onSuccess() {
                        //Se ha enviado correctamente el mensaje

                        //Evalua si se debe incrementar el numero de mensajes leido o no
                        //por el usuario
                        if (!mUidConnectedFriend.equals(getCurrentUser().getUid())){ // Si no esta conectado conmigo
                            //aumenta el numero de mensajes no leidos de mi parte
                            mDatabase.sumUnreadMessages(getCurrentUser().getUid(), mFriendUid);
                            // TODO: 18/05/2019 notify
                            if(mLastConnectionFriend != Constants.ONLINE_VALUE){
                                //Notificacion
                                mNotification.sendNotification(getCurrentUser().getUsername(),
                                        msg, mFriendEmail, getCurrentUser().getUid(),
                                        getCurrentUser().getEmail(), getCurrentUser().getUri(),
                                        new EventErrorTypeListener() {
                                            @Override
                                            public void onError(int typeEvent, int resMsg) {
                                                // si se envio correctamente no se hace nada
                                                //  se notificica solo si hubo error
                                                post(typeEvent, resMsg);
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    /*
    *   POST
    * */

    private void postUploadSucces() {
        post(ChatEvent.IMAGE_UPLOAD_SUCCESS, 0, null, false, 0);
    }

    private void post(int typeEvent, int resMsg){
        post(typeEvent,resMsg,null,false,0);
    }

    private void postMessage(Message message){
        post(ChatEvent.MESSAGE_ADDED,0,message,false, 0);
    }

    private void postStatusFriend(boolean online, long lastConnection) {
        post(ChatEvent.GET_STATUS_FRIEND, 0, null, online, lastConnection);
    }

    private void post(int typeEvent, int resMsg, Message message, boolean online, long lastConnection) {
        ChatEvent event = new ChatEvent();
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        event.setMessage(message);
        event.setConnected(online);
        event.setLastConnection(lastConnection);

        EventBus.getDefault().post(event);
    }
}
