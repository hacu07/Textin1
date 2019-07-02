package com.hacu.textin1;

import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

//offline - se llama desde firebase
//habilitala persistencia de datos para firebase
public class TextingApplication extends Application {

    //NOTIFICACION
    private RequestQueue mRequestQueue;
    private static TextingApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        configFirebase();

        mInstance = this;
    }

    private void configFirebase(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    //NOTIFICACION
    public static synchronized TextingApplication getInstance(){
        return mInstance;
    }

    public RequestQueue getmRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        }
        return mRequestQueue;
    }

    //Ayuda a agregar solicitudes a cola de peticiones
    public <T> void addToReqQueue(Request<T> request){
        //FORMULA = tiempo + (tipoe * Multi)
        //Tiempo de espera de solicitud, si falla reintenta perosi falla ahi termina
        // en si hace dos peticiones si la primera falla
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getmRequestQueue().add(request);
    }
}
