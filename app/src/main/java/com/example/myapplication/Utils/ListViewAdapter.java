package com.example.myapplication.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;


public class ListViewAdapter extends ArrayAdapter<String> {

    Context context;
    String text[];
    int rImgs[];
    int raw;
    public ListViewAdapter(Context c, String text[], int imgs[], int raw) {
        super(c, R.layout.listview_raw, R.id.textview,text);
        this.context = c;
        this.text = text;
        this.rImgs = imgs;
        this.raw=raw;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(raw, parent, false);
        TextView myText = row.findViewById(R.id.textview);
        ImageView images = row.findViewById(R.id.image_setting);
        // now set our resources on views
        myText.setText(text[position]);
        images.setImageResource(rImgs[position]);
        return row;
    }
}
