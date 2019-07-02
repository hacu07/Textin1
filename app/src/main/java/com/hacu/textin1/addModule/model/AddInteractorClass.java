package com.hacu.textin1.addModule.model;


import com.hacu.textin1.addModule.events.AddEvent;
import com.hacu.textin1.common.Model.BasicEventsCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseAuthenticationAPI;
import com.hacu.textin1.addModule.model.dataAccess.RealtimeDatabase;

import org.greenrobot.eventbus.EventBus;

public class AddInteractorClass implements AddInteractor {
    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public AddInteractorClass() {
        this.mDatabase = new RealtimeDatabase();
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    @Override
    public void addFriend(String email) {
        mDatabase.addFriend(email, mAuthenticationAPI.getAuthUser(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                //Solicitud enviada y almacenada correctamente
                post(AddEvent.SEND_REQUEST_SUCCES);
            }

            @Override
            public void onError() {
                post(AddEvent.ERROR_SERVER);
            }
        });
    }

    private void post(int typeEvent) {
        AddEvent event = new AddEvent();
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }
}
