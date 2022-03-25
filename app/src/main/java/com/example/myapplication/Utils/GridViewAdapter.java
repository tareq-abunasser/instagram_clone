package com.example.myapplication.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.myapplication.R;

import java.util.ArrayList;

public class  GridViewAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater inflater;
    int  layoutResource;
    String myappend;
   ArrayList<String> imgURLs;
    public GridViewAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, layoutResource, imgURLs);
        this.context=context;
        this.layoutResource=layoutResource;
        this.myappend=append;
        this.imgURLs=imgURLs;
    }

    private static class ViewHolder{
        ImageView image;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutResource, parent, false);
        SquareImageView image =  convertView.findViewById(R.id.gridview_image);
        ProgressBar progressBar=convertView.findViewById(R.id.progressBar);
        String imgURL = getItem(position);

        UniversalImageLoader.setImage(imgURL, image, progressBar,  myappend);

        return convertView;
    }
}
//
//    final ViewHolder holder;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                if(convertView == null){
//                convertView = inflater.inflate(layoutResource, parent, false);
//                holder = new ViewHolder();
//                holder.image =  convertView.findViewById(R.id.gridview_image);
//                convertView.setTag(holder);
//                }
//                else{
//                holder = (ViewHolder) convertView.getTag();
//                }
//
//                String imgURL = getItem(position);
//
//                UniversalImageLoader.setImage(imgURL, holder.image, null,  myappend);
//
//                return convertView;