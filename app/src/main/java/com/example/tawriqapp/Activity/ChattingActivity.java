package com.example.tawriqapp.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tawriqapp.Adapter.AdapterMessage;
import com.example.tawriqapp.Model.Message;
import com.example.tawriqapp.Model.MessageRoom;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ActivityChattingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChattingActivity extends AppCompatActivity implements Student.ViewMessages {

    ActivityChattingBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase Database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = Database.getReference("AllStudent");
    DatabaseReference messageReference = Database.getReference("Messages");
    DatabaseReference messageRoomReference = Database.getReference("MessageRoom");

    ValueEventListener seenListener;

    AdapterMessage adapterMessage;
    List<Message> messages;

    String receiverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.primary));

        receiverID = getIntent().getStringExtra("ReceiverID");

        getReceiverData(receiverID);

        Initialize_Variables();

        // Loading All Message Between User & Receiver
        ViewMessages(user.getUid(), receiverID);


        binding.layoutSend.setOnClickListener(v -> {
            String Msg = binding.inputMessage.getText().toString();
            if (!Msg.isEmpty()) {
                sendPrivetMessage(user.getUid(), receiverID, Msg);
            } else {
                Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            binding.inputMessage.setText("");
        });
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        seenMessage(receiverID);
    }

    void Initialize_Variables() {
        messages = new ArrayList<>();
        adapterMessage = new AdapterMessage(this, messages);
        binding.chatRecyclerView.setAdapter(adapterMessage);
        inputChange();
    }

    public void getReceiverData(String receiverID) {
        referenceAllStudent
                .child(receiverID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Student receiver = snapshot.getValue(Student.class);
                        if (receiver != null) {
                            try {
                                Picasso
                                        .get()
                                        .load(receiver.getImageUri() + "")
                                        .fit()
                                        .into(binding.receiverImageProfile);
                            } catch (Exception e) {
                            }

                            adapterMessage = new AdapterMessage(ChattingActivity.this, messages, receiver.getImageUri());
                            binding.chatRecyclerView.setAdapter(adapterMessage);

                            binding.receiverName.setText(receiver.getFirstName());

                            String typing = receiver.getTypingTo();
                            String receiverStatus = receiver.getStatus();

                            if (typing.equals(user.getUid())) {
                                binding.receiverStatus.setText("typing...");
                            } else {
                                if (receiverStatus.equals("Online")) {
                                    binding.receiverStatus.setTextColor(getResources().getColor(R.color.colorLime));
                                    binding.receiverStatus.setText("Online");
                                } else {
                                    binding.receiverStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                                    binding.receiverStatus.setText("Last Seen " + receiverStatus);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void sendPrivetMessage(String Sender, String Receiver, String Msg) {
        String ID = String.valueOf(System.currentTimeMillis());
        String DateTime = getDateTime();

        Message message = new Message();
        message.setID(ID);
        message.setSender(Sender);
        message.setReceiver(Receiver);
        message.setDescription(Msg);
        message.setDateTime(DateTime);
        message.setWasRead(false);
        messageReference
                .child(ID)
                .setValue(message);

        messageRoomReference
                .child(Sender)
                .child(Receiver)
                .setValue(new MessageRoom(Sender, Receiver, Msg, DateTime));

        messageRoomReference
                .child(Receiver)
                .child(Sender)
                .setValue(new MessageRoom(Receiver, Sender, Msg, DateTime));
    }

    @Override
    public void ViewMessages(String Sender, String Receiver) {
        binding.progressBar.setVisibility(View.VISIBLE);
        messageReference
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                if (message.getSender().equals(Sender) && message.getReceiver().equals(Receiver)
                                        ||
                                        message.getReceiver().equals(Sender) && message.getSender().equals(Receiver)) {
                                    messages.add(message);
                                }
                            }
                        }

                        if (!messages.isEmpty())
                            binding.chatRecyclerView.smoothScrollToPosition(messages.size() - 1);

                        adapterMessage.notifyDataSetChanged();
                        binding.chatRecyclerView.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputChange() {
        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    setUserTypingStatus("noOne");
                } else {
                    setUserTypingStatus(receiverID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void seenMessage(String userid) {
        seenListener = messageReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                if (message.getReceiver().equals(user.getUid()) &&
                                        message.getSender().equals(userid)) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("wasRead", true);
                                    snapshot.getRef().updateChildren(hashMap);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setUserStatus(String status) {
        if (user != null)
            referenceAllStudent
                    .child(user.getUid())
                    .child("status")
                    .setValue(status);
    }

    private void setUserTypingStatus(String typing) {
        if (user != null)
            referenceAllStudent
                    .child(user.getUid())
                    .child("typingTo")
                    .setValue(typing);
    }

    String getDateTime() {
        Date date = new Date();
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageReference.removeEventListener(seenListener);
        setUserStatus(getDateTime());
        setUserTypingStatus("noOne");
    }

}