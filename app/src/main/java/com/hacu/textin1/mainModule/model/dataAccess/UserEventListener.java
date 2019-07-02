package com.hacu.textin1.mainModule.model.dataAccess;

import com.hacu.textin1.common.pojo.User;

public interface UserEventListener {
    void onUserAdd(User user);
    void onUserUpdate(User user);
    void onUserRemove(User user);

    void onError(int resMsg);
}
