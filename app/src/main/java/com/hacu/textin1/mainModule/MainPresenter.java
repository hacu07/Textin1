package com.hacu.textin1.mainModule;

import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.mainModule.events.MainEvent;

public interface MainPresenter {
    void onCreate();
    void onDestroid();
    void onPause();
    void onResume();

    void signOff();
    User getCurrentUser();
    void removeFriend(String friendUid);

    void acceptRequest(User user);
    void denyRequest(User user);

    void onEventListener(MainEvent event);
}
