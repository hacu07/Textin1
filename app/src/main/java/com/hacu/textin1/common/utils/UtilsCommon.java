package com.hacu.textin1.common.utils;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hacu.textin1.R;
import com.hacu.textin1.mainModule.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class UtilsCommon {
    /*
    * Codificar un correo electronico
    * Para tener el email como key se cambian los caracteres siguientes
    * */
    public static String getEmailEncoded(String email){
        String preKey = email.replace("_","__");
        return preKey.replace(".","_");
    }

    //NOTIFICACION
    /*
    * Codifica el correo cambia el arroba por _64
    * Importante para suscribirse al topico para envio y recibimiento de notificacion
    * */
    public static String getEmailToTopic(String email){
        String topic = getEmailEncoded(email);
        topic = topic.replace("@","_64");
        return topic;
    }

    /*
    * Cargar imagenes basicas
    * */
    public static void loadImage(Context context, String url, CircleImageView target) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(target);
    }

    /*
    * Valida que el correo sea un correo valido
    * */
    public static boolean validateEmail(Context context, TextInputEditText etEmail) {
        boolean isValid = true;

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()){
            etEmail.setError(context.getString(R.string.common_validate_field_required));
            etEmail.requestFocus();
            isValid = false;
        }
        //Revisa que el string sea un correo valido - que tenga estructura de un corre
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(context.getString(R.string.common_validate_email_invalid));
            etEmail.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    /*
    * Verificacion de versiones
    * */
    public static boolean hasMaterialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /*
    * Mostrar Mensajes con Snackbar
    * */
    public static void showSnackbar(View contentMain, int resMsg) {
        showSnackbar(contentMain, resMsg, Snackbar.LENGTH_SHORT);
    }

    public static void showSnackbar(View contentMain, int resMsg, int duration) {
        Snackbar.make(contentMain, resMsg, duration).show();
    }

    public static boolean validateMessage(EditText etMessage) {
        return etMessage.getText() != null &&
                !etMessage.getText().toString().trim().isEmpty();
    }
}
