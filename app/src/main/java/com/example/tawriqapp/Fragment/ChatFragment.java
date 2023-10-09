package com.example.tawriqapp.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tawriqapp.Adapter.AdapterReceivers;
import com.example.tawriqapp.Model.MessageRoom;
import com.example.tawriqapp.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    FragmentChatBinding chatBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messageRoomReference = database.getReference("MessageRoom");


    List<MessageRoom> receivers;
    AdapterReceivers adapterReceivers;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false);
        if (user != null) {
            Initialize_Variables();
            getAllReceivers();
        } else {
            chatBinding.emptyImage.setVisibility(View.VISIBLE);
            chatBinding.progressCircle.setVisibility(View.GONE);
        }
        return chatBinding.getRoot();
    }

    private void Initialize_Variables() {
        receivers = new ArrayList<>();
        adapterReceivers = new AdapterReceivers(getContext(), receivers);
        chatBinding.messagesListRecyclerView.setHasFixedSize(true);
        chatBinding.messagesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatBinding.messagesListRecyclerView.setAdapter(adapterReceivers);
    }

    void getAllReceivers() {
        chatBinding.progressCircle.setVisibility(View.VISIBLE);
        messageRoomReference
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        receivers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MessageRoom receiver = snapshot.getValue(MessageRoom.class);
                            if (receiver != null) {
                                receivers.add(receiver);
                            }
                        }
                        adapterReceivers.notifyDataSetChanged();
                        chatBinding.progressCircle.setVisibility(View.GONE);
                        if (receivers.isEmpty())
                            chatBinding.emptyImage.setVisibility(View.VISIBLE);
                        else {
                            chatBinding.emptyImage.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (user != null)
            adapterReceivers.notifyDataSetChanged();
    }



}