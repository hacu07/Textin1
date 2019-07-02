package com.hacu.textin1.mainModule;

import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.mainModule.events.MainEvent;
import com.hacu.textin1.mainModule.model.MainInteractor;
import com.hacu.textin1.mainModule.model.MainInteractorClass;
import com.hacu.textin1.mainModule.view.MainView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainPresenterClass implements MainPresenter {
    private MainView mView;
    private MainInteractor mInteractor;

    public MainPresenterClass(MainView mView) {
        this.mView = mView;
        this.mInteractor =  new MainInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroid() {
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    @Override
    public void onPause() {
        if (mView != null){
            mInteractor.unSubscribeToUserList();
        }
    }

    @Override
    public void onResume() {
        if (mView != null){
            mInteractor.subscribeToUserList();
        }
    }

    @Override
    public void signOff() {
        mInteractor.unSubscribeToUserList();
        mInteractor.signOff();
        onDestroid();   //Evita error en ciclo de vida
    }

    @Override
    public User getCurrentUser() {
        return mInteractor.getCurrentUser();
    }

    @Override
    public void removeFriend(String friendUid) {
        if (mView != null){
            mInteractor.removeFriend(friendUid);
        }
    }

    @Override
    public void acceptRequest(User user) {
        if (mView != null){
            mInteractor.acceptRequest(user);
        }
    }

    @Override
    public void denyRequest(User user) {
        if (mView != null){
            mInteractor.denyRequest(user);
        }
    }

    @Subscribe
    @Override
    public void onEventListener(MainEvent event) {
        if (mView != null){
            User user = event.getUser();

            switch (event.getTypeEvent()){
                case MainEvent.USER_ADDED:
                    mView.friendAdded(user);
                    break;
                case MainEvent.USER_UPDATE:
                    mView.friendUpdate(user);
                    break;
                case MainEvent.USER_REMOVE:
                    if (user != null) {
                        mView.friendRemove(user);
                    }else{
                        mView.showFriendRemoved();
                    }
                    break;
                case MainEvent.REQUEST_ADDED:
                    mView.requestAdded(user);
                    break;
                case MainEvent.REQUEST_UPDATE:
                    mView.requestUpdate(user);
                    break;
                case MainEvent.REQUEST_REMOVE:
                    mView.requestRemove(user);
                    break;
                case MainEvent.REQUEST_ACCEPTED:
                    mView.showRequestAccepted(user.getUsername());
                    break;
                case MainEvent.REQUEST_DENIED:
                    mView.showRequestDenied();
                    break;
                case MainEvent.ERROR_SERVER:
                    mView.showError(event.getResMsg());
                    break;
            }
        }
    }
}
