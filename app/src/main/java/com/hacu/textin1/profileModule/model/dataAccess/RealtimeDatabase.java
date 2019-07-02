package com.hacu.textin1.profileModule.model.dataAccess;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hacu.textin1.R;
import com.hacu.textin1.common.Model.StorageUploadImageCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.hacu.textin1.common.pojo.User;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        mDatabaseAPI =  FirebaseRealtimeDatabaseAPI.getInstance();
    }

    //Metodos propios

    public void changeUsername(final User myUser, final UpdateUserListener listener){
        //Si existe el usuario
        if (mDatabaseAPI.getUserReferenceByUid(myUser.getUid()) != null){
            Map<String, Object> updates = new HashMap<>();
            updates.put(User.USERNAME, myUser.getUsername());

            //Actualiza el nombre del usuario
            mDatabaseAPI.getUserReferenceByUid(myUser.getUid()).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Se cambio el nombre del usuario en la rama de usuarios
                            listener.onSuccess();

                            //Notifica a los usuarios(contactos) que se cambio el usuario
                            notifyContactsUsername(myUser,listener);
                        }
                    });

        }
    }

    //Notifica a todos los contactos que se ha cambiado el nombre de este usuarios
    private void notifyContactsUsername(final User myUser, final UpdateUserListener listener) {
        mDatabaseAPI.getContactsReference(myUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Ya se tiene listado de contactos a notificar
                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            String friendUid = child.getKey();
                            //Obtiene la referencia del contacto a actualizar el cambio
                            DatabaseReference reference = getContactsReference(friendUid, myUser.getUid() );
                            Map<String,Object>  updates = new HashMap<>();
                            updates.put(User.USERNAME, myUser.getUsername());
                            //Actualiza el nombre en la lista del contacto
                            reference.updateChildren(updates);
                        }
                        listener.onNotifyContacts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(R.string.profile_error_userUpdated);
                    }
                });
    }

    private DatabaseReference getContactsReference(String mainUid, String childUid) {
        //en usuarios, ubica el amigo y el contacto(Este contacto que acaba de cambiar el nombre)
        return mDatabaseAPI.getUserReferenceByUid(mainUid)
                .child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS).child(childUid);
    }

    public void updatePhotoUrl(final Uri downloadUri, final String myUid, final StorageUploadImageCallback callback){
        if (mDatabaseAPI.getUserReferenceByUid(myUid) != null){
            Map<String,Object> updates = new HashMap<>();
            updates.put(User.PHOTO_URL,downloadUri.toString());
            mDatabaseAPI.getUserReferenceByUid(myUid).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            callback.onSuccess(downloadUri);
                            notifyContactsPhoto(downloadUri.toString(), myUid, callback);
                        }
                    });
        }
    }

    private void notifyContactsPhoto(final String photoUrl, final String myUid, final StorageUploadImageCallback callback) {
        mDatabaseAPI.getContactsReference(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Ya se tiene listado de contactos a notificar
                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            String friendUid = child.getKey();
                            //Obtiene la referencia del contacto a actualizar el cambio
                            DatabaseReference reference = getContactsReference(friendUid, myUid );
                            Map<String,Object>  updates = new HashMap<>();
                            updates.put(User.PHOTO_URL, photoUrl);
                            //Actualiza el nombre en la lista del contacto
                            reference.updateChildren(updates);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(R.string.profile_error_imageUpdated);
                    }
                });
    }
}
