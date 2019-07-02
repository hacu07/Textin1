package com.hacu.textin1.common.Model.dataAccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.hacu.textin1.common.Constants;
import com.hacu.textin1.common.pojo.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRealtimeDatabaseAPI {
    public static final String SEPARATOR = "___&___"; //Saber que usuario esta con quien
    public static final String PATH_USERS = "users";
    public static final String PATH_CONTACTS = "contacts";
    public static final String PATH_REQUESTS = "requests";  //Amigo añadido como contacto

    //Variables principales

    private DatabaseReference mDatabaseReference;


    private static class SingletonHolder{
        private static final FirebaseRealtimeDatabaseAPI INSTANCE = new FirebaseRealtimeDatabaseAPI();
    }

    public static FirebaseRealtimeDatabaseAPI getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private FirebaseRealtimeDatabaseAPI(){
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /*
    *   Reference
    * */
    public DatabaseReference getRootReference(){
        return mDatabaseReference.getRoot();
    }

    public DatabaseReference getUserReferenceByUid(String uid){
        return getRootReference().child(PATH_USERS).child(uid);
    }

    public DatabaseReference getContactsReference(String uid) {
        return getUserReferenceByUid(uid).child(PATH_CONTACTS);
    }

    public DatabaseReference getRequestReference(String email) {
        return getRootReference().child(PATH_REQUESTS).child(email);
    }

    public void updateMyLastConnection(boolean online, String uid) {
        updateMyLastConnection(online, "",uid);
    }


    //Con quien estuvo conectado y cuando fue la ultima vez
    public void updateMyLastConnection(boolean online, String uidFriend, String uid) {
        String lastConnectionWith = Constants.ONLINE_VALUE + SEPARATOR + uidFriend;
        Map<String,Object> values = new HashMap<>();
        values.put(User.LAST_CONNECTION_WITH, online? lastConnectionWith : ServerValue.TIMESTAMP);
        //offline
        getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        getUserReferenceByUid(uid).updateChildren(values);

        if (online){
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect().
                    setValue(ServerValue.TIMESTAMP);
        }else{
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect().cancel();
        }
     }


}
