package com.hacu.textin1.mainModule.view.adapters;

import com.hacu.textin1.common.pojo.User;

public interface OnItemClickListener {
    //Solicitudes y amigos que se han agregado
    void onItemClick(User user);
    void onItemLongClick(User user);

    void onAcceptRequest(User user);
    void onDenyRequest(User user);
}
