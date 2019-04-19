package com.example.mongohack;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TopicViewHolder extends RecyclerView.ViewHolder {

    TextView topic_name;
    TextView topic_number;
    TextView topic_count;

    TopicViewHolder(View itemView){
        super(itemView);

        topic_name = itemView.findViewById(R.id.topic_name);
        topic_count = itemView.findViewById(R.id.topic_count);
        topic_number = itemView.findViewById(R.id.topic_number);

    }
}
