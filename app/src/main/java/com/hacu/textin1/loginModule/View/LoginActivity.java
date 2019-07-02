package com.hacu.textin1.loginModule.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.hacu.textin1.loginModule.LoginPresenter;
import com.hacu.textin1.loginModule.LoginPresenterClass;
import com.hacu.textin1.R;
import com.hacu.textin1.mainModule.MainActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginView{

    public static final int RC_SIGN_IN = 21;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Log.i("flag", "onCreate: ");
        //Inmeditamente averigua si se esta o no loggeado
        mPresenter = new LoginPresenterClass(this);
        mPresenter.onCreate();
        mPresenter.getStatusAuth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        Log.i("flag", "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
        Log.i("flag", "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroid();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("flag", "onActivityResult: ");
        mPresenter.result(requestCode,resultCode,data);
    }

    /*
    *   LoginView - metodo de la interfaz
    *   codigo mas ordenado
    * */

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        //Elimina el registro de loginActivity una vez se ha iniciado sesion correctamente
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK |
                 intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void openUILogin() {
        //Lanza Interfaz de FirebaseUI
        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder().build();

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setTosAndPrivacyPolicyUrls("www.policy.cursos-android-ant.com",
                "www.privacity.cursos-android-ant.com")
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                        googleIdp))
                .setTheme(R.style.BlueTheme)
                .setLogo(R.mipmap.ic_launcher)
        .build(), RC_SIGN_IN);
    }

    @Override
    public void showLoginSuccessfully(Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        String email = ""; // El Email no se puede repetir, sirve para validar
        if(response != null){
            email = response.getEmail();
        }
        Toast.makeText(this,getString(R.string.login_message_success,email),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessageStarting() {
        //Sesion autenticada
        tvMessage.setText(R.string.login_message_loading);
    }

    @Override
    public void showError(int resMsg) {
        Toast.makeText(this,resMsg, Toast.LENGTH_SHORT).show();
    }
}
