package com.group5.homework06;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> myData;

    PrettyTime prettyTime = new PrettyTime();

    public MessageAdapter(ArrayList<Message> myData) {
        this.myData = myData;
    }

    @Override
    public int getItemViewType(int position) {
  
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final int index = i;

        final View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);



        TextView messageTextView = view.findViewById(R.id.message_text_in_item_list_textView);
        ImageView messageImageView = view.findViewById(R.id.message_image_in_item_list_imageView);
        TextView firstNameTextView = view.findViewById(R.id.firstName_in_item_list_textView);
        TextView timeTextView = view.findViewById(R.id.time_in_item_list_textView);
        ImageView deleteImageViewButton = view.findViewById(R.id.delete_message_imageView_button);

 
        final Message message = myData.get(index);

        messageTextView.setText(message.messageText);
        firstNameTextView.setText(message.firstName);
        timeTextView.setText(prettyTime.format(message.dateTime));

        if(message.imageUrl == null) {
            messageImageView.setImageBitmap(null);
        } else {
            Picasso.get().load(message.imageUrl).placeholder(R.drawable.loading).into(messageImageView);
        }

        deleteImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myData.remove(index);
                notifyDataSetChanged();
                MainActivity mainActivity = (MainActivity) view.getContext();
                mainActivity.myRef.setValue(myData);
            }
        });

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final Message message = myData.get(position);
        viewHolder.messageTextView.setText(message.messageText);
        viewHolder.firstNameTextView.setText(message.firstName);
        viewHolder.timeTextView.setText(prettyTime.format(message.dateTime));

        if(message.imageUrl == null) {
            viewHolder.messageImageView.setImageBitmap(null);
        } else {
            Picasso.get().load(message.imageUrl).placeholder(R.drawable.loading).into(viewHolder.messageImageView);
        }

        viewHolder.deleteImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myData.remove(position);
                notifyDataSetChanged();
                MainActivity mainActivity = (MainActivity) viewHolder.deleteImageViewButton.getContext();
                mainActivity.myRef.setValue(myData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView, firstNameTextView, timeTextView;
        ImageView messageImageView, deleteImageViewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.message_text_in_item_list_textView);
            firstNameTextView = itemView.findViewById(R.id.firstName_in_item_list_textView);
            timeTextView = itemView.findViewById(R.id.time_in_item_list_textView);
            messageImageView = itemView.findViewById(R.id.message_image_in_item_list_imageView);
            deleteImageViewButton = itemView.findViewById(R.id.delete_message_imageView_button);
        }
    }
}