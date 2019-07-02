package com.hacu.textin1.addModule;

import com.hacu.textin1.addModule.events.AddEvent;

public interface AddPresenter {
    //Metodos del ciclo de vida del dialogo
    void onShow(); //Suscribe a eventbus
    void onDestroy();

    void addFriend(String Email);
    void onEventListener(AddEvent event);
}
