package com.hacu.textin1.addModule.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hacu.textin1.addModule.AddPresenter;
import com.hacu.textin1.addModule.AddPresenterClass;
import com.hacu.textin1.R;
import com.hacu.textin1.common.utils.UtilsCommon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Se extendio a DialogFragment
 * se implemento OnShowListener
 */
public class AddFragment extends DialogFragment implements DialogInterface.OnShowListener, AddView {


    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.contentMain)
    FrameLayout contentMain;

    //Envia solicitud de amistad
    private Button positiveButton;

    private AddPresenter mPresenter;

    Unbinder unbinder;

    public AddFragment() {
        mPresenter = new AddPresenterClass(this);
    }

    //Se elimino onCreate y se agrego este metodo
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Lo que se va a mostrar como dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.addFriend_title)
                .setPositiveButton(R.string.common_label_accept, null)
                .setNeutralButton(R.string.common_label_cancel, null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);
        builder.setView(view);  // Se agrega la vista al dialogo
        unbinder = ButterKnife.bind(this, view);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }


    //Una vez se muestra el dialogo se activa este evento
    @Override
    public void onShow(DialogInterface dialogInterface) {
        //Obtiene el dialogo actual
        final AlertDialog dialog = (AlertDialog) getDialog();

        //Agrega los btn locales al dialogo y asigna su listener
        if (dialog != null){
            positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Valida que el email ingresado sea valido
                    if (UtilsCommon.validateEmail(getActivity(), etEmail)) {
                        //Envia la solicitud
                        mPresenter.addFriend(etEmail.getText().toString().trim());
                    }
                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //quita el dialogo
                    dismiss();
                }
            });
        }
        //Registra en eventbus
        mPresenter.onShow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void enableUIElements() {
        etEmail.setEnabled(true);
        positiveButton.setEnabled(true);
    }

    @Override
    public void disableUIElements() {
        etEmail.setEnabled(false);
        positiveButton.setEnabled(false);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    //Si se envio correctamente la solicitud
    @Override
    public void friendAdded() {
        Toast.makeText(getActivity(), R.string.addFriend_message_request_dispatched, Toast.LENGTH_SHORT).show();
        //Elimina el dialog
        dismiss();
    }

    // No se logro enviar la solcitud
    @Override
    public void friendNotAdded() {
        etEmail.setError(getString(R.string.addFriend_error_message));
        etEmail.requestFocus();
    }
}
