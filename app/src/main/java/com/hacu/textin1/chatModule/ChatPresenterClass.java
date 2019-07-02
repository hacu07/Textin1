package com.hacu.textin1.chatModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.hacu.textin1.chatModule.events.ChatEvent;
import com.hacu.textin1.chatModule.model.ChatInteractor;
import com.hacu.textin1.chatModule.model.ChatInteractorClass;
import com.hacu.textin1.chatModule.view.ChatView;
import com.hacu.textin1.common.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ChatPresenterClass implements ChatPresenter {

    private ChatView mView;
    private ChatInteractor mInteractor;

    private String mFriendUid, mFriendEmail;

    public ChatPresenterClass(ChatView mView) {
        this.mView = mView;
        this.mInteractor =  new ChatInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView =  null;
    }

    @Override
    public void onPause() {
        if(mView != null){
            mInteractor.unsubscribeToFriend(mFriendUid);
            mInteractor.subscribeToMessage();
        }
    }

    @Override
    public void onResume() {
        if (mView != null){
            mInteractor.subscribeToFriend(mFriendUid, mFriendEmail);
            mInteractor.subscribeToMessage();
        }
    }

    @Override
    public void setupFriend(String uid, String email) {
        mFriendEmail = email;
        mFriendUid = uid;
    }

    @Override
    public void sendMessage(String msg) {
        if (mView != null) {
            mInteractor.sendMessage(msg);
        }
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        if (mView != null){
            mView.showProgress();
            mInteractor.sendImage(activity, imageUri);
        }
    }

    //Cuando se ha elegido una foto desde la galeria
    //se realiza la comprobacion logica aqui
    // respetando MVP
    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK){
            mView.openDialogPreview(data);
        }
    }

    @Subscribe
    @Override
    public void onEventListener(ChatEvent event) {
        if (mView != null){
            switch (event.getTypeEvent()){
                case ChatEvent.MESSAGE_ADDED:
                    mView.onMessageReceived(event.getMessage());
                    break;
                case ChatEvent.IMAGE_UPLOAD_SUCCESS:
                    mView.hideProgress();
                    break;
                case ChatEvent.GET_STATUS_FRIEND:
                    //Se pinta en la barra de estado
                    mView.onStatusUser(event.isConnected(), event.getLastConnection());
                    break;
                //Errores
                case ChatEvent.ERROR_PROCESS_DATA:
                case ChatEvent.IMAGE_UPLOAD_FAIL:
                case ChatEvent.ERROR_SERVER:
                case ChatEvent.ERROR_VOLLEY:
                    mView.hideProgress();
                    mView.onError(event.getResMsg());
                    break;
            }
        }
    }
}
