package com.hacu.textin1.common.Model.dataAccess;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hacu.textin1.common.utils.UtilsCommon;

//Patron singleton
public class FirebaseStorageAPI {
    private FirebaseStorage mFirebaseStorage;

    private static class Singleton{
        private static final FirebaseStorageAPI INSTANCE = new FirebaseStorageAPI();
    }

    public static FirebaseStorageAPI getInstance(){
        return Singleton.INSTANCE;
    }

    private FirebaseStorageAPI(){
        this.mFirebaseStorage = FirebaseStorage.getInstance();
    }

    public FirebaseStorage getmFirebaseStorage(){
        return mFirebaseStorage;
    }

    //En la raiz de storage se crea una carpeta por correoCodificado de cada usuario
    // alli si crean subcarpetas para imagen de perfil (en este caso) y para el chat
    public StorageReference getPhotosReferenceByEmail(String email){
        return mFirebaseStorage.getReference().child(UtilsCommon.getEmailEncoded(email));
    }
}
