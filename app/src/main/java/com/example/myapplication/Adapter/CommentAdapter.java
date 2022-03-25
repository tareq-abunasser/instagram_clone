package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Comment;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentsHolder> {

    Context context;
    ArrayList<Comment> list;
    public CommentAdapter(Context context,ArrayList list) {
        this.context=context;
        this.list=list;
    }
    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comments,parent,false);
        CommentsHolder holder=new CommentsHolder(view,context,list);
    return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        Comment c=list.get(position);

            holder.setUserId(c.getUser_id());
            holder.getCommentText().setText(c.getComment());

        if (position != 0) {
            holder.getCommentReply().setText("View "+String.valueOf(getItemCount())+" replies");
            holder.setLikeList(c.getLikes());
            holder.setCommentNum(getItemCount());
        }

   // try {
        holder.getUser();
        Log.d("CommentAdapter : onBindViewHolder :"," 2");

    //}
//    catch (NullPointerException e){
//        Log.d("CommentAdapter : onBindViewHolder :","null 2");
//
//    }
    try {
        if (position == 0)
            holder.setPosition();
    }catch (NullPointerException e){
    }
        String timestampDiff = getTimestampDifference(c.getDate_created());
        if (!timestampDiff.equals("0")) {
            holder.getCommentTimePosted().setText(timestampDiff + " DAYS AGO");
        } else {
            holder.getCommentTimePosted().setText("TODAY");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getTimestampDifference(String dateCreated) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Palestine/Gaza"));
        String postTimeStamp = dateCreated;
        Date today = calendar.getTime();
        sdf.format(today);
        Date timeStamp;
        String differences = "";
        try {
            timeStamp = sdf.parse(postTimeStamp);
            differences = String.valueOf(Math.round((today.getTime() - timeStamp.getTime()) / 1000 / 60 / 60 / 24));
        } catch (ParseException e) {
            differences = "0";
        }
        return differences;
    }
}
