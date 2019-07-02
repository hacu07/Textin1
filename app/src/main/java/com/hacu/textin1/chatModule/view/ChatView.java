package com.hacu.textin1.chatModule.view;

import android.content.Intent;

import com.hacu.textin1.common.pojo.Message;

public interface ChatView {
    void showProgress();
    void hideProgress();

    void onStatusUser(Boolean connected, long lastConnection);

    void onError(int resMsg);

    void onMessageReceived(Message msg);

    void openDialogPreview(Intent data);
}
