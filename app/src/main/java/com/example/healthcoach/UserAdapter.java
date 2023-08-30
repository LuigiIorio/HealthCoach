package com.example.healthcoach;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserEntity> userList = new ArrayList<>();

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserEntity currentUser = userList.get(position);

        holder.emailTextView.setText(currentUser.getEmail());
        holder.ageTextView.setText("Age: " + currentUser.getAge());
        holder.genderTextView.setText("Gender: " + currentUser.getGender());
        holder.birthdateTextView.setText("Birthdate: " + currentUser.getBirthdate());
        holder.weightTextView.setText("Weight: " + currentUser.getWeight());
        holder.heightTextView.setText("Height: " + currentUser.getHeight());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<UserEntity> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView emailTextView;
        private TextView ageTextView;
        private TextView genderTextView;
        private TextView birthdateTextView;
        private TextView weightTextView;
        private TextView heightTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            birthdateTextView = itemView.findViewById(R.id.birthdateTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
        }
    }
}
