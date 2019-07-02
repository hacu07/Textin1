package com.hacu.textin1.chatModule.view.adapters;

import com.hacu.textin1.common.pojo.Message;

public interface OnItemClickListener {
    //termian de cargar una imagen y para usar scroll
    void onImageLoaded();
    // mensaje de tipo imagen y para vista zoom
    void onClickImage(Message message);

}
