package com.example.myapplication.Profile.EditProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class GenderFragment extends DialogFragment {

    String gender="tareq";
    TextView ok,cancel;
    public interface OnInputListener{
        void sendGender(String gender);
    }
    public OnInputListener mOnInputListener;

    public GenderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_gender, container, false);
        final RadioGroup radioGroup=view.findViewById(R.id.radio_group);
       ok=view.findViewById(R.id.dialogOK);
       cancel=view.findViewById(R.id.dialogCancel);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb=view.findViewById(i);
                switch (rb.getId()){
                    case R.id.female:
                        gender="Female";
                        break;
                    case R.id.male:
                        gender="Male";
                        break;
                    case R.id.custom:
                        gender="Custom";
                        break;
                    case R.id.prefer_not_to_say:
                        gender="Prefer Not to Say";
                        break;
//                                       default:
//                                           gender="Prefer Not to Say";
//                                           break;
                } }
        });
       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getDialog().dismiss();
           }
       });
       ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(final View view) {
//               ((EditProfileActivity)getActivity()).mGender.setText(gender);
               mOnInputListener.sendGender(gender);
               getDialog().dismiss();
           }
       });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){

        }
    }

}