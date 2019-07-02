package com.hacu.textin1.chatModule.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hacu.textin1.R;
import com.hacu.textin1.common.pojo.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> mMessages;
    private OnItemClickListener mListener;

    //Cual fue la ultima imagen, respecto al scroll
    private int lastPhoto = 0;

    public ChatAdapter(List<Message> messages, OnItemClickListener listener) {
        this.mMessages = messages;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,
                parent,false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Message message = mMessages.get(position);
        //Constantes de max y min de ancho y alto
        final int maxMarginHorizontal = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_horizontal);//48dp
        final int maxMaginTop = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_top);//8dp
        final int minMargin = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_min);//1dp
        //Propiedades por default a la derecha - mensajes nuestros
        int gravity = Gravity.END;
        Drawable background = ContextCompat.getDrawable(mContext, R.drawable.background_chat_me);
        int marginStart = maxMarginHorizontal;
        int margintop = minMargin;
        int marginEnd = minMargin;
        //si el mensaje es enviado por el amigo
        if (!message.isSentByMe()){
            gravity = Gravity.START; // Se establece a la isquierda
            background = ContextCompat.getDrawable(mContext, R.drawable.background_chat_friend);
            marginEnd =  maxMarginHorizontal;
            marginStart = minMargin;
        }

        //Si los mensajes no son del mismo usuario que envia
        // si el ultimo mensaje no es enviado por mi
        if (position > 0 &&
                message.isSentByMe() != mMessages.get(position-1).isSentByMe()){
            margintop = maxMaginTop;
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvMessage.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(marginStart,margintop,marginEnd,minMargin);

        //si es una imagen o texto
        if(message.getPhotoUrl() != null){
            holder.tvMessage.setVisibility(View.GONE);
            holder.imgPhoto.setVisibility(View.VISIBLE);

            //ultima posicion de la imagen
            //avisa al contexto que tiene la imagen cargada y se puede recorrer el Scroll
            //para que se empuje el listado de mensajes para mejorar experiencia de usuario

            if (position > lastPhoto){
                lastPhoto = position;
            }

            final int size = mContext.getResources().getDimensionPixelSize(R.dimen.chat_size_image);
            params.width = size;
            params.height = size;

            //carga imagen con glide
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_timer_sand_160)
                    .error(R.drawable.ic_emoticon_sad)
                    .centerCrop();


            Glide.with(mContext)
                    .asBitmap()
                    .load(message.getPhotoUrl())
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            //fallo la carga de la imagen
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            //Se cargo exitosamente en el chat
                            int dimension = size - mContext.getResources().getDimensionPixelSize(R.dimen.chat_padding_image);
                            //Obtiene la miniatura de la imagen - del resource (imagen)
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(resource, dimension,dimension);
                            holder.imgPhoto.setImageBitmap(bitmap);

                            if (!message.isLoaded()){ // si no se ha cargado
                                message.setLoaded(true);

                                if (position == lastPhoto){ //Si es la ultima foto
                                    //Envia el scroll a la imagen
                                    mListener.onImageLoaded();
                                }
                            }
                            return true;
                        }
                    })
                    .into(holder.imgPhoto);
            holder.imgPhoto.setBackground(background);

            //Cuando da clic en la imagen hace un ZOOM
            holder.setClickListener(message,mListener);
        }else{
            //carga del texto
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.imgPhoto.setVisibility(View.GONE);

            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.height= LinearLayout.LayoutParams.WRAP_CONTENT;

            holder.tvMessage.setBackground(background);
            holder.tvMessage.setText(message.getMsg());
        }

        holder.imgPhoto.setLayoutParams(params);
        holder.tvMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void add(Message message){
        if (!mMessages.contains(message)){
            mMessages.add(message);
            notifyItemInserted(mMessages.size() - 1);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMessage)
        TextView tvMessage;
        @BindView(R.id.imgPhoto)
        AppCompatImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void setClickListener(final Message message, final OnItemClickListener listener){
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickImage(message);
                }
            });
        }
    }
}
