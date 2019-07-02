package com.hacu.textin1.profileModule.model.dataAccess;

public interface UpdateUserListener {
    void onSuccess();
    void onNotifyContacts();
    void onError(int resMsg);
}
