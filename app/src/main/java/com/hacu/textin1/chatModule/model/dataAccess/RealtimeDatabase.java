package com.hacu.textin1.chatModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hacu.textin1.R;
import com.hacu.textin1.chatModule.model.LastConnectionEventListener;
import com.hacu.textin1.chatModule.model.MessagesEventListener;
import com.hacu.textin1.chatModule.model.SendMessageListener;
import com.hacu.textin1.common.Constants;
import com.hacu.textin1.common.Model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.hacu.textin1.common.pojo.Message;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.common.utils.UtilsCommon;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabase {
    //Constantes para acceder a los nodos
    private static final String PATH_CHATS = "chats";
    private static final String PATH_MESSAGES = "messages";

    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    //Mensajes que se reciben
    private ChildEventListener mMessageEventListener;
    //Estado del amigo
    private ValueEventListener mFriendProfileListener;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI() {
        return mDatabaseAPI;
    }

    public void subscribeToMessage(String myEmail, String friendEmail, final MessagesEventListener listener){
        if (mMessageEventListener == null){
            mMessageEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Solo este metodo para estar pendiente de los mensajes
                    listener.onMessageReceived(getMessage(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    switch (databaseError.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.chat_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
            //vincula a firebase
            getChatsMessagesReference(myEmail,friendEmail).addChildEventListener(mMessageEventListener);
        }

    }

    // cada referencia se debe realizar en un metodo aparte (buena practica)
    private DatabaseReference getChatsMessagesReference(String myEmail, String friendEmail) {
        ////SE UBICA EN CHATS/keyElaboradaDeLosCorreos/mensajes
        return getChatReference(myEmail, friendEmail).child(PATH_MESSAGES);
    }

    private DatabaseReference getChatReference(String myEmail, String friendEmail) {
        String myEmailEncoded = UtilsCommon.getEmailEncoded(myEmail);
        String friendEmailEncoded = UtilsCommon.getEmailEncoded(friendEmail);

        //nodo unico para dos personas a partir de su correo
        String keyChat = myEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + friendEmailEncoded;

        //orderna alfabeticamente los correos
        //estandar para acceder al nodo sin importar quien inicie la conversacion
        if (myEmailEncoded.compareTo(friendEmailEncoded) > 0 ){
            keyChat = friendEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + myEmailEncoded;
        }
        //SE UBICA EN CHATS/keyElaboradaDeLosCorreos
        return mDatabaseAPI.getRootReference().child(PATH_CHATS).child(keyChat);
    }

    private Message getMessage(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);

        if (message != null){
            message.setUid(dataSnapshot.getKey());
        }
        return message;
    }

    public void unsubscribeToMessage(String myEmail, String frienEmail){
        if (mMessageEventListener != null){
            getChatsMessagesReference(myEmail,frienEmail).removeEventListener(mMessageEventListener);
        }
    }

    //suscribe al status del amigo
    public void subscribeToFriend(String uid, final LastConnectionEventListener listener){
        if (mFriendProfileListener == null){
            //Si cambia el estado del amigo: se conecta, desconecta, etc.
            mFriendProfileListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long lastConnectionFriend = 0;
                    String uidConnectedFriend = "";
                    //Extrae los datos
                    try{
                        //CASO 1
                        //extrae la ultima conexion
                        Long value = dataSnapshot.getValue(Long.class);
                        if(value != null){
                            lastConnectionFriend = value;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        //CASO 2
                        //Separa con quien se conecto y cuando fue la ultima conexion gracias al metodo split
                        String lastConnectionWith = dataSnapshot.getValue(String.class);
                        if (lastConnectionWith != null && !lastConnectionWith.isEmpty()){
                            String[] values = lastConnectionWith.split(FirebaseRealtimeDatabaseAPI.SEPARATOR);
                            if (values.length > 0){
                                lastConnectionFriend = Long.valueOf(values[0]);
                                if (values.length > 1){
                                    uidConnectedFriend = values[1];
                                }
                            }
                        }
                    }

                    listener.onSuccess(lastConnectionFriend == Constants.ONLINE_VALUE,
                            lastConnectionFriend, uidConnectedFriend);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
        }


        //offline
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH)
                .addValueEventListener(mFriendProfileListener);
    }

    public void unsubscribeToFriend(String uid){
        if (mFriendProfileListener != null){
            //finaliza con el estatus del amigo que estaba chateando
            mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH)
                    .removeEventListener(mFriendProfileListener);
        }
    }

    //Numero de mensajes leidos por nosotros
    /*
    * READ/UNREAD MESSAGES
    * */
    public void setMessagesRead(String myUid, String frienUid){
        final DatabaseReference userReference = getOneContactReference(myUid, frienUid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class);
                if (user != null){
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(User.MESSAGES_UNREAD, 0);
                    userReference.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    //accdemos a nuestro usuarios/contactos/contactoamigo para resetear a cero el contador de mensajes
    private DatabaseReference getOneContactReference(String uidMain, String uidChild) {
        return mDatabaseAPI.getUserReferenceByUid(uidMain).child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS)
                .child(uidChild);
    }

    //Suma mensajes no leido al contacto que se le envia los mensajes y no esta en chat con mi usuario
    public void sumUnreadMessages(String myUid, String friendUid){
        //accede al perfil del amigo/contactos/ mi contacto
        //para que se puedan ir incrementando los mensajes no leidos en su listado
        final DatabaseReference userReference = getOneContactReference(friendUid,myUid);
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                //todos aquellos que quieran afectar a un mismo dato deben esperar
                // a que uno termine para que el otro pueda actuar
                User user = mutableData.getValue(User.class); // Info del contacto
                if (user == null){
                    return Transaction.success(mutableData);
                }
                //Incrementa los mensajes no leidos
                user.setMessagesUnread(user.getMessagesUnread()+1);
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                //si verifica que fue o no aplicado con exito se debe basar en la variable boolean b
                // si es true es porque se realizo el cambio con exito
            }
        });
    }

    /*
    *   SEND MESSAGE
    * */

    public void sendMessage(String msg, String photoUrl, String friendEmail, User myUser,
                            final SendMessageListener listener){
        //arma mensaje
        Message message =  new Message();
        message.setSender(myUser.getEmail()); //quien envia
        message.setMsg(msg);                  //texto
        message.setPhotoUrl(photoUrl);

        //lo envia
        DatabaseReference chatReference = getChatsMessagesReference(myUser.getEmail(), friendEmail);
        chatReference.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null){
                    //no existio error
                    listener.onSuccess();
                }
            }
        });
    }
}
