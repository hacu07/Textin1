package com.hacu.textin1.profileModule.model;

import android.app.Activity;
import android.net.Uri;

public interface ProfileInteractor {
    void updateUserName(String username);
    void updateImage(Activity activity, Uri uri, String oldPhotoUrl);
}
