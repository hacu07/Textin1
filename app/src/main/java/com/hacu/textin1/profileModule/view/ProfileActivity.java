package com.hacu.textin1.profileModule.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hacu.textin1.R;
import com.hacu.textin1.common.pojo.User;
import com.hacu.textin1.common.utils.UtilsCommon;
import com.hacu.textin1.common.utils.UtilsImage;
import com.hacu.textin1.profileModule.ProfilePresenter;
import com.hacu.textin1.profileModule.ProfilePresenterClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    public static final int RC_PHOTO_PICKER = 22;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.btnEditPhoto)
    ImageButton btnEditPhoto;
    @BindView(R.id.progressBarImage)
    ProgressBar progressBarImage;
    @BindView(R.id.etUsername)
    TextInputEditText etUsername;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.contentMain)
    LinearLayout contentMain;

    //Utilzada para cambiar barra de estado si es editar o guardar
    private MenuItem mCurrentMenuItem;

    private ProfilePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mPresenter = new ProfilePresenterClass(this);
        mPresenter.onCreate();
        //Obtiene los datos enviados por el intent desde el maintActivity
        mPresenter.setupUser(getIntent().getStringExtra(User.USERNAME),
                getIntent().getStringExtra(User.EMAIL),
                getIntent().getStringExtra(User.PHOTO_URL));
        configActionBar();

    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            //muestra barra de retroceso
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //Carga la imagen de perfil
    private void setImageProfile(String photoUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_timer_sand)
                .error(R.drawable.ic_emoticon_sad)
                .centerCrop();

        //Agrega listener para saber cuando el recurso esta disponible (Sa carga imagen como bitmap para poder editar)
        Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        hideProgressImage();
                        imgProfile.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this,
                                R.drawable.ic_upload));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        hideProgressImage();
                        imgProfile.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(imgProfile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_profile:
                mCurrentMenuItem = item;
                if (etUsername.getText() != null) {
                    mPresenter.updateUserName(etUsername.getText().toString().trim());
                }
                break;
            case android.R.id.home:
                // agrega transicion
                if (UtilsCommon.hasMaterialDesign()) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }


    @OnClick({R.id.imgProfile, R.id.btnEditPhoto})
    public void onSelectPhoto(View view) {
        mPresenter.checkMode();
    }

    /*
     *           PROFILE VIEW
     * */

    @Override
    public void enableUIElements() {
        setInputs(true);
    }


    @Override
    public void DisableUIElements() {
        setInputs(false);
    }

    private void setInputs(boolean enable) {
        etUsername.setEnabled(enable);
        //Si muestra o no el menu de edicion
        btnEditPhoto.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (mCurrentMenuItem != null) {
            mCurrentMenuItem.setEnabled(enable);
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressImage() {
        progressBarImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressImage() {
        progressBarImage.setVisibility(View.GONE);
    }

    @Override
    public void showUserData(String username, String email, String photoUrl) {
        setImageProfile(photoUrl);

        etUsername.setText(username);
        etEmail.setText(email);
    }

    @Override
    public void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_PHOTO_PICKER);
    }

    @Override
    public void openDialogPreview(Intent data) {
        //layout para previsualizar imagen a subir
        final String urlLocal = data.getDataString();

        //para quitar advertencia cuando se infla
        final ViewGroup nullParent = null;
        View view = getLayoutInflater().inflate(R.layout.dialog_image_upload_preview, nullParent);

        //muestra imagen y texto
        final ImageView imgDialog = view.findViewById(R.id.imgDialog);
        final TextView tvMessage = view.findViewById(R.id.tvMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(R.string.profile_dialog_title)
                .setPositiveButton(R.string.profile_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.updateImage(ProfileActivity.this, Uri.parse(urlLocal));
                        UtilsCommon.showSnackbar(contentMain, R.string.profile_message_imageUploading,
                                Snackbar.LENGTH_LONG);
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //previsualiza la imagen dentro del dialogo
                // se recorta para que sea mas eficiente
                int sizeImagePreview = getResources().getDimensionPixelSize(R.dimen.chat_size_img_preview);
                Bitmap bitmap = UtilsImage.reduceBitmap(ProfileActivity.this, contentMain,
                        urlLocal, sizeImagePreview, sizeImagePreview);
                if (bitmap != null) {
                    imgDialog.setImageBitmap(bitmap);
                }
                tvMessage.setText(R.string.profile_dialog_message);
            }
        });
        alertDialog.show();
    }

    @Override
    public void menuEditMode() {
        mCurrentMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check));
    }

    @Override
    public void menuNormalMode() {
        if (mCurrentMenuItem != null) {
            mCurrentMenuItem.setEnabled(true);
            mCurrentMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pencil));
        }
    }

    @Override
    public void saveUsernameSuccess() {
        UtilsCommon.showSnackbar(contentMain, R.string.profile_message_userUpdated);
    }

    @Override
    public void updateImageSuccess(String photoUrl) {
        setImageProfile(photoUrl);
        UtilsCommon.showSnackbar(contentMain, R.string.profile_message_imageUpdated);
    }

    @Override
    public void setResultOk(String username, String photoUrl) {
        Intent data = new Intent();
        data.putExtra(User.USERNAME, username);
        data.putExtra(User.PHOTO_URL, photoUrl);
        //Recopila informacion y se actualiza
        setResult(RESULT_OK, data);
    }

    @Override
    public void onErrorUpload(int resMgs) {
        UtilsCommon.showSnackbar(contentMain, resMgs);
    }

    @Override
    public void onError(int resMsg) {
        etUsername.requestFocus();
        etUsername.setError(getString(resMsg));
    }


}
