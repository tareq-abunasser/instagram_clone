package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.Profile.UserProfileFragment;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<UserAccountSettings> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<UserAccountSettings> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }


    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final UserAccountSettings user = mUsers.get(position);

//        holder.btn_follow.setVisibility(View.VISIBLE);


   // isFollowing(user.getId(), holder.btn_follow);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getProfile_photo()).into(holder.image_profile);

//        if (user.getUserId().equals(firebaseUser.getUid())){
//            holder.btn_follow.setVisibility(View.GONE);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUserId());
                    editor.apply();
                    Log.d("UserAdapter","tareq :"+user.getUserId());
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                            new UserProfileFragment(user.getUserId())).commit();
//                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
//                           new ProfileFragment("Search",user.getUserId())).commit();

                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });

//        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.btn_follow.getText().toString().equals("follow")) {
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(user.getId()).setValue(true);
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
//                            .child("followers").child(firebaseUser.getUid()).setValue(true);
//
//                   addNotification(user.getId());
//                } else {
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(user.getId()).removeValue();
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
//                            .child("followers").child(firebaseUser.getUid()).removeValue();
//                }
//            }
//
//        });
    }

//
//    private void addNotification(String userid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("userid", firebaseUser.getUid());
//        hashMap.put("text", "started following you");
//        hashMap.put("postid", "");
//        hashMap.put("ispost", false);
//
//        reference.push().setValue(hashMap);
//    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }












    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_close;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_close = itemView.findViewById(R.id.btn_close);
        }
    }









//    private void isFollowing(final String userid, final Button button){
//
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(firebaseUser.getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(userid).exists()){
//                    button.setText("following");
//                } else{
//                    button.setText("follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}