package com.seproject.questionmanagement.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seproject.questionmanagement.R;

import java.util.ArrayList;

public class ListApplicationRecyclerAdapter extends RecyclerView.Adapter<ListApplicationRecyclerAdapter.ApplicationHolder> {

    private ArrayList<String> applicationUsernameList;
    private ArrayList<String> applicationEmailList;
    private ArrayList<String> applicationTcnoList;
    private ArrayList<String> applicationBirthdateList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onApproveClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public ListApplicationRecyclerAdapter(ArrayList<String> applicationUsernameList, ArrayList<String> applicationEmailList, ArrayList<String> applicationTcnoList, ArrayList<String> applicationBirthdateList) {
        this.applicationUsernameList = applicationUsernameList;
        this.applicationEmailList = applicationEmailList;
        this.applicationTcnoList = applicationTcnoList;
        this.applicationBirthdateList = applicationBirthdateList;
    }

    @NonNull
    @Override
    public ApplicationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row_admin_user_applications, parent, false);
        ApplicationHolder applicationHolder = new ApplicationHolder(view, itemClickListener);
        return applicationHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationHolder holder, int position) {

        holder.usernameText.setText("Username: " + applicationUsernameList.get(position));
        holder.emailText.setText("Email: " + applicationEmailList.get(position));
        holder.tcnoText.setText("Tc No: " + applicationTcnoList.get(position));
        holder.birthdateText.setText("Birthdate: " + applicationBirthdateList.get(position));

    }

    @Override
    public int getItemCount() {
        return applicationUsernameList.size();
    }

    class ApplicationHolder extends RecyclerView.ViewHolder{

        TextView usernameText, emailText, tcnoText, birthdateText;
        Button approveButton;

        public ApplicationHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            usernameText = itemView.findViewById(R.id.recyclerview_userApplications_username_text);
            emailText = itemView.findViewById(R.id.recyclerview_userApplications_email_text);
            tcnoText = itemView.findViewById(R.id.recyclerview_userApplications_tcno_text);
            birthdateText = itemView.findViewById(R.id.recyclerview_userApplications_birthdate_text);
            approveButton = itemView.findViewById(R.id.approveButton);

            approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onApproveClicked(position);
                        }
                    }
                }
            });

        }
    }
}
