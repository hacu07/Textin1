package com.hacu.textin1.mainModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.hacu.textin1.R;
import com.hacu.textin1.common.Model.BasicEventsCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.common.utils.UtilsCommon;

import java.util.HashMap;
import java.util.Map;

public class RealTimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    private ChildEventListener mUserEventListener;
    private ChildEventListener mRequestEventListener;

    public RealTimeDatabase(){
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    /*
    * references
    * */

    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI() {
        return mDatabaseAPI;
    }

    private DatabaseReference getUsersReference(){
        return mDatabaseAPI.getRootReference().child(FirebaseRealtimeDatabaseAPI.PATH_USERS);
    }

    /*
    * public methods
    * */

    public void susbscribeToUserList(String myUid, final UserEventListener listener){
        if(mUserEventListener == null){
            mUserEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onUserAdd(getUser(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onUserUpdate(getUser(dataSnapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    listener.onUserRemove(getUser(dataSnapshot));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    switch (databaseError.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.main_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
        }

        mDatabaseAPI.getContactsReference(myUid).addChildEventListener(mUserEventListener);
    }

    private User getUser(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        if(user != null){
            user.setUid(dataSnapshot.getKey());
            return user;
        }
        return user;
    }

    public void subscribeToRequest(String email, final UserEventListener listener){
        if(mRequestEventListener == null){
            mRequestEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onUserAdd(getUser(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onUserUpdate(getUser(dataSnapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    listener.onUserRemove(getUser(dataSnapshot));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onError(R.string.common_error_server);
                }
            };
        }

        final String emailEncoded = UtilsCommon.getEmailEncoded(email);
        mDatabaseAPI.getRequestReference(emailEncoded).addChildEventListener(mRequestEventListener);
    }

    public void unsubscribeToUser(String uid){
        if(mUserEventListener != null){
            mDatabaseAPI.getContactsReference(uid).removeEventListener(mUserEventListener);
        }
    }

    public void unsubscribeRequests(String email){
        if (mRequestEventListener != null){
            final String emailEncoded = UtilsCommon.getEmailEncoded(email);
            mDatabaseAPI.getRequestReference(emailEncoded).removeEventListener(mRequestEventListener);
        }
    }

    /* myUid = Se ubica en la rama users del usuario que ha iniciado sesion
           PATH_CONTACTS = para a la rama de contactos del usuario
           friendUid = ubica el id del contacto a eliminar
           -para eliminar se envia el null
           -se eliminan mutuamente de la lista de contactos
           --No se elimina el chat por si en un futuro vuelve a añadirse se recupera el historial
        */
    public void removeUser(String friendUid, String myUid, final BasicEventsCallback callback){
        Map<String, Object> removeUserMap = new HashMap<>();

        removeUserMap.put(myUid+"/"+FirebaseRealtimeDatabaseAPI.PATH_CONTACTS+"/"+friendUid, null);
        removeUserMap.put(friendUid+"/"+FirebaseRealtimeDatabaseAPI.PATH_CONTACTS+"/"+myUid, null);

        getUsersReference().updateChildren(removeUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null){
                    callback.onSuccess();
                }else{
                    callback.onError();
                }
            }
        });
    }


    public void acceptRequest(User user, User myUser, final BasicEventsCallback callback){
        //Datos del contacto a agregar
        Map<String, String> userRequestMap = new HashMap<>();
        userRequestMap.put(User.USERNAME,user.getUsername());
        userRequestMap.put(User.EMAIL,user.getEmail());
        userRequestMap.put(User.PHOTO_URL,user.getPhotoUrl());
        //Datos de usuario actual
        Map<String, String> myUserMap = new HashMap<>();
        myUserMap.put(User.USERNAME,myUser.getUsername());
        myUserMap.put(User.EMAIL,myUser.getEmail());
        myUserMap.put(User.PHOTO_URL,myUser.getPhotoUrl());

        final String emailEncoded = UtilsCommon.getEmailEncoded(myUser.getEmail());

        //Sirve para insertar datos
        Map<String,Object> acceptRequest = new HashMap<>();
        //Agrega mi usuario en lista de contacto a agregar
        acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_USERS+"/"+user.getUid()+"/"+
               FirebaseRealtimeDatabaseAPI.PATH_CONTACTS+"/"+myUser.getUid(), myUserMap);
        //Agrega a contacto en mi lista
        acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_USERS+"/"+myUser.getUid()+"/"+
               FirebaseRealtimeDatabaseAPI.PATH_CONTACTS+"/"+user.getUid(), userRequestMap);
        //Elimina la solicitud para que no siga apareciendo
        acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_REQUESTS+"/"+emailEncoded+"/"+
               user.getUid(),null);

        //Raiz del firebase
        mDatabaseAPI.getRootReference().updateChildren(acceptRequest, new DatabaseReference.CompletionListener() {
           @Override
           public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if(databaseError == null){
                    callback.onSuccess();
               }else{
                   callback.onError();
               }
           }
        });
    }

    public void denyRequest(User user, String myEmail, final BasicEventsCallback callback){
        final String emailEncoded = UtilsCommon.getEmailEncoded(myEmail);

        mDatabaseAPI.getRequestReference(emailEncoded).child(user.getUid())
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            callback.onSuccess();
                        }else{
                            callback.onError();
                        }
                    }
                });
    }

}
