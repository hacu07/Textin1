package com.hacu.textin1.chatModule.model.dataAccess;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hacu.textin1.R;
import com.hacu.textin1.TextingApplication;
import com.hacu.textin1.chatModule.events.ChatEvent;
import com.hacu.textin1.common.Constants;
import com.hacu.textin1.common.Model.EventErrorTypeListener;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.common.utils.UtilsCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//Notification remote service
//Envia notificacion usando volley con metodo de transporte de datos en JSON
//al archivo php en servidor donde alli realiza el envio de la notificacion al servidor de firebase
public class NotificationRS {
    //Direccion URL
    private static final String TEXTING_RS = "https://texting-firebase.000webhostapp.com/texting/dataAccess/TextingRS.php";
    //Metodo en php
    private static final String SEND_NOTIFICATION = "sendNotification";

    //Peticion al servicio remoto
    public void sendNotification(String title, String message, String email, String uid, String myEmail,
                                 Uri photoUrl, final EventErrorTypeListener listener){
        //Objeto json con parametros a enviar
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.METHOD, SEND_NOTIFICATION);
            params.put(Constants.TITLE, title);
            params.put(Constants.MESSAGE, message);
            params.put(Constants.TOPIC, UtilsCommon.getEmailToTopic(email));
            params.put(User.UID, uid);
            params.put(User.EMAIL, myEmail);
            params.put(User.PHOTO_URL, photoUrl);
            params.put(User.USERNAME,title);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError(ChatEvent.ERROR_PROCESS_DATA, R.string.common_error_process_data);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                TEXTING_RS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int success = response.getInt(Constants.SUCCESS);
                    switch (success){
                        case ChatEvent.SEND_NOTIFICATION_SUCCESS:
                            //Se envio la notificacion
                            break;
                        case ChatEvent.ERROR_METHOD_NOT_EXIST:
                            listener.onError(ChatEvent.ERROR_METHOD_NOT_EXIST, R.string.chat_error_method_not_exist);
                            break;
                        default:
                            listener.onError(ChatEvent.ERROR_SERVER,R.string.common_error_server);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(ChatEvent.ERROR_PROCESS_DATA,R.string.common_error_process_data);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley error",error.getLocalizedMessage());
                listener.onError(ChatEvent.ERROR_VOLLEY, R.string.common_error_volley);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //  Las mismas de PHP
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type","application/json; charset=utf-8");
                return params;
            }
        };
        //Configura el envio de la solicitud
        TextingApplication.getInstance().addToReqQueue(jsonObjectRequest);
    }
}
