package com.example.myapplication.Share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.FilePaths;
import com.example.myapplication.Utils.FileSearch;
import com.example.myapplication.Utils.GridViewAdapter;
import com.example.myapplication.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
//
//
//    //widgets
//    private GridView gridView;
//    private ImageView galleryImage;
//    private Spinner directorySpinner;
//    View view;
//    //vars
//    private ArrayList<String> directories;
//    private String mAppend = "file:/";
//    private String mSelectedImage;
//
//    private    static final int NUM_GRID_COLUMNS=3;
//
//    public GalleryFragment() {
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//         view=inflater.inflate(R.layout.fragment_gallery, container, false);
//        galleryImage =  view.findViewById(R.id.galleryImageView);
//        gridView =  view.findViewById(R.id.gridView);
//        directorySpinner =  view.findViewById(R.id.spinnerDirectory);
//        directories = new ArrayList<>();
//
//        ImageView shareClose =  view.findViewById(R.id.close);
//        shareClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });
//        initSpinner();
//        return view;
//    }
//
//
//    public void initSpinner(){
//       if( FileSearch.getDirectoryPaths(FilePaths.PICTURES)!=null){
//           directories=FileSearch.getDirectoryPaths(FilePaths.PICTURES);
//       }
//     ArrayList<String> directoryName=new ArrayList<>();
//       for(String dir:directories){
//           String lastDirectoryName=dir.substring(dir.lastIndexOf("/")+1);
//            directoryName.add(lastDirectoryName);
//       }
//        directories.add(FilePaths.CAMERA);
//        ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_spinner_item,directoryName);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                setupGridView(directories.get(i));
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//
//
//        });
//
//    }
//
//    public void setupGridView(String selectedDirectory){
//
//        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
//        //use the grid adapter to adapter the images to gridview
//
//        GridViewAdapter adapter;
//        adapter = new GridViewAdapter(getActivity(), R.layout.grid_view_raw, "", imgURLs);
//        gridView.setAdapter(adapter);
//
//        //set the grid column width
//        int gridWidth=getResources().getDisplayMetrics().widthPixels;
//        int imageWidth=gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(imageWidth);
//
//
//        //set the first image to be displayed when the activity fragment view is inflated
//        setImage(imgURLs.get(0),galleryImage,mAppend);
//        mSelectedImage = imgURLs.get(0);
//
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                setImage(imgURLs.get(position), galleryImage, mAppend);
//                mSelectedImage = imgURLs.get(position);
//            }
//        });
//    }
//
//    private void setImage(String imgURL, ImageView image, String append) {
//        UniversalImageLoader.setImage(imgURL, image, null, append);
//    }


    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    //private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext=getActivity();
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage =  view.findViewById(R.id.galleryImageView);
        gridView =  view.findViewById(R.id.gridView);
        directorySpinner =  view.findViewById(R.id.spinnerDirectory);

        directories = new ArrayList<>();

        ImageView shareClose = view.findViewById(R.id.close);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        TextView nextScreen = view.findViewById(R.id.next);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NextActivity.class);
                intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                startActivity(intent);
            }
        });

        init();
      //  CropImage.activity().setAspectRatio(1,1).start(getActivity());

        return view;
    }

    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        if(FileSearch.getDirectoryPaths(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        ArrayList<String> directoryNames = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < directories.size(); i++){
             index = directories.get(i).lastIndexOf("/")+1;
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        directories.add(filePaths.CAMERA);
        index = filePaths.CAMERA.lastIndexOf("/")+1;
        directoryNames.add(filePaths.CAMERA.substring(index));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(String selectedDirectory){
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridViewAdapter adapter = new GridViewAdapter(mContext, R.layout.grid_view_raw, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try {
            if(imgURLs!=null) {
                setImage(imgURLs.get(0), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(0);
            }
        }catch (Exception e){
           Log.d("GalleryFragment","No Image");
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }


        private void setImage(String imgURL, ImageView image, String append) {
        UniversalImageLoader.setImage(imgURL, image, null, append);
    }

}
