package com.hacu.textin1.common.Model;

import android.net.Uri;

public interface StorageUploadImageCallback {
    void onSuccess(Uri newUri);
    void onError(int resMsg);
}
