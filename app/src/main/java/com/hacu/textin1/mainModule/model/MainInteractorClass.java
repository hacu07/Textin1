package com.hacu.textin1.mainModule.model;

import com.hacu.textin1.common.Constants;
import com.hacu.textin1.common.Model.BasicEventsCallback;
import com.hacu.textin1.common.Model.dataAccess.FirebaseCloudMessagingAPI;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.mainModule.events.MainEvent;
import com.hacu.textin1.mainModule.model.dataAccess.Authentication;
import com.hacu.textin1.mainModule.model.dataAccess.RealTimeDatabase;
import com.hacu.textin1.mainModule.model.dataAccess.UserEventListener;

import org.greenrobot.eventbus.EventBus;

public class MainInteractorClass implements MainInteractor {

    private RealTimeDatabase mDatabase;
    private Authentication mAuthentication;
    //Notify
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;

    private User mMyUser = null;

    public MainInteractorClass() {
        mDatabase = new RealTimeDatabase();
        mAuthentication = new Authentication();
        mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void subscribeToUserList() {
        mDatabase.susbscribeToUserList(getCurrentUser().getUid(), new UserEventListener() {
            @Override
            public void onUserAdd(User user) {
                post(MainEvent.USER_ADDED, user);
            }

            @Override
            public void onUserUpdate(User user) {
                post(MainEvent.USER_UPDATE,user);
            }

            @Override
            public void onUserRemove(User user) {
                post(MainEvent.USER_REMOVE,user);
            }

            @Override
            public void onError(int resMsg) {
                postError(resMsg);
            }
        });

        mDatabase.subscribeToRequest(getCurrentUser().getEmail(), new UserEventListener() {
            @Override
            public void onUserAdd(User user) {
                post(MainEvent.REQUEST_ADDED,user);
            }

            @Override
            public void onUserUpdate(User user) {
                post(MainEvent.REQUEST_UPDATE,user);
            }

            @Override
            public void onUserRemove(User user) {
                post(MainEvent.REQUEST_REMOVE,user);
            }

            @Override
            public void onError(int resMsg) {
                post(MainEvent.ERROR_SERVER);
            }
        });

        //Se cambia la configuracion del usuario respecto a su conexcion
        changeConnectionStatus(Constants.ONLINE);
    }

    private void changeConnectionStatus(boolean online) {
        mDatabase.getmDatabaseAPI().updateMyLastConnection(online, getCurrentUser().getUid());
    }


    @Override
    public void unSubscribeToUserList() {
        mDatabase.unsubscribeToUser(getCurrentUser().getUid());
        mDatabase.unsubscribeRequests(getCurrentUser().getEmail());

        changeConnectionStatus(Constants.OFFLINE);
    }

    @Override
    public void signOff() {
        //NOTIFICACION
        mCloudMessagingAPI.unsubscribeToMyTopic(getCurrentUser().getEmail());
        mAuthentication.signOff();
    }

    @Override
    public User getCurrentUser() {
        return mMyUser == null? mAuthentication.getmAuthenticationAPI().getAuthUser() : mMyUser;
    }

    @Override
    public void removeFriend(String friendUid) {
        mDatabase.removeUser(friendUid, getCurrentUser().getUid(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.USER_REMOVE);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void acceptRequest(final User user) {
        mDatabase.acceptRequest(user, getCurrentUser(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.REQUEST_ACCEPTED, user);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void denyRequest(User user) {
        mDatabase.denyRequest(user, getCurrentUser().getEmail(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.REQUEST_DENIED);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    private void post(int typeEvent, User user){
        post(typeEvent,user,0);
    }

    private void postError(int resMsg) {
        post(MainEvent.ERROR_SERVER,null,resMsg);
    }


    private void post(int typeEvent) {
        post(typeEvent,null,0);
    }


    private void post(int typeEvent, User user, int resMsg) {
        MainEvent event = new MainEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);
    }
}
