package com.example.mongohack;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView user_name;
    TextView user_message;
    TextView message_date;
    TextView message_time;
    ImageView image_message_profile;

    MessageViewHolder(View itemView){
        super(itemView);

        user_name = itemView.findViewById(R.id.user_name);
        user_message = itemView.findViewById(R.id.user_message);
        message_time = itemView.findViewById(R.id.message_time);
        message_date = itemView.findViewById(R.id.message_date);
        image_message_profile = itemView.findViewById(R.id.image_message_profile);
    }
}
