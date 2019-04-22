package com.example.mongohack;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.bson.Document;

import java.util.List;

public class TopicListAdapter extends RecyclerView.Adapter<TopicViewHolder> {

    Context context;
    List<Document> list;

    public TopicListAdapter(List<Document> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.topic_list,
                viewGroup, false);

        TopicViewHolder viewHolder = new TopicViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder topicViewHolder, int i) {
        topicViewHolder.topic_name.setText(list.get(i).getString("topic_name"));
        topicViewHolder.topic_count.setText("+" + list.get(i).getInteger("topic_count"));
        topicViewHolder.topic_number.setText(i+1 +".");

        topicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context.getApplicationContext(), HashtagActivity.class);
                intent.putExtra("hashtagId", list.get(i).getObjectId("_id").toString());
                intent.putExtra("hashtagName", list.get(i).getString("topic_name"));
                context.startActivity(intent);

            }
        });
    }

}
