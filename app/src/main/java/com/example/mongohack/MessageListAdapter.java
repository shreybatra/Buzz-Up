package com.example.mongohack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bson.Document;

import java.lang.annotation.Documented;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    List<Document> list = Collections.emptyList();

    Context context;

    public MessageListAdapter(List<Document> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.topic_message,
                viewGroup, false);

        MessageViewHolder viewHolder = new MessageViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int i) {
        viewHolder.user_name.setText(list.get(i).getString("user_name"));
        viewHolder.user_message.setText(list.get(i).getString("message"));
        Date messageDate =  list.get(i).getDate("created_at");
        viewHolder.message_time.setText(DateFormat.format("K:mm a", messageDate).toString());
        viewHolder.message_date.setText(DateFormat.format("dd", messageDate).toString() + " " + DateFormat.format("MMM", messageDate).toString() + ", " + DateFormat.format("yyyy", messageDate).toString());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}