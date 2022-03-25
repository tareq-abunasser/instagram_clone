package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Comment;
import com.example.myapplication.Models.Like;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.myapplication.R.color.sky_blue;

public class CommentsHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
    CircleImageView userPhoto;
    TextView username, commentText, commentTimePosted, commentLikes, reply, commentReply;
    ImageView like;
    ArrayList<Like> likeList;
    String userId;
    Context context;
    int commentNum;



    public CommentsHolder(@NonNull View itemView, Context context, ArrayList<Comment> list) {
        super(itemView);
        userPhoto = itemView.findViewById(R.id.comment_profile_image);
        username = itemView.findViewById(R.id.comment_username);
        commentText = itemView.findViewById(R.id.comment);
        commentTimePosted = itemView.findViewById(R.id.comment_time_posted);
        commentLikes = itemView.findViewById(R.id.comment_likes);
        reply = itemView.findViewById(R.id.comment_reply);
        like = itemView.findViewById(R.id.comment_like);
        commentReply = itemView.findViewById(R.id.comment_replys);
        this.context = context;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public ArrayList<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(ArrayList<Like> likeList) {
        this.likeList = likeList;
    }

    public CircleImageView getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(CircleImageView userPhoto) {
//        UniversalImageLoader.
        this.userPhoto = userPhoto;
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public TextView getCommentText() {
        return commentText;
    }

    public void setCommentText(TextView commentText) {
        this.commentText = commentText;
    }

    public TextView getCommentTimePosted() {
        return commentTimePosted;
    }

    public void setCommentTimePosted(TextView commentTimePosted) {
        this.commentTimePosted = commentTimePosted;
    }

    public TextView getCommentLikes() {
        return commentLikes;
    }

    public void setCommentLikes(TextView commentLikes) {
        this.commentLikes = commentLikes;
    }

    public TextView getReply() {
        return reply;
    }

    public void setReply(TextView reply) {
        this.reply = reply;
    }

    public TextView getCommentReply() {
        return commentReply;
    }

    public void setCommentReply(TextView commentReply) {
        this.commentReply = commentReply;
    }

    public ImageView getLike() {
        return like;
    }

    public void setLike(ImageView like) {
        this.like = like;
    }

    public void getUser() {
        if(likeList!=null ) {
            if (likeList.size() != 0)
                commentLikes.setText(likeList.size());
            else
                commentLikes.setVisibility(View.GONE);

            if (commentNum == 0)
                commentReply.setVisibility(View.GONE);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_user_account_settings))
                .child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings settings = new UserAccountSettings();
                settings = dataSnapshot.getValue(UserAccountSettings.class);
                Glide.with(itemView.getContext()).load(settings.getProfile_photo()).into(userPhoto);
                username.setText(settings.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setCommentNum(int itemCount) {
        commentNum=itemCount;
    }

    @Override
    public void onClick(View view) {
        itemView.setBackgroundColor(itemView.getResources().getColor(sky_blue));
    }

    public void setPosition() {
        commentLikes.setVisibility(View.GONE);
        reply.setVisibility(View.GONE);
        commentReply.setVisibility(View.GONE);
        like.setVisibility(View.GONE);
    }
}
