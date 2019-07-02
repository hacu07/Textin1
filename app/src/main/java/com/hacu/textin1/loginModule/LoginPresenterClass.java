package com.hacu.textin1.loginModule;

import android.app.Activity;
import android.content.Intent;

import com.hacu.textin1.loginModule.Model.LoginInteractor;
import com.hacu.textin1.loginModule.Model.LoginInteractorClass;
import com.hacu.textin1.loginModule.View.LoginActivity;
import com.hacu.textin1.loginModule.View.LoginView;
import com.hacu.textin1.loginModule.events.LoginEvent;
import com.hacu.textin1.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginPresenterClass implements LoginPresenter {
    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenterClass(LoginView mView) {
        this.mView = mView;
        this.mInteractor = new LoginInteractorClass();
    }

    @Override
    public void onCreate() {
        //Registra en eventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        if (setProgress()){
            mInteractor.onResume();    
        }
    }

    private boolean setProgress() {
        if (mView != null){
            mView.showProgress();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        if (setProgress()){
            mInteractor.onPause();
        }
    }

    @Override
    public void onDestroid() {
        mView = null;
        EventBus.getDefault().unregister(this);
    }

    //Determina que pasa con el resultado que lance la actividad de FirebaseUI
    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case LoginActivity.RC_SIGN_IN:
                    if(data != null){
                        mView.showLoginSuccessfully(data);
                    }
                break;
            }
        }else{
            mView.showError(R.string.login_message_error);
        }
    }

    @Override
    public void getStatusAuth() {
        if (setProgress()){
            mInteractor.getStatusAuth();
        }
    }

    @Subscribe
    @Override
    public void onEventListener(LoginEvent event) {
        if (mView != null){
            mView.hideProgress();

            switch (event.getTypeEvent()){
                case LoginEvent.STATU_AUTH_SUCCESS:
                    if (setProgress()){
                        mView.showMessageStarting();//Mensaje de inicio
                        mView.openMainActivity();   //Lanza la actividad
                    }
                    break;
                case LoginEvent.STATUS_AUTH_ERROR:
                    mView.openUILogin();    //Muestra firebase UI
                    break;

                case LoginEvent.ERROR_SERVER:
                    mView.showError(event.getResMsg());
                    break;
            }
        }
    }
}
