package com.hacu.textin1.mainModule;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hacu.textin1.addModule.view.AddFragment;
import com.hacu.textin1.R;
import com.hacu.textin1.chatModule.view.ChatActivity;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.common.utils.UtilsCommon;
import com.hacu.textin1.loginModule.View.LoginActivity;
import com.hacu.textin1.mainModule.view.MainView;
import com.hacu.textin1.mainModule.view.adapters.OnItemClickListener;
import com.hacu.textin1.mainModule.view.adapters.RequestAdapter;
import com.hacu.textin1.mainModule.view.adapters.UserAdapter;
import com.hacu.textin1.profileModule.view.ProfileActivity;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, MainView {

    private static final int RC_PROFILE = 23;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvRequests)
    RecyclerView rvRequests;
    @BindView(R.id.rvUers)
    RecyclerView rvUers;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.contentMain)
    CoordinatorLayout contentMain;

    private UserAdapter mUserAdapter;
    private RequestAdapter mRequestAdapter;
    private MainPresenter mPresenter;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Se quita el toolbar y fab

        mPresenter = new MainPresenterClass(this);
        mPresenter.onCreate(); //registra en eventbus
        mUser = mPresenter.getCurrentUser();

        configToolbar();
        configAdapter();
        configRecyclerView();
        configTutorial();
    }

    private void configToolbar() {
        //Muestra nombre de usuario con el que se loggeo
        UtilsCommon.loadImage(this, mUser.getPhotoUrl(), imgProfile);
        toolbar.setTitle(mUser.getUsernameValid());
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        mUserAdapter = new UserAdapter(new ArrayList<User>(), this);
        mRequestAdapter = new RequestAdapter(new ArrayList<User>(), this);
    }

    private void configRecyclerView() {
        rvUers.setLayoutManager(new LinearLayoutManager(this));
        rvUers.setAdapter(mUserAdapter);

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(mRequestAdapter);
    }

    private void configTutorial() {
        //Se usa libreria para el tutorial
        //Lo muestra solo una unica vez
        new MaterialShowcaseView.Builder(this)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTextColor(ContextCompat.getColor(this,R.color.blue_a100))
                .setDismissTextColor(ContextCompat.getColor(this,android.R.color.white))
                .setMaskColour(ContextCompat.getColor(this,R.color.gray_900_t))
                .setTarget(fab)
                .setTargetTouchable(true)
                .setTitleText(R.string.app_name)
                .setContentText(R.string.main_tuto_message)
                .setDismissText(R.string.main_tuto_ok)
                .setDismissStyle(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC))
                .singleUse(getString(R.string.main_tuto_fabAdd))
                .setDelay(2000)//animacion - retraso en aparecer
                .setFadeDuration(600)//Duraciond e la animacion
                .setDismissOnTargetTouch(true)//Permite que el tutorial desparezca al hacer clic en el fab
                .setDismissOnTouch(false)//desaparece al hacer clic en cualquier parte de la pantalla
                .show();
    }

    //Infla el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                //Cierra Sesion
                mPresenter.signOff();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP
                        | intent.FLAG_ACTIVITY_NEW_TASK
                        | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.action_profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                intentProfile.putExtra(User.USERNAME, mUser.getUsername());
                intentProfile.putExtra(User.EMAIL, mUser.getEmail());
                intentProfile.putExtra(User.PHOTO_URL, mUser.getPhotoUrl());

                if (UtilsCommon.hasMaterialDesign()){
                    startActivityForResult(intentProfile, RC_PROFILE,
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }else{
                    startActivityForResult(intentProfile,RC_PROFILE);
                }
                break;
            case R.id.action_about:
                openAbout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Se encarga de actualizar el nombre e imagen una vez a salido con exito la actualizacion desde el perfil
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case RC_PROFILE:
                    if (data != null){
                        mUser.setUsername(data.getStringExtra(User.USERNAME));
                        mUser.setPhotoUrl(data.getStringExtra(User.PHOTO_URL));
                        configToolbar();
                    }
                    break;
            }
        }
    }

    private void openAbout() {
        //Infla la actividad mostrando un dialogo
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogFragmentTheme)
                .setTitle(R.string.main_menu_about)
                .setView(view)
                .setPositiveButton(R.string.common_label_ok, null)
                .setNeutralButton(R.string.about_privacy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Redirecciona a pagina web
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/HAROLDHERNANDEZ14"));
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        clearNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroid();
    }

    /*
    Cada vez que entra a la bandeja de contactos
    borra las notificaciones push recibidas
    */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }


    @OnClick(R.id.fab)
    public void onAddClicked() {
        //Lanza el dialog para enviar solicitud a amigo
        new AddFragment().show(getSupportFragmentManager(), getString(R.string.addFriend_title));
    }


    /**
     * MainView
     */

    @Override
    public void friendAdded(User user) {
        mUserAdapter.add(user);
    }

    @Override
    public void friendUpdate(User user) {
        mUserAdapter.update(user);
    }

    @Override
    public void friendRemove(User user) {
        mUserAdapter.remove(user);
    }

    @Override
    public void requestAdded(User user) {
        mRequestAdapter.add(user);
    }

    @Override
    public void requestUpdate(User user) {
        mRequestAdapter.update(user);
    }

    @Override
    public void requestRemove(User user) {
        mRequestAdapter.remove(user);
    }

    @Override
    public void showRequestAccepted(String username) {
        Snackbar.make(contentMain, getString(R.string.main_message_request_accepted, username), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showRequestDenied() {
        Snackbar.make(contentMain, getString(R.string.main_message_request_denied), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showFriendRemoved() {
        Snackbar.make(contentMain, getString(R.string.main_message_user_removed), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(int resMsg) {
        Snackbar.make(contentMain, resMsg, Snackbar.LENGTH_SHORT).show();
    }

    /*
     *   OnItemClickListener
     * */
    @Override
    public void onItemClick(User user) {
        //Entra al chat directo con el usuario seleccionado
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(User.UID, user.getUid());
        intent.putExtra(User.USERNAME,user.getUsername());
        intent.putExtra(User.EMAIL, user.getEmail());
        intent.putExtra(User.PHOTO_URL, user.getPhotoUrl());

        if (UtilsCommon.hasMaterialDesign()){
            //agrega transicion
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }else{
            startActivity(intent);
        }

    }

    @Override
    public void onItemLongClick(final User user) {

        //Elimina el usuario seleccionado
        new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(getString(R.string.main_dialog_title_confirmDelete))
                .setMessage(String.format(Locale.ROOT, getString(R.string.main_dialog_message_confirmDelete)
                        , user.getUsernameValid()))
                .setPositiveButton(R.string.main_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.removeFriend(user.getUid());
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null)
                .show();

    }

    @Override
    public void onAcceptRequest(User user) {
        //Acepta solicitud de amistar
        mPresenter.acceptRequest(user);
    }

    @Override
    public void onDenyRequest(User user) {
        //Rechaza solicitud
        mPresenter.denyRequest(user);
    }


}
