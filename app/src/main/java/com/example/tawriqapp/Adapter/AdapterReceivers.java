package com.example.tawriqapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tawriqapp.Activity.ChattingActivity;
import com.example.tawriqapp.Activity.HomeActivity;
import com.example.tawriqapp.Model.MessageRoom;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterReceivers extends RecyclerView.Adapter<AdapterReceivers.ViewHolder> {

    private final Context mContext;
    private final List<MessageRoom> receivers;

    FirebaseDatabase Database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = Database.getReference("AllStudent");

    public AdapterReceivers(Context mContext, List<MessageRoom> receivers) {
        this.mContext = mContext;
        this.receivers = receivers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.receiver_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MessageRoom receiver = receivers.get(position);
        getReceiverData(receiver.getReceiverID(), holder);

        holder.lastMsg.setText(receiver.getLastMessage());
        holder.dateTime.setText(receiver.getDateTime());

        holder.receiverLayout.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ChattingActivity.class);
            intent.putExtra("ReceiverID", receiver.getReceiverID());
            mContext.startActivity(intent);
        });

        if (HomeActivity.listSenderID.contains(receiver.getReceiverID())) {
            holder.unReadMessage.setVisibility(View.VISIBLE);
        } else {
            holder.unReadMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return receivers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout receiverLayout;
        public ImageView receiverImageProfile, receiverOnline, receiverOffline, unReadMessage;
        public TextView receiverName, lastMsg, dateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            receiverLayout = itemView.findViewById(R.id.receiverLayout);
            receiverName = itemView.findViewById(R.id.receiverName);
            receiverImageProfile = itemView.findViewById(R.id.receiverImageProfile);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            receiverOnline = itemView.findViewById(R.id.receiverOnline);
            receiverOffline = itemView.findViewById(R.id.receiverOffline);
            unReadMessage = itemView.findViewById(R.id.unReadMessage);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }

    void getReceiverData(String receiverID, ViewHolder holder) {
        referenceAllStudent
                .child(receiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Student receiverData = snapshot.getValue(Student.class);
                        if (receiverData != null) {
                            try {
                                Picasso
                                        .get()
                                        .load(receiverData.getImageUri() + "")
                                        .fit()
                                        .into(holder.receiverImageProfile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            holder.receiverName.setText(receiverData.getFirstName());

                            if (receiverData.getStatus().equals("Online")) {
                                holder.receiverOnline.setVisibility(View.VISIBLE);
                                holder.receiverOffline.setVisibility(View.GONE);
                            } else {
                                holder.receiverOnline.setVisibility(View.GONE);
                                holder.receiverOffline.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
