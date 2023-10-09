package com.example.tawriqapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tawriqapp.Model.Comment;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ImageViewHolder> {

    private final Context mContext;
    private final List<Comment> listComment;

    public AdapterComments(Context context, List<Comment> comments) {
        mContext = context;
        listComment = comments;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        Comment comment = listComment.get(position);

        holder.userComment.setText(comment.getCommentText());
        holder.commentTime.setText(comment.getCommentTime());
        holder.commentDate.setText(comment.getCommentDate());

        getPublisherInfo(comment.getWriter(), holder.userImage, holder.userName);

    }

    @Override
    public int getItemCount() {
        return listComment.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView userImage;
        public TextView userName, userComment, commentTime, commentDate;

        public ImageViewHolder(View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.studentImage);
            userName = itemView.findViewById(R.id.studentName);
            userComment = itemView.findViewById(R.id.studentComment);
            commentTime = itemView.findViewById(R.id.commentTime);
            commentDate = itemView.findViewById(R.id.commentDate);
        }
    }

    private void getPublisherInfo(String writer, CircleImageView imageView, TextView username) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("AllStudent");

        reference
                .child(writer)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Student userData = snapshot.getValue(Student.class);
                        assert userData != null;
                        username.setText(userData.getFirstName());
                        try {
                            Picasso
                                    .get()
                                    .load(userData.getImageUri())
                                    .fit()
                                    .placeholder(R.drawable.loading)
                                    .into(imageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
