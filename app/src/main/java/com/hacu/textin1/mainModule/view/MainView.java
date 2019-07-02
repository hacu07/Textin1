package com.hacu.textin1.mainModule.view;

import com.hacu.textin1.common.pojo.User;

public interface MainView {
    void friendAdded(User user);
    void friendUpdate(User user);
    void friendRemove(User user);

    void requestAdded(User user);
    void requestUpdate(User user);
    void requestRemove(User user);

    void showRequestAccepted(String username);
    void showRequestDenied();

    void showFriendRemoved();

    void showError(int resMsg);
}
