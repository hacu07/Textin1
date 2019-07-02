package com.hacu.textin1.mainModule.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hacu.textin1.R;
import com.hacu.textin1.common.pojo.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private List<User> mUsers;
    private Context mContext;
    private OnItemClickListener mListener;

    public UserAdapter(List<User> mUsers, OnItemClickListener mListener) {
        this.mUsers = mUsers;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                 .inflate(R.layout.item_user, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.setClickListener(user, mListener);

        holder.tvName.setText(user.getUsernameValid()); //Si no tiene nombre se agrega el correo

        int messageUnread = user.getMessagesUnread();

        if (messageUnread>0){
            String countStr = messageUnread > 99?
                    mContext.getString(R.string.main_item_max_messageUnread) : String.valueOf(messageUnread);
            holder.tvCountUnread.setText(countStr);
            holder.tvCountUnread.setVisibility(View.VISIBLE);
        }else{
            holder.tvCountUnread.setVisibility(View.GONE);
        }

        RequestOptions options =  new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);

        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(options)
                .into(holder.imgPhoto);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void add(User user){
        if (!mUsers.contains(user)){
            mUsers.add(user);
            notifyItemInserted(mUsers.size() - 1);
        }else{
            update(user);
        }
    }

    public void update(User user) {
        if (mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.set(index,user);
            notifyItemChanged(index);
        }
    }

    public void remove(User user){
        if (mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.remove(index);
            notifyItemRemoved(index);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgPhoto)
        CircleImageView imgPhoto;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvCountUnread)
        TextView tvCountUnread;

        private View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            //Para eventos de clic
            this.view = itemView;
        }

        private void setClickListener(final User user, final OnItemClickListener listener){
            //Entra al char
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(user);
                }
            });

            //Elimina un contacto
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(user);
                    return true;
                }
            });
        }
    }
}
