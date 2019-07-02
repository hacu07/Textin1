package com.hacu.textin1.mainModule.model;

import com.hacu.textin1.common.pojo.User;

public interface MainInteractor {
    void subscribeToUserList();
    void unSubscribeToUserList();

    void signOff();

    User getCurrentUser();
    void removeFriend(String friendUid);

    void acceptRequest(User user);
    void denyRequest(User user);
}
