package com.example.tawriqapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tawriqapp.Model.Message;
import com.example.tawriqapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    Context mContext;
    List<Message> messages;
    String receiverImageUrl = "";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterMessage(Context mContext, List<Message> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    public AdapterMessage(Context mContext, List<Message> messages, String receiverImageUrl) {
        this.mContext = mContext;
        this.messages = messages;
        this.receiverImageUrl = receiverImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_container_sent_message, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_container_received_message, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message message = messages.get(position);

        try {
            Picasso
                    .get()
                    .load(receiverImageUrl + "")
                    .fit()
                    //.placeholder(R.drawable.user_m)
                    .into(holder.receiverImageProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.textMessage.setText(message.getDescription());
        holder.textDateTime.setText(message.getDateTime());

        if (position == messages.size() - 1) {
            if (message.getWasRead()) {
                holder.seenMsg.setVisibility(View.VISIBLE);
                holder.sendMsg.setVisibility(View.GONE);
            } else {
                holder.sendMsg.setVisibility(View.VISIBLE);
                holder.seenMsg.setVisibility(View.GONE);
            }
        } else {
            holder.seenMsg.setVisibility(View.GONE);
            holder.sendMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView receiverImageProfile, sendMsg, seenMsg;
        public TextView textMessage, textDateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            receiverImageProfile = itemView.findViewById(R.id.receiverImageProfile);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            sendMsg = itemView.findViewById(R.id.sendMsg);
            seenMsg = itemView.findViewById(R.id.seenMsg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}